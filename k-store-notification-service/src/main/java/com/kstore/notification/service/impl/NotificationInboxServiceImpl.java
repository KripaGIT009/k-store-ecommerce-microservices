package com.kstore.notification.service.impl;

import com.kstore.notification.entity.NotificationInbox;
import com.kstore.notification.entity.Notification;
import com.kstore.notification.repository.NotificationInboxRepository;
import com.kstore.notification.service.NotificationInboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationInboxServiceImpl implements NotificationInboxService {

    private final NotificationInboxRepository inboxRepository;

    @Override
    public NotificationInbox saveToInbox(Notification notification) {
        log.debug("Saving notification to inbox for user: {}", notification.getUserId());

        // Map notification priority to inbox priority
        NotificationInbox.Priority inboxPriority = mapPriorityToInboxPriority(notification.getPriority());

        // Calculate expiration time (e.g., 30 days for regular notifications, 7 days for promotional)
        LocalDateTime expiresAt = calculateExpirationTime(notification.getType());

        NotificationInbox inboxNotification = NotificationInbox.builder()
                .userId(notification.getUserId())
                .notificationId(notification.getId())
                .title(notification.getSubject())
                .message(notification.getContent())
                .type(notification.getType())
                .priority(inboxPriority)
                .expiresAt(expiresAt)
                .build();

        NotificationInbox saved = inboxRepository.save(inboxNotification);
        log.info("Notification saved to inbox with ID: {} for user: {}", saved.getId(), notification.getUserId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationInbox> getUserNotifications(Long userId, Pageable pageable) {
        return inboxRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationInbox> getUnreadNotifications(Long userId, Pageable pageable) {
        return inboxRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationInbox> getNotificationsByType(Long userId, Notification.NotificationType type, Pageable pageable) {
        return inboxRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationInbox> getNotificationsByPriority(Long userId, NotificationInbox.Priority priority, Pageable pageable) {
        return inboxRepository.findByUserIdAndPriorityOrderByCreatedAtDesc(userId, priority, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationInbox> getActiveNotifications(Long userId, Pageable pageable) {
        return inboxRepository.findByUserIdAndIsArchivedFalseOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotificationInbox> getNotificationById(Long id, Long userId) {
        return inboxRepository.findById(id)
                .filter(notification -> notification.getUserId().equals(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadCount(Long userId) {
        return inboxRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    public boolean markAsRead(Long id, Long userId) {
        int updated = inboxRepository.markAsRead(id, userId, LocalDateTime.now());
        boolean success = updated > 0;
        if (success) {
            log.debug("Marked notification {} as read for user: {}", id, userId);
        }
        return success;
    }

    @Override
    public int markAllAsRead(Long userId) {
        int updated = inboxRepository.markAllAsRead(userId, LocalDateTime.now());
        log.info("Marked {} notifications as read for user: {}", updated, userId);
        return updated;
    }

    @Override
    public boolean archiveNotification(Long id, Long userId) {
        int updated = inboxRepository.archiveNotification(id, userId, LocalDateTime.now());
        boolean success = updated > 0;
        if (success) {
            log.debug("Archived notification {} for user: {}", id, userId);
        }
        return success;
    }

    @Override
    public boolean deleteNotification(Long id, Long userId) {
        Optional<NotificationInbox> notification = getNotificationById(id, userId);
        if (notification.isPresent()) {
            inboxRepository.deleteById(id);
            log.debug("Deleted notification {} for user: {}", id, userId);
            return true;
        }
        return false;
    }

    @Override
    public int cleanupExpiredNotifications() {
        int deleted = inboxRepository.deleteExpiredNotifications(LocalDateTime.now());
        if (deleted > 0) {
            log.info("Cleaned up {} expired notifications", deleted);
        }
        return deleted;
    }

    private NotificationInbox.Priority mapPriorityToInboxPriority(Integer priority) {
        if (priority == null) return NotificationInbox.Priority.MEDIUM;
        
        return switch (priority) {
            case 1 -> NotificationInbox.Priority.LOW;
            case 2 -> NotificationInbox.Priority.MEDIUM;
            case 3 -> NotificationInbox.Priority.HIGH;
            case 4 -> NotificationInbox.Priority.CRITICAL;
            default -> NotificationInbox.Priority.MEDIUM;
        };
    }

    private LocalDateTime calculateExpirationTime(Notification.NotificationType type) {
        LocalDateTime now = LocalDateTime.now();
        
        return switch (type) {
            case PROMOTIONAL -> now.plusDays(7);  // Promotional notifications expire in 7 days
            case SYSTEM_ALERT, SYSTEM -> now.plusDays(3);  // System alerts expire in 3 days
            case ORDER_CONFIRMATION, ORDER_SHIPPED, ORDER_DELIVERED, 
                 PAYMENT_SUCCESSFUL, PAYMENT_FAILED -> now.plusDays(90);  // Order/payment notifications expire in 90 days
            case PASSWORD_RESET, ACCOUNT_VERIFICATION -> now.plusDays(1);  // Security notifications expire in 1 day
            default -> now.plusDays(30);  // Default: 30 days
        };
    }
}
