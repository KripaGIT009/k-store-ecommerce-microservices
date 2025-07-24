package com.kstore.notification.controller;

import com.kstore.common.dto.ApiResponse;
import com.kstore.notification.dto.NotificationInboxResponse;
import com.kstore.notification.entity.NotificationInbox;
import com.kstore.notification.entity.Notification;
import com.kstore.notification.service.NotificationInboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/notifications/inbox")
@RequiredArgsConstructor
@Slf4j
public class NotificationInboxController {

    private final NotificationInboxService inboxService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<NotificationInboxResponse>>> getUserNotifications(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<NotificationInbox> notifications = inboxService.getUserNotifications(userId, pageable);
        Page<NotificationInboxResponse> response = notifications.map(NotificationInboxResponse::from);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<ApiResponse<Page<NotificationInboxResponse>>> getUnreadNotifications(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<NotificationInbox> notifications = inboxService.getUnreadNotifications(userId, pageable);
        Page<NotificationInboxResponse> response = notifications.map(NotificationInboxResponse::from);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<ApiResponse<Page<NotificationInboxResponse>>> getActiveNotifications(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<NotificationInbox> notifications = inboxService.getActiveNotifications(userId, pageable);
        Page<NotificationInboxResponse> response = notifications.map(NotificationInboxResponse::from);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<ApiResponse<Page<NotificationInboxResponse>>> getNotificationsByType(
            @PathVariable Long userId,
            @PathVariable Notification.NotificationType type,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<NotificationInbox> notifications = inboxService.getNotificationsByType(userId, type, pageable);
        Page<NotificationInboxResponse> response = notifications.map(NotificationInboxResponse::from);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}/priority/{priority}")
    public ResponseEntity<ApiResponse<Page<NotificationInboxResponse>>> getNotificationsByPriority(
            @PathVariable Long userId,
            @PathVariable NotificationInbox.Priority priority,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<NotificationInbox> notifications = inboxService.getNotificationsByPriority(userId, priority, pageable);
        Page<NotificationInboxResponse> response = notifications.map(NotificationInboxResponse::from);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/user/{userId}")
    public ResponseEntity<ApiResponse<NotificationInboxResponse>> getNotificationById(
            @PathVariable Long id,
            @PathVariable Long userId) {
        
        Optional<NotificationInbox> notification = inboxService.getNotificationById(id, userId);
        if (notification.isPresent()) {
            NotificationInboxResponse response = NotificationInboxResponse.from(notification.get());
            return ResponseEntity.ok(ApiResponse.success(response));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@PathVariable Long userId) {
        Long count = inboxService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @PutMapping("/{id}/user/{userId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id,
            @PathVariable Long userId) {
        
        boolean success = inboxService.markAsRead(id, userId);
        if (success) {
            return ResponseEntity.ok(ApiResponse.success(null, "Notification marked as read"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<ApiResponse<Integer>> markAllAsRead(@PathVariable Long userId) {
        int updated = inboxService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success(updated, updated + " notifications marked as read"));
    }

    @PutMapping("/{id}/user/{userId}/archive")
    public ResponseEntity<ApiResponse<Void>> archiveNotification(
            @PathVariable Long id,
            @PathVariable Long userId) {
        
        boolean success = inboxService.archiveNotification(id, userId);
        if (success) {
            return ResponseEntity.ok(ApiResponse.success(null, "Notification archived"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/user/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @PathVariable Long id,
            @PathVariable Long userId) {
        
        boolean success = inboxService.deleteNotification(id, userId);
        if (success) {
            return ResponseEntity.ok(ApiResponse.success(null, "Notification deleted"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
