package com.kstore.notification.dto;

import com.kstore.notification.entity.Notification;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Recipient is required")
    private String recipient;

    @NotNull(message = "Notification type is required")
    private Notification.NotificationType type;

    @NotNull(message = "Notification channel is required")
    private Notification.NotificationChannel channel;

    @Size(max = 200, message = "Subject cannot exceed 200 characters")
    private String subject;

    @Size(max = 5000, message = "Content cannot exceed 5000 characters")
    private String content;

    private String templateName;

    private Map<String, String> parameters;

    @Min(value = 1, message = "Priority must be between 1 and 4")
    @Max(value = 4, message = "Priority must be between 1 and 4")
    @Builder.Default
    private Integer priority = 1;

    private LocalDateTime scheduledAt;

    @Min(value = 1, message = "Max attempts must be at least 1")
    @Max(value = 10, message = "Max attempts cannot exceed 10")
    @Builder.Default
    private Integer maxAttempts = 3;
}
