package com.kstore.notification.controller;

import com.kstore.notification.event.BulkNotificationEvent;
import com.kstore.notification.event.NotificationEvent;
import com.kstore.notification.service.NotificationEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Slf4j
public class NotificationEventController {

    private final NotificationEventProducer eventProducer;

    @PostMapping("/notification")
    public ResponseEntity<String> publishNotificationEvent(@RequestBody NotificationEvent event) {
        try {
            if (event.getEventId() == null) {
                event.setEventId(UUID.randomUUID().toString());
            }
            if (event.getCreatedAt() == null) {
                event.setCreatedAt(LocalDateTime.now());
            }
            
            eventProducer.publishNotificationEvent(event);
            return ResponseEntity.ok("Event published successfully: " + event.getEventId());
        } catch (Exception e) {
            log.error("Error publishing notification event", e);
            return ResponseEntity.internalServerError().body("Failed to publish event");
        }
    }

    @PostMapping("/bulk-notification")
    public ResponseEntity<String> publishBulkNotificationEvent(@RequestBody BulkNotificationEvent event) {
        try {
            if (event.getEventId() == null) {
                event.setEventId(UUID.randomUUID().toString());
            }
            if (event.getCreatedAt() == null) {
                event.setCreatedAt(LocalDateTime.now());
            }
            
            eventProducer.publishBulkNotificationEvent(event);
            return ResponseEntity.ok("Bulk event published successfully: " + event.getEventId());
        } catch (Exception e) {
            log.error("Error publishing bulk notification event", e);
            return ResponseEntity.internalServerError().body("Failed to publish bulk event");
        }
    }

    @PostMapping("/user/{eventType}")
    public ResponseEntity<String> publishUserEvent(
            @PathVariable String eventType,
            @RequestBody Map<String, Object> eventData) {
        try {
            eventProducer.publishUserEvent(eventType, eventData);
            return ResponseEntity.ok("User event published successfully: " + eventType);
        } catch (Exception e) {
            log.error("Error publishing user event", e);
            return ResponseEntity.internalServerError().body("Failed to publish user event");
        }
    }

    @PostMapping("/order/{eventType}")
    public ResponseEntity<String> publishOrderEvent(
            @PathVariable String eventType,
            @RequestBody Map<String, Object> eventData) {
        try {
            eventProducer.publishOrderEvent(eventType, eventData);
            return ResponseEntity.ok("Order event published successfully: " + eventType);
        } catch (Exception e) {
            log.error("Error publishing order event", e);
            return ResponseEntity.internalServerError().body("Failed to publish order event");
        }
    }

    @PostMapping("/payment/{eventType}")
    public ResponseEntity<String> publishPaymentEvent(
            @PathVariable String eventType,
            @RequestBody Map<String, Object> eventData) {
        try {
            eventProducer.publishPaymentEvent(eventType, eventData);
            return ResponseEntity.ok("Payment event published successfully: " + eventType);
        } catch (Exception e) {
            log.error("Error publishing payment event", e);
            return ResponseEntity.internalServerError().body("Failed to publish payment event");
        }
    }

    @PostMapping("/product/{eventType}")
    public ResponseEntity<String> publishProductEvent(
            @PathVariable String eventType,
            @RequestBody Map<String, Object> eventData) {
        try {
            eventProducer.publishProductEvent(eventType, eventData);
            return ResponseEntity.ok("Product event published successfully: " + eventType);
        } catch (Exception e) {
            log.error("Error publishing product event", e);
            return ResponseEntity.internalServerError().body("Failed to publish product event");
        }
    }
}
