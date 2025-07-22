package com.kstore.notification.listener;

import com.kstore.notification.dto.NotificationRequest;
import com.kstore.notification.dto.BulkNotificationRequest;
import com.kstore.notification.entity.Notification;
import com.kstore.notification.event.NotificationEvent;
import com.kstore.notification.event.BulkNotificationEvent;
import com.kstore.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "notification-events", groupId = "notification-service")
    public void handleNotificationEvent(
            @Payload NotificationEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            log.info("Received notification event: {} from topic: {}, partition: {}, offset: {}", 
                    event.getEventType(), topic, partition, offset);

            NotificationRequest request = mapToNotificationRequest(event);
            
            // Create and send notification
            var notification = notificationService.createNotification(request);
            notificationService.sendNotificationAsync(notification.getId());
            
            log.info("Successfully processed notification event: {}", event.getEventId());
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            log.error("Error processing notification event: {}", event.getEventId(), e);
            // Handle error - could implement dead letter queue or retry logic
        }
    }

    @KafkaListener(topics = "bulk-notification-events", groupId = "notification-service")
    public void handleBulkNotificationEvent(
            @Payload BulkNotificationEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            log.info("Received bulk notification event from topic: {}, partition: {}, offset: {}", 
                    topic, partition, offset);

            BulkNotificationRequest request = mapToBulkNotificationRequest(event);
            
            // Process bulk notifications
            notificationService.sendBulkNotificationsAsync(request);
            
            log.info("Successfully processed bulk notification event: {}", event.getEventId());
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            log.error("Error processing bulk notification event: {}", event.getEventId(), e);
        }
    }

    @KafkaListener(topics = "user-events", groupId = "notification-service")
    public void handleUserEvent(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            Acknowledgment acknowledgment) {
        
        try {
            log.info("Received user event from topic: {}", topic);
            
            // Parse user event and create appropriate notification
            // This is a simplified example - you'd parse the actual user event
            if (message.contains("USER_REGISTERED")) {
                createWelcomeNotification(message);
            }
            
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            log.error("Error processing user event", e);
        }
    }

    @KafkaListener(topics = "order-events", groupId = "notification-service")
    public void handleOrderEvent(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            Acknowledgment acknowledgment) {
        
        try {
            log.info("Received order event from topic: {}", topic);
            
            // Parse order event and create appropriate notification
            if (message.contains("ORDER_CREATED")) {
                createOrderConfirmationNotification(message);
            } else if (message.contains("ORDER_SHIPPED")) {
                createOrderShippedNotification(message);
            }
            
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            log.error("Error processing order event", e);
        }
    }

    private NotificationRequest mapToNotificationRequest(NotificationEvent event) {
        return NotificationRequest.builder()
                .userId(event.getUserId())
                .recipient(event.getRecipient())
                .type(Notification.NotificationType.valueOf(event.getNotificationType()))
                .channel(Notification.NotificationChannel.valueOf(event.getChannel()))
                .subject(event.getSubject())
                .content(event.getContent())
                .templateName(event.getTemplateName())
                .parameters(event.getParameters())
                .priority(event.getPriority())
                .scheduledAt(event.getScheduledAt())
                .build();
    }

    private BulkNotificationRequest mapToBulkNotificationRequest(BulkNotificationEvent event) {
        List<BulkNotificationRequest.RecipientData> recipients = event.getRecipients().stream()
                .map(r -> BulkNotificationRequest.RecipientData.builder()
                        .userId(r.getUserId())
                        .recipient(r.getRecipient())
                        .personalizedParameters(r.getPersonalizedParameters())
                        .build())
                .collect(Collectors.toList());

        return BulkNotificationRequest.builder()
                .templateName(event.getTemplateName())
                .globalParameters(event.getGlobalParameters())
                .recipients(recipients)
                .build();
    }

    private void createWelcomeNotification(String userEvent) {
        // Extract user data from event and create welcome notification
        // This is a simplified implementation
        NotificationEvent event = NotificationEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("USER_WELCOME")
                .userId(1L) // Extract from event
                .recipient("user@example.com") // Extract from event
                .notificationType("WELCOME")
                .channel("EMAIL")
                .templateName("WELCOME_EMAIL")
                .priority(2)
                .createdAt(LocalDateTime.now())
                .build();

        handleNotificationEvent(event, "notification-events", 0, 0, null);
    }

    private void createOrderConfirmationNotification(String orderEvent) {
        // Extract order data from event and create order confirmation notification
        // This is a simplified implementation
        log.info("Creating order confirmation notification for event: {}", orderEvent);
    }

    private void createOrderShippedNotification(String orderEvent) {
        // Extract order data from event and create order shipped notification
        // This is a simplified implementation
        log.info("Creating order shipped notification for event: {}", orderEvent);
    }
}
