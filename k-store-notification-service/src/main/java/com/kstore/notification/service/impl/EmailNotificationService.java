package com.kstore.notification.service.impl;

import com.kstore.notification.entity.Notification;
import com.kstore.notification.service.NotificationChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService implements NotificationChannelService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@kstore.com}")
    private String fromEmail;

    @Value("${spring.mail.from-name:K-Store}")
    private String fromName;

    @Override
    @Async("notificationTaskExecutor")
    public CompletableFuture<Boolean> sendNotification(Notification notification) {
        try {
            log.info("Sending email notification to: {}", notification.getRecipient());

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(notification.getRecipient());
            helper.setSubject(notification.getSubject());
            helper.setText(notification.getContent(), true); // true = HTML content

            mailSender.send(message);

            log.info("Email notification sent successfully to: {}", notification.getRecipient());
            return CompletableFuture.completedFuture(true);

        } catch (MessagingException e) {
            log.error("Failed to send email notification to: {}", notification.getRecipient(), e);
            return CompletableFuture.completedFuture(false);
        } catch (Exception e) {
            log.error("Unexpected error sending email notification to: {}", notification.getRecipient(), e);
            return CompletableFuture.completedFuture(false);
        }
    }

    @Override
    public boolean supportsChannel(Notification.NotificationChannel channel) {
        return channel == Notification.NotificationChannel.EMAIL;
    }

    @Override
    public String getChannelName() {
        return "EMAIL";
    }
}
