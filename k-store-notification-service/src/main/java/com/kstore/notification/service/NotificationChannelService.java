package com.kstore.notification.service;

import com.kstore.notification.entity.Notification;

import java.util.concurrent.CompletableFuture;

public interface NotificationChannelService {

    CompletableFuture<Boolean> sendNotification(Notification notification);

    boolean supportsChannel(Notification.NotificationChannel channel);

    String getChannelName();
}
