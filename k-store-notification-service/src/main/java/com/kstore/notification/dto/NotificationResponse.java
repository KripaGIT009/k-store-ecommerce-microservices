package com.kstore.notification.dto;

import com.kstore.notification.entity.Notification;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;
    private Long userId;
    private String recipient;
    private Notification.NotificationType type;
    private Notification.NotificationChannel channel;
    private String subject;
    private String content;
    private String templateName;
    private Map<String, String> parameters;
    private Notification.NotificationStatus status;
    private Integer priority;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private Integer deliveryAttempts;
    private Integer maxAttempts;
    private String errorMessage;
    private String externalMessageId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
