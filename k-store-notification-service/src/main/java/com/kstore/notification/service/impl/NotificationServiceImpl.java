package com.kstore.notification.service.impl;

import com.kstore.common.exception.ResourceNotFoundException;
import com.kstore.notification.dto.BulkNotificationRequest;
import com.kstore.notification.dto.NotificationRequest;
import com.kstore.notification.dto.NotificationResponse;
import com.kstore.notification.entity.Notification;
import com.kstore.notification.entity.NotificationTemplate;
import com.kstore.notification.repository.NotificationRepository;
import com.kstore.notification.repository.NotificationTemplateRepository;
import com.kstore.notification.service.NotificationChannelService;
import com.kstore.notification.service.NotificationService;
import com.kstore.notification.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository templateRepository;
    private final List<NotificationChannelService> channelServices;
    private final TemplateService templateService;

    @Override
    public NotificationResponse createNotification(NotificationRequest request) {
        Notification notification = buildNotificationFromRequest(request);
        notification = notificationRepository.save(notification);
        
        log.info("Created notification with ID: {} for user: {}", notification.getId(), notification.getUserId());
        return mapToResponse(notification);
    }

    @Override
    @Async("notificationTaskExecutor")
    public CompletableFuture<NotificationResponse> sendNotificationAsync(Long notificationId) {
        try {
            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

            if (notification.getStatus() != Notification.NotificationStatus.PENDING) {
                log.warn("Cannot send notification {} - status is {}", notificationId, notification.getStatus());
                return CompletableFuture.completedFuture(mapToResponse(notification));
            }

            notification.setStatus(Notification.NotificationStatus.PROCESSING);
            notification.setDeliveryAttempts(notification.getDeliveryAttempts() + 1);
            notification = notificationRepository.save(notification);

            NotificationChannelService channelService = getChannelService(notification.getChannel());
            if (channelService == null) {
                log.error("No channel service found for: {}", notification.getChannel());
                notification.setStatus(Notification.NotificationStatus.FAILED);
                notification.setErrorMessage("No channel service available");
                notification = notificationRepository.save(notification);
                return CompletableFuture.completedFuture(mapToResponse(notification));
            }

            CompletableFuture<Boolean> sendResult = channelService.sendNotification(notification);
            Boolean success = sendResult.get();

            if (success) {
                notification.setStatus(Notification.NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());
                log.info("Notification {} sent successfully via {}", notificationId, notification.getChannel());
            } else {
                notification.setStatus(Notification.NotificationStatus.FAILED);
                notification.setErrorMessage("Failed to send via " + notification.getChannel());
                log.error("Failed to send notification {} via {}", notificationId, notification.getChannel());
            }

            notification = notificationRepository.save(notification);
            return CompletableFuture.completedFuture(mapToResponse(notification));

        } catch (Exception e) {
            log.error("Error processing notification {}", notificationId, e);
            Notification notification = notificationRepository.findById(notificationId).orElse(null);
            if (notification != null) {
                notification.setStatus(Notification.NotificationStatus.FAILED);
                notification.setErrorMessage(e.getMessage());
                notification = notificationRepository.save(notification);
                return CompletableFuture.completedFuture(mapToResponse(notification));
            }
            throw new RuntimeException("Failed to process notification", e);
        }
    }

    @Override
    @Async("notificationTaskExecutor")
    public CompletableFuture<List<NotificationResponse>> sendBulkNotificationsAsync(BulkNotificationRequest request) {
        List<NotificationResponse> responses = new ArrayList<>();
        
        try {
            NotificationTemplate template = templateRepository.findByName(request.getTemplateName())
                    .orElseThrow(() -> new ResourceNotFoundException("Template not found: " + request.getTemplateName()));

            List<CompletableFuture<NotificationResponse>> futures = new ArrayList<>();

            for (BulkNotificationRequest.RecipientData recipient : request.getRecipients()) {
                Map<String, String> mergedParameters = new HashMap<>();
                if (request.getGlobalParameters() != null) {
                    mergedParameters.putAll(request.getGlobalParameters());
                }
                if (recipient.getPersonalizedParameters() != null) {
                    mergedParameters.putAll(recipient.getPersonalizedParameters());
                }

                String processedSubject = templateService.processTemplate(template.getSubjectTemplate(), mergedParameters);
                String processedContent = templateService.processTemplate(template.getContentTemplate(), mergedParameters);

                NotificationRequest notificationRequest = NotificationRequest.builder()
                        .userId(recipient.getUserId())
                        .recipient(recipient.getRecipient())
                        .type(template.getType())
                        .channel(template.getChannel())
                        .subject(processedSubject)
                        .content(processedContent)
                        .templateName(template.getName())
                        .parameters(mergedParameters)
                        .priority(2) // Medium priority for bulk
                        .build();

                NotificationResponse notification = createNotification(notificationRequest);
                futures.add(sendNotificationAsync(notification.getId()));
            }

            // Wait for all notifications to complete
            for (CompletableFuture<NotificationResponse> future : futures) {
                responses.add(future.get());
            }

            log.info("Bulk notification completed. Sent {} notifications", responses.size());
            return CompletableFuture.completedFuture(responses);

        } catch (Exception e) {
            log.error("Error processing bulk notifications", e);
            throw new RuntimeException("Failed to process bulk notifications", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        return mapToResponse(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotificationsByUserId(Long userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getAllNotifications(Pageable pageable) {
        return notificationRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotificationsByStatus(Notification.NotificationStatus status, Pageable pageable) {
        return notificationRepository.findByStatus(status, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotificationsByChannel(Notification.NotificationChannel channel, Pageable pageable) {
        return notificationRepository.findByChannel(channel, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public NotificationResponse updateNotificationStatus(Long id, Notification.NotificationStatus status) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        notification.setStatus(status);
        if (status == Notification.NotificationStatus.SENT) {
            notification.setSentAt(LocalDateTime.now());
        }

        notification = notificationRepository.save(notification);
        return mapToResponse(notification);
    }

    @Override
    @Scheduled(fixedDelay = 30000) // Run every 30 seconds
    public void processScheduledNotifications() {
        List<Notification> pendingNotifications = notificationRepository
                .findPendingNotifications(Notification.NotificationStatus.PENDING, LocalDateTime.now());

        log.info("Processing {} scheduled notifications", pendingNotifications.size());

        for (Notification notification : pendingNotifications) {
            try {
                sendNotificationAsync(notification.getId());
            } catch (Exception e) {
                log.error("Error processing scheduled notification {}", notification.getId(), e);
            }
        }
    }

    @Override
    @Scheduled(fixedDelay = 300000) // Run every 5 minutes
    public void retryFailedNotifications() {
        List<Notification> failedNotifications = notificationRepository
                .findFailedNotificationsForRetry(Notification.NotificationStatus.FAILED);

        log.info("Retrying {} failed notifications", failedNotifications.size());

        for (Notification notification : failedNotifications) {
            try {
                sendNotificationAsync(notification.getId());
            } catch (Exception e) {
                log.error("Error retrying failed notification {}", notification.getId(), e);
            }
        }
    }

    @Override
    public void cancelNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        if (notification.getStatus() == Notification.NotificationStatus.PENDING ||
            notification.getStatus() == Notification.NotificationStatus.PROCESSING) {
            notification.setStatus(Notification.NotificationStatus.CANCELLED);
            notificationRepository.save(notification);
            log.info("Notification {} cancelled", id);
        } else {
            throw new IllegalStateException("Cannot cancel notification with status: " + notification.getStatus());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadNotificationCount(Long userId) {
        return notificationRepository.countByUserIdAndStatus(userId, Notification.NotificationStatus.SENT);
    }

    private Notification buildNotificationFromRequest(NotificationRequest request) {
        Notification.NotificationBuilder builder = Notification.builder()
                .userId(request.getUserId())
                .recipient(request.getRecipient())
                .type(request.getType())
                .channel(request.getChannel())
                .priority(request.getPriority())
                .maxAttempts(request.getMaxAttempts())
                .parameters(request.getParameters());

        if (request.getTemplateName() != null) {
            NotificationTemplate template = templateRepository.findByName(request.getTemplateName())
                    .orElseThrow(() -> new ResourceNotFoundException("Template not found: " + request.getTemplateName()));

            Map<String, String> parameters = request.getParameters() != null ? request.getParameters() : new HashMap<>();
            String processedSubject = templateService.processTemplate(template.getSubjectTemplate(), parameters);
            String processedContent = templateService.processTemplate(template.getContentTemplate(), parameters);

            builder.subject(processedSubject)
                   .content(processedContent)
                   .templateName(request.getTemplateName());
        } else {
            builder.subject(request.getSubject())
                   .content(request.getContent());
        }

        if (request.getScheduledAt() != null) {
            builder.scheduledAt(request.getScheduledAt());
        } else {
            builder.scheduledAt(LocalDateTime.now());
        }

        return builder.build();
    }

    private NotificationChannelService getChannelService(Notification.NotificationChannel channel) {
        return channelServices.stream()
                .filter(service -> service.supportsChannel(channel))
                .findFirst()
                .orElse(null);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .recipient(notification.getRecipient())
                .type(notification.getType())
                .channel(notification.getChannel())
                .subject(notification.getSubject())
                .content(notification.getContent())
                .templateName(notification.getTemplateName())
                .parameters(notification.getParameters())
                .status(notification.getStatus())
                .priority(notification.getPriority())
                .scheduledAt(notification.getScheduledAt())
                .sentAt(notification.getSentAt())
                .deliveryAttempts(notification.getDeliveryAttempts())
                .maxAttempts(notification.getMaxAttempts())
                .errorMessage(notification.getErrorMessage())
                .externalMessageId(notification.getExternalMessageId())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }
}
