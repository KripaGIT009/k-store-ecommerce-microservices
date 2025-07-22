package com.kstore.notification.service;

import com.kstore.notification.event.BulkNotificationEvent;
import com.kstore.notification.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CompletableFuture<SendResult<String, Object>> publishNotificationEvent(NotificationEvent event) {
        log.info("Publishing notification event: {} to topic: notification-events", event.getEventId());
        
        return kafkaTemplate.send("notification-events", event.getEventId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Successfully published notification event: {} with offset: {}", 
                                event.getEventId(), result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to publish notification event: {}", event.getEventId(), ex);
                    }
                });
    }

    public CompletableFuture<SendResult<String, Object>> publishBulkNotificationEvent(BulkNotificationEvent event) {
        log.info("Publishing bulk notification event: {} to topic: bulk-notification-events", event.getEventId());
        
        return kafkaTemplate.send("bulk-notification-events", event.getEventId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Successfully published bulk notification event: {} with offset: {}", 
                                event.getEventId(), result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to publish bulk notification event: {}", event.getEventId(), ex);
                    }
                });
    }

    public CompletableFuture<SendResult<String, Object>> publishUserEvent(String eventType, Object eventData) {
        log.info("Publishing user event: {} to topic: user-events", eventType);
        
        return kafkaTemplate.send("user-events", eventType, eventData)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Successfully published user event: {} with offset: {}", 
                                eventType, result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to publish user event: {}", eventType, ex);
                    }
                });
    }

    public CompletableFuture<SendResult<String, Object>> publishOrderEvent(String eventType, Object eventData) {
        log.info("Publishing order event: {} to topic: order-events", eventType);
        
        return kafkaTemplate.send("order-events", eventType, eventData)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Successfully published order event: {} with offset: {}", 
                                eventType, result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to publish order event: {}", eventType, ex);
                    }
                });
    }

    public CompletableFuture<SendResult<String, Object>> publishPaymentEvent(String eventType, Object eventData) {
        log.info("Publishing payment event: {} to topic: payment-events", eventType);
        
        return kafkaTemplate.send("payment-events", eventType, eventData)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Successfully published payment event: {} with offset: {}", 
                                eventType, result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to publish payment event: {}", eventType, ex);
                    }
                });
    }

    public CompletableFuture<SendResult<String, Object>> publishProductEvent(String eventType, Object eventData) {
        log.info("Publishing product event: {} to topic: product-events", eventType);
        
        return kafkaTemplate.send("product-events", eventType, eventData)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Successfully published product event: {} with offset: {}", 
                                eventType, result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to publish product event: {}", eventType, ex);
                    }
                });
    }
}
