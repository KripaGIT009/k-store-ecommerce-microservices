package com.kstore.notification.service;

import com.kstore.notification.dto.NotificationRequest;
import com.kstore.notification.dto.NotificationResponse;
import com.kstore.notification.dto.BulkNotificationRequest;
import com.kstore.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface NotificationService {

    NotificationResponse createNotification(NotificationRequest request);

    CompletableFuture<NotificationResponse> sendNotificationAsync(Long notificationId);

    CompletableFuture<List<NotificationResponse>> sendBulkNotificationsAsync(BulkNotificationRequest request);

    NotificationResponse getNotificationById(Long id);

    Page<NotificationResponse> getNotificationsByUserId(Long userId, Pageable pageable);

    Page<NotificationResponse> getAllNotifications(Pageable pageable);

    Page<NotificationResponse> getNotificationsByStatus(Notification.NotificationStatus status, Pageable pageable);

    Page<NotificationResponse> getNotificationsByChannel(Notification.NotificationChannel channel, Pageable pageable);

    NotificationResponse updateNotificationStatus(Long id, Notification.NotificationStatus status);

    void processScheduledNotifications();

    void retryFailedNotifications();

    void cancelNotification(Long id);

    Long getUnreadNotificationCount(Long userId);
}
