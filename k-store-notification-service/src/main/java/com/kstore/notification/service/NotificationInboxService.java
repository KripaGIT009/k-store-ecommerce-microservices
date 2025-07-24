package com.kstore.notification.service;

import com.kstore.notification.entity.NotificationInbox;
import com.kstore.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface NotificationInboxService {

    /**
     * Save a notification to user's inbox
     */
    NotificationInbox saveToInbox(Notification notification);

    /**
     * Get all notifications for a user
     */
    Page<NotificationInbox> getUserNotifications(Long userId, Pageable pageable);

    /**
     * Get unread notifications for a user
     */
    Page<NotificationInbox> getUnreadNotifications(Long userId, Pageable pageable);

    /**
     * Get notifications by type for a user
     */
    Page<NotificationInbox> getNotificationsByType(Long userId, Notification.NotificationType type, Pageable pageable);

    /**
     * Get notifications by priority for a user
     */
    Page<NotificationInbox> getNotificationsByPriority(Long userId, NotificationInbox.Priority priority, Pageable pageable);

    /**
     * Get non-archived notifications for a user
     */
    Page<NotificationInbox> getActiveNotifications(Long userId, Pageable pageable);

    /**
     * Get notification by ID for a specific user
     */
    Optional<NotificationInbox> getNotificationById(Long id, Long userId);

    /**
     * Count unread notifications for a user
     */
    Long getUnreadCount(Long userId);

    /**
     * Mark notification as read
     */
    boolean markAsRead(Long id, Long userId);

    /**
     * Mark all notifications as read for a user
     */
    int markAllAsRead(Long userId);

    /**
     * Archive a notification
     */
    boolean archiveNotification(Long id, Long userId);

    /**
     * Delete a notification
     */
    boolean deleteNotification(Long id, Long userId);

    /**
     * Clean up expired notifications
     */
    int cleanupExpiredNotifications();
}
