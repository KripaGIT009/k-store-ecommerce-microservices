package com.kstore.notification.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kstore.notification.entity.Notification;
import com.kstore.notification.service.NotificationChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebNotificationService implements NotificationChannelService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    @Async("notificationTaskExecutor")
    public CompletableFuture<Boolean> sendNotification(Notification notification) {
        try {
            log.info("Sending web notification to user: {}", notification.getUserId());

            Map<String, Object> notificationData = new HashMap<>();
            notificationData.put("id", notification.getId());
            notificationData.put("type", notification.getType());
            notificationData.put("subject", notification.getSubject());
            notificationData.put("content", notification.getContent());
            notificationData.put("priority", notification.getPriority());
            notificationData.put("timestamp", notification.getCreatedAt());

            if (notification.getParameters() != null) {
                notificationData.put("parameters", notification.getParameters());
            }

            // Send to specific user via WebSocket
            String destination = "/topic/notifications/" + notification.getUserId();
            messagingTemplate.convertAndSend(destination, notificationData);

            // Also send to user's private queue
            String userDestination = "/queue/notifications/" + notification.getUserId();
            messagingTemplate.convertAndSend(userDestination, notificationData);

            log.info("Web notification sent successfully to user: {}", notification.getUserId());
            return CompletableFuture.completedFuture(true);

        } catch (Exception e) {
            log.error("Failed to send web notification to user: {}", notification.getUserId(), e);
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
