package com.kstore.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Notification.NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel")
    private Notification.NotificationChannel channel;

    @Column(name = "subject_template")
    private String subjectTemplate;

    @Column(name = "content_template", columnDefinition = "TEXT")
    private String contentTemplate;

    @Column(name = "language")
    @Builder.Default
    private String language = "en";

    @Column(name = "is_active")
    @Builder.Default
    private Boolean active = true;

    @Column(name = "description")
    private String description;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
