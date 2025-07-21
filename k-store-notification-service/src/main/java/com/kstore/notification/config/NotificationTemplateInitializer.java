package com.kstore.notification.config;

import com.kstore.notification.entity.Notification;
import com.kstore.notification.entity.NotificationTemplate;
import com.kstore.notification.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationTemplateInitializer implements CommandLineRunner {

    private final NotificationTemplateRepository templateRepository;

    @Override
    public void run(String... args) {
        initializeDefaultTemplates();
    }

    private void initializeDefaultTemplates() {
        List<NotificationTemplate> defaultTemplates = List.of(
            // Welcome Email Template
            NotificationTemplate.builder()
                .name("WELCOME_EMAIL")
                .type(Notification.NotificationType.WELCOME)
                .channel(Notification.NotificationChannel.EMAIL)
                .subjectTemplate("Welcome to K-Store, {{firstName}}!")
                .contentTemplate("""
                    <html>
                    <body>
                        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                            <h1 style="color: #333;">Welcome to K-Store!</h1>
                            <p>Dear {{firstName}} {{lastName}},</p>
                            <p>Thank you for joining K-Store! We're excited to have you as part of our community.</p>
                            <p>Your account has been successfully created with email: <strong>{{email}}</strong></p>
                            <p>Start exploring our amazing products and enjoy shopping with us!</p>
                            <p>Best regards,<br>The K-Store Team</p>
                        </div>
                    </body>
                    </html>
                    """)
                .active(true)
                .build(),

            // Order Confirmation Template
            NotificationTemplate.builder()
                .name("ORDER_CONFIRMATION")
                .type(Notification.NotificationType.ORDER_CONFIRMATION)
                .channel(Notification.NotificationChannel.EMAIL)
                .subjectTemplate("Order Confirmation - #{{orderNumber}}")
                .contentTemplate("""
                    <html>
                    <body>
                        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                            <h1 style="color: #4CAF50;">Order Confirmed!</h1>
                            <p>Hi {{customerName}},</p>
                            <p>Thank you for your order! We've received your order and it's being processed.</p>
                            <div style="background: #f5f5f5; padding: 15px; margin: 20px 0;">
                                <h3>Order Details:</h3>
                                <p><strong>Order Number:</strong> {{orderNumber}}</p>
                                <p><strong>Order Date:</strong> {{orderDate}}</p>
                                <p><strong>Total Amount:</strong> ${{totalAmount}}</p>
                                <p><strong>Delivery Address:</strong> {{deliveryAddress}}</p>
                            </div>
                            <p>You'll receive another email when your order ships.</p>
                            <p>Thanks for shopping with K-Store!</p>
                        </div>
                    </body>
                    </html>
                    """)
                .active(true)
                .build(),

            // Password Reset Template
            NotificationTemplate.builder()
                .name("PASSWORD_RESET")
                .type(Notification.NotificationType.PASSWORD_RESET)
                .channel(Notification.NotificationChannel.EMAIL)
                .subjectTemplate("Reset Your K-Store Password")
                .contentTemplate("""
                    <html>
                    <body>
                        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                            <h1 style="color: #ff6b6b;">Password Reset Request</h1>
                            <p>Hi {{firstName}},</p>
                            <p>We received a request to reset your password for your K-Store account.</p>
                            <p>Click the link below to reset your password:</p>
                            <a href="{{resetLink}}" style="background: #007bff; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; display: inline-block; margin: 20px 0;">Reset Password</a>
                            <p>This link will expire in 24 hours.</p>
                            <p>If you didn't request this password reset, please ignore this email.</p>
                            <p>Best regards,<br>The K-Store Team</p>
                        </div>
                    </body>
                    </html>
                    """)
                .active(true)
                .build(),

            // Order Shipped SMS Template
            NotificationTemplate.builder()
                .name("ORDER_SHIPPED_SMS")
                .type(Notification.NotificationType.ORDER_SHIPPED)
                .channel(Notification.NotificationChannel.SMS)
                .subjectTemplate("Order Shipped")
                .contentTemplate("Hi {{customerName}}, your K-Store order #{{orderNumber}} has been shipped! Track: {{trackingUrl}}")
                .active(true)
                .build(),

            // Low Stock Alert Template
            NotificationTemplate.builder()
                .name("LOW_STOCK_ALERT")
                .type(Notification.NotificationType.SYSTEM_ALERT)
                .channel(Notification.NotificationChannel.EMAIL)
                .subjectTemplate("Low Stock Alert - {{productName}}")
                .contentTemplate("""
                    <html>
                    <body>
                        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                            <h1 style="color: #ff9800;">Low Stock Alert</h1>
                            <p>Hi Admin,</p>
                            <p>The following product is running low on stock:</p>
                            <div style="background: #fff3cd; padding: 15px; margin: 20px 0; border-left: 4px solid #ff9800;">
                                <h3>{{productName}}</h3>
                                <p><strong>Product ID:</strong> {{productId}}</p>
                                <p><strong>Current Stock:</strong> {{currentStock}}</p>
                                <p><strong>Minimum Threshold:</strong> {{minThreshold}}</p>
                            </div>
                            <p>Please restock this item to avoid stock-out situations.</p>
                        </div>
                    </body>
                    </html>
                    """)
                .active(true)
                .build(),

            // Push Notification Template for Order Updates
            NotificationTemplate.builder()
                .name("ORDER_UPDATE_PUSH")
                .type(Notification.NotificationType.ORDER_CONFIRMATION)
                .channel(Notification.NotificationChannel.PUSH_NOTIFICATION)
                .subjectTemplate("Order Update")
                .contentTemplate("Your order #{{orderNumber}} status has been updated to: {{orderStatus}}")
                .active(true)
                .build()
        );

        for (NotificationTemplate template : defaultTemplates) {
            if (!templateRepository.existsByName(template.getName())) {
                templateRepository.save(template);
                log.info("Created default notification template: {}", template.getName());
            }
        }

        log.info("Default notification templates initialization completed");
    }
}
