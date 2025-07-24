package com.kstore.notification.repository;

import com.kstore.notification.entity.NotificationInbox;
import com.kstore.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationInboxRepository extends JpaRepository<NotificationInbox, Long> {

    // Find all notifications for a user
    Page<NotificationInbox> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Find unread notifications for a user
    Page<NotificationInbox> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Find notifications by type for a user
    Page<NotificationInbox> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, Notification.NotificationType type, Pageable pageable);

    // Find notifications by priority for a user
    Page<NotificationInbox> findByUserIdAndPriorityOrderByCreatedAtDesc(Long userId, NotificationInbox.Priority priority, Pageable pageable);

    // Find non-archived notifications for a user
    Page<NotificationInbox> findByUserIdAndIsArchivedFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Count unread notifications for a user
    Long countByUserIdAndIsReadFalse(Long userId);

    // Find expired notifications
    @Query("SELECT ni FROM NotificationInbox ni WHERE ni.expiresAt IS NOT NULL AND ni.expiresAt < :currentTime")
    List<NotificationInbox> findExpiredNotifications(@Param("currentTime") LocalDateTime currentTime);

    // Mark notification as read
    @Modifying
    @Query("UPDATE NotificationInbox ni SET ni.isRead = true, ni.readAt = :readTime WHERE ni.id = :id AND ni.userId = :userId")
    int markAsRead(@Param("id") Long id, @Param("userId") Long userId, @Param("readTime") LocalDateTime readTime);

    // Mark all notifications as read for a user
    @Modifying
    @Query("UPDATE NotificationInbox ni SET ni.isRead = true, ni.readAt = :readTime WHERE ni.userId = :userId AND ni.isRead = false")
    int markAllAsRead(@Param("userId") Long userId, @Param("readTime") LocalDateTime readTime);

    // Archive notification
    @Modifying
    @Query("UPDATE NotificationInbox ni SET ni.isArchived = true, ni.archivedAt = :archiveTime WHERE ni.id = :id AND ni.userId = :userId")
    int archiveNotification(@Param("id") Long id, @Param("userId") Long userId, @Param("archiveTime") LocalDateTime archiveTime);

    // Delete expired notifications
    @Modifying
    @Query("DELETE FROM NotificationInbox ni WHERE ni.expiresAt IS NOT NULL AND ni.expiresAt < :currentTime")
    int deleteExpiredNotifications(@Param("currentTime") LocalDateTime currentTime);
}
