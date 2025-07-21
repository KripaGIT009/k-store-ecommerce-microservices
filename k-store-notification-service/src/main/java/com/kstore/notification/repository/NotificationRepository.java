package com.kstore.notification.repository;

import com.kstore.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserId(Long userId, Pageable pageable);

    Page<Notification> findByStatus(Notification.NotificationStatus status, Pageable pageable);

    Page<Notification> findByChannel(Notification.NotificationChannel channel, Pageable pageable);

    Page<Notification> findByType(Notification.NotificationType type, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.status = :status AND n.scheduledAt <= :now ORDER BY n.priority DESC, n.scheduledAt ASC")
    List<Notification> findPendingNotifications(@Param("status") Notification.NotificationStatus status, 
                                                @Param("now") LocalDateTime now);

    @Query("SELECT n FROM Notification n WHERE n.status = :status AND n.deliveryAttempts < n.maxAttempts ORDER BY n.priority DESC")
    List<Notification> findFailedNotificationsForRetry(@Param("status") Notification.NotificationStatus status);

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.status = :status")
    Page<Notification> findByUserIdAndStatus(@Param("userId") Long userId, 
                                            @Param("status") Notification.NotificationStatus status, 
                                            Pageable pageable);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, 
                               @Param("status") Notification.NotificationStatus status);

    @Query("SELECT n FROM Notification n WHERE n.createdAt BETWEEN :startDate AND :endDate ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsByDateRange(@Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT n FROM Notification n WHERE n.externalMessageId = :externalId")
    List<Notification> findByExternalMessageId(@Param("externalId") String externalMessageId);
}
