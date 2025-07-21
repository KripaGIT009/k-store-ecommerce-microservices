package com.kstore.notification.service.impl;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.kstore.notification.entity.Notification;
import com.kstore.notification.service.NotificationChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsNotificationService implements NotificationChannelService {

    private final AmazonSNS amazonSNS;

    @Value("${aws.sns.sender-id:K-Store}")
    private String senderId;

    @Value("${aws.sns.sms-type:Transactional}")
    private String smsType;

    @Override
    @Async("notificationTaskExecutor")
    public CompletableFuture<Boolean> sendNotification(Notification notification) {
        try {
            log.info("Sending SMS notification to: {}", notification.getRecipient());

            Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
            smsAttributes.put("AWS.SNS.SMS.SenderID", 
                new MessageAttributeValue()
                    .withStringValue(senderId)
                    .withDataType("String"));
            smsAttributes.put("AWS.SNS.SMS.SMSType", 
                new MessageAttributeValue()
                    .withStringValue(smsType)
                    .withDataType("String"));

            PublishRequest publishRequest = new PublishRequest()
                    .withPhoneNumber(notification.getRecipient())
                    .withMessage(notification.getContent())
                    .withMessageAttributes(smsAttributes);

            PublishResult result = amazonSNS.publish(publishRequest);

            log.info("SMS notification sent successfully to: {}, MessageId: {}", 
                    notification.getRecipient(), result.getMessageId());
            return CompletableFuture.completedFuture(true);

        } catch (Exception e) {
            log.error("Failed to send SMS notification to: {}", notification.getRecipient(), e);
            return CompletableFuture.completedFuture(false);
        }
    }

    @Override
    public boolean supportsChannel(Notification.NotificationChannel channel) {
        return channel == Notification.NotificationChannel.SMS;
    }

    @Override
    public String getChannelName() {
        return "SMS";
    }
}
