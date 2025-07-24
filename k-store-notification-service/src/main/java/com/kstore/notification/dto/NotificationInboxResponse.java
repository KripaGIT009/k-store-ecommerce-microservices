package com.kstore.notification.dto;

import com.kstore.notification.entity.NotificationInbox;
import com.kstore.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationInboxResponse {

    private Long id;
    private Long userId;
    private String title;
    private String message;
    private Notification.NotificationType type;
    private NotificationInbox.Priority priority;
    private Boolean isRead;
    private Boolean isArchived;
    private LocalDateTime readAt;
    private LocalDateTime archivedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Factory method to convert from entity
    public static NotificationInboxResponse from(NotificationInbox inbox) {
        return NotificationInboxResponse.builder()
                .id(inbox.getId())
                .userId(inbox.getUserId())
                .title(inbox.getTitle())
                .message(inbox.getMessage())
                .type(inbox.getType())
                .priority(inbox.getPriority())
                .isRead(inbox.getIsRead())
                .isArchived(inbox.getIsArchived())
                .readAt(inbox.getReadAt())
                .archivedAt(inbox.getArchivedAt())
                .expiresAt(inbox.getExpiresAt())
                .createdAt(inbox.getCreatedAt())
                .updatedAt(inbox.getUpdatedAt())
                .build();
    }
}
