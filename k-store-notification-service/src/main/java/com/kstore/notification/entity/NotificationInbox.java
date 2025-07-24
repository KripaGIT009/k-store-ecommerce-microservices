package com.kstore.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_inbox")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class NotificationInbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Notification.NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    @Builder.Default
    private Priority priority = Priority.MEDIUM;

    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;

    @Column(name = "is_archived")
    @Builder.Default
    private Boolean isArchived = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Priority {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    // Helper methods
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    public void archive() {
        this.isArchived = true;
        this.archivedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}
