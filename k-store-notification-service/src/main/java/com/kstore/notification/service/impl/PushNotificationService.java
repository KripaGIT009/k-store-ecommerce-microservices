package com.kstore.notification.service.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.kstore.notification.service.NotificationChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class PushNotificationService implements NotificationChannelService {

    private final FirebaseMessaging firebaseMessaging;

    @Override
    @Async("notificationTaskExecutor")
    public CompletableFuture<Boolean> sendNotification(com.kstore.notification.entity.Notification notification) {
        try {
            log.info("Sending push notification to device: {}", notification.getRecipient());

            Notification.Builder notificationBuilder = Notification.builder()
                    .setTitle(notification.getSubject())
                    .setBody(notification.getContent());

            Message.Builder messageBuilder = Message.builder()
                    .setToken(notification.getRecipient()) // Device FCM token
                    .setNotification(notificationBuilder.build());

            // Add custom data from parameters
            if (notification.getParameters() != null && !notification.getParameters().isEmpty()) {
                messageBuilder.putAllData(notification.getParameters());
            }

            Message message = messageBuilder.build();
            String response = firebaseMessaging.send(message);

            log.info("Push notification sent successfully to device: {}, Response: {}", 
                    notification.getRecipient(), response);
            return CompletableFuture.completedFuture(true);

        } catch (Exception e) {
            log.error("Failed to send push notification to device: {}", notification.getRecipient(), e);
            return CompletableFuture.completedFuture(false);
        }
    }

    @Override
    public boolean supportsChannel(com.kstore.notification.entity.Notification.NotificationChannel channel) {
        return channel == com.kstore.notification.entity.Notification.NotificationChannel.PUSH_NOTIFICATION;
    }

    @Override
    public String getChannelName() {
        return "PUSH_NOTIFICATION";
    }
}
