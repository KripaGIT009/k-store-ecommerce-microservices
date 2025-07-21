package com.kstore.notification.controller;

import com.kstore.notification.dto.BulkNotificationRequest;
import com.kstore.notification.dto.NotificationRequest;
import com.kstore.notification.dto.NotificationResponse;
import com.kstore.notification.entity.Notification;
import com.kstore.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(@Valid @RequestBody NotificationRequest request) {
        log.info("Creating notification for user: {}", request.getUserId());
        NotificationResponse response = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<NotificationResponse> sendNotification(@PathVariable Long id) {
        log.info("Sending notification: {}", id);
        CompletableFuture<NotificationResponse> future = notificationService.sendNotificationAsync(id);
        
        try {
            NotificationResponse response = future.get();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sending notification {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<NotificationResponse>> sendBulkNotifications(@Valid @RequestBody BulkNotificationRequest request) {
        log.info("Sending bulk notifications for template: {}", request.getTemplateName());
        CompletableFuture<List<NotificationResponse>> future = notificationService.sendBulkNotificationsAsync(request);
        
        try {
            List<NotificationResponse> responses = future.get();
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error sending bulk notifications", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable Long id) {
        NotificationResponse response = notificationService.getNotificationById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<NotificationResponse>> getNotificationsByUserId(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<NotificationResponse> notifications = notificationService.getNotificationsByUserId(userId, pageable);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getAllNotifications(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<NotificationResponse> notifications = notificationService.getAllNotifications(pageable);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<NotificationResponse>> getNotificationsByStatus(
            @PathVariable Notification.NotificationStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<NotificationResponse> notifications = notificationService.getNotificationsByStatus(status, pageable);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/channel/{channel}")
    public ResponseEntity<Page<NotificationResponse>> getNotificationsByChannel(
            @PathVariable Notification.NotificationChannel channel,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<NotificationResponse> notifications = notificationService.getNotificationsByChannel(channel, pageable);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<NotificationResponse> updateNotificationStatus(
            @PathVariable Long id,
            @RequestParam Notification.NotificationStatus status) {
        NotificationResponse response = notificationService.updateNotificationStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelNotification(@PathVariable Long id) {
        notificationService.cancelNotification(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadNotificationCount(@PathVariable Long userId) {
        Long count = notificationService.getUnreadNotificationCount(userId);
        return ResponseEntity.ok(count);
    }
}
