package com.kstore.notification.service.impl;

import com.kstore.notification.entity.Notification;
import com.kstore.notification.entity.NotificationInbox;
import com.kstore.notification.service.NotificationChannelService;
import com.kstore.notification.service.NotificationInboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebNotificationService implements NotificationChannelService {

    private final NotificationInboxService inboxService;

    @Override
    @Async("notificationTaskExecutor")
    public CompletableFuture<Boolean> sendNotification(Notification notification) {
        try {
            log.info("Processing web notification for user: {} with subject: {}", 
                    notification.getUserId(), notification.getSubject());

            // Since we're using Kafka for real-time notifications,
            // this service integrates with frontend clients by storing 
            // notifications in the database for user inbox retrieval
            
            // Save notification to user's inbox for later retrieval
            NotificationInbox inboxNotification = inboxService.saveToInbox(notification);
            
            log.info("Web notification saved to inbox with ID: {} for user: {}", 
                    inboxNotification.getId(), notification.getUserId());
            
            // Update notification metadata with inbox reference
            notification.setExternalMessageId("INBOX_" + inboxNotification.getId());
            
            // Frontend clients can now:
            // 1. Poll REST API endpoints to get user notifications
            // 2. Use Server-Sent Events (SSE) for real-time updates
            // 3. Subscribe to user-specific Kafka topics for instant delivery
            // 4. Use browser push notifications for offline users
            
            return CompletableFuture.completedFuture(true);
            
        } catch (Exception e) {
            log.error("Failed to process web notification for user: {}", notification.getUserId(), e);
            return CompletableFuture.completedFuture(false);
        }
    }

    @Override
    public boolean supportsChannel(Notification.NotificationChannel channel) {
        return channel == Notification.NotificationChannel.WEB_NOTIFICATION;
    }

    @Override
    public String getChannelName() {
        return "WEB_NOTIFICATION";
    }
}
