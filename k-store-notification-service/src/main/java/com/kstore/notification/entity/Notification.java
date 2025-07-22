package com.kstore.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "recipient")
    private String recipient; // email, phone number, device token

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel")
    private NotificationChannel channel;

    @Column(name = "subject")
    private String subject;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "template_name")
    private String templateName;

    @ElementCollection
    @CollectionTable(name = "notification_parameters", 
                    joinColumns = @JoinColumn(name = "notification_id"))
    @MapKeyColumn(name = "parameter_key")
    @Column(name = "parameter_value")
    private Map<String, String> parameters;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(name = "priority")
    @Builder.Default
    private Integer priority = 1; // 1=Low, 2=Medium, 3=High, 4=Critical

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "delivery_attempts")
    @Builder.Default
    private Integer deliveryAttempts = 0;

    @Column(name = "max_attempts")
    @Builder.Default
    private Integer maxAttempts = 3;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "external_message_id")
    private String externalMessageId;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum NotificationType {
        ORDER_CONFIRMATION,
        ORDER_SHIPPED,
        ORDER_DELIVERED,
        ORDER_CANCELLED,
        PAYMENT_SUCCESSFUL,
        PAYMENT_FAILED,
        PASSWORD_RESET,
        ACCOUNT_VERIFICATION,
        WELCOME,
        PROMOTIONAL,
        SYSTEM_ALERT,
        SYSTEM,
        TRANSACTIONAL,
        CUSTOM
    }

    public enum NotificationChannel {
        EMAIL,
        SMS,
        PUSH_NOTIFICATION,
        WEB_NOTIFICATION
    }

    public enum NotificationStatus {
        PENDING,
        PROCESSING,
        SENT,
        DELIVERED,
        FAILED,
        CANCELLED
    }
}
