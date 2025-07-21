# K-Store Notification Service

## Overview
The K-Store Notification Service is a comprehensive async notification system that supports multiple delivery channels including Email (SMTP), SMS (text messages), Mobile Push Notifications, and Real-time Web Notifications.

## Features

### Multi-Channel Support
- **Email Notifications**: HTML/text emails via SMTP (JavaMailSender)
- **SMS Notifications**: Text messages via AWS SNS
- **Push Notifications**: Mobile push notifications via Firebase Cloud Messaging (FCM)
- **Web Notifications**: Real-time notifications via WebSocket/STOMP

### Async Processing
- Non-blocking async notification delivery using `@Async` and `CompletableFuture`
- Dedicated thread pools for each notification channel
- Background processing for bulk notifications

### Template Engine
- Dynamic template processing with parameter substitution
- Variable syntax: `{{variableName}}`
- Pre-configured templates for common notification types
- Template validation and error handling

### Advanced Features
- **Delivery Tracking**: Status monitoring with delivery attempts and error logging
- **Retry Mechanism**: Automatic retry for failed notifications with configurable max attempts
- **Scheduled Notifications**: Support for delayed/scheduled delivery
- **Bulk Notifications**: Efficient processing of mass notifications
- **Priority System**: 4-level priority system (Low, Medium, High, Critical)
- **Rate Limiting**: Built-in rate limiting for different channels

## Architecture

### Core Components

#### Entities
- `Notification`: Core notification entity with delivery tracking
- `NotificationTemplate`: Template management with parameter support

#### Services
- `NotificationService`: Main orchestration service
- `NotificationChannelService`: Abstract channel interface
- `EmailNotificationService`: SMTP email delivery
- `SmsNotificationService`: AWS SNS SMS delivery  
- `PushNotificationService`: Firebase FCM push notifications
- `WebNotificationService`: WebSocket real-time notifications
- `TemplateService`: Template processing and validation

#### Configuration
- `AsyncConfig`: Thread pool configuration for async processing
- `WebSocketConfig`: WebSocket/STOMP configuration
- `FirebaseConfig`: Firebase FCM initialization
- `NotificationTemplateInitializer`: Default template setup

### Database Schema
- **notifications**: Main notification records with delivery status
- **notification_templates**: Template definitions with content
- **notification_parameters**: Key-value parameters for notifications

## Default Templates

### Email Templates
1. **WELCOME_EMAIL**: User registration welcome message
2. **ORDER_CONFIRMATION**: Order confirmation with details
3. **PASSWORD_RESET**: Password reset with secure link
4. **LOW_STOCK_ALERT**: Admin inventory alerts

### SMS Templates
1. **ORDER_SHIPPED_SMS**: Order shipping notifications

### Push Templates
1. **ORDER_UPDATE_PUSH**: Order status updates

## API Endpoints

### REST API
- `POST /api/notifications` - Create notification
- `POST /api/notifications/{id}/send` - Send notification
- `POST /api/notifications/bulk` - Send bulk notifications
- `GET /api/notifications/{id}` - Get notification details
- `GET /api/notifications/user/{userId}` - Get user notifications
- `PUT /api/notifications/{id}/status` - Update notification status
- `DELETE /api/notifications/{id}` - Cancel notification

### WebSocket Endpoints
- `/ws` - Main WebSocket endpoint
- `/ws-notifications` - Notification-specific WebSocket
- `/topic/*` - Broadcast topics
- `/queue/*` - Point-to-point messaging
- `/user/*` - User-specific destinations

## Configuration

### Application Properties
```yaml
server:
  port: 8085

spring:
  application:
    name: k-store-notification-service
  
  datasource:
    url: jdbc:postgresql://localhost:5432/k_store_notifications
    username: kstore_user
    password: kstore123
  
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${SMTP_USERNAME}
    password: ${SMTP_PASSWORD}

aws:
  region: ${AWS_REGION:us-east-1}
  sns:
    sms:
      sender-id: K-Store

notification:
  firebase:
    config-file: firebase-service-account.json
  default:
    max-attempts: 3
    retry-delay-minutes: 5
```

### Environment Variables
- `SMTP_USERNAME`: Email service username
- `SMTP_PASSWORD`: Email service password/app-password
- `AWS_ACCESS_KEY_ID`: AWS access key for SNS
- `AWS_SECRET_ACCESS_KEY`: AWS secret key for SNS
- `AWS_REGION`: AWS region for services
- `FIREBASE_DATABASE_URL`: Firebase database URL (optional)

## Integration

### Service Discovery
- Registered with Eureka Discovery Server on port 8761
- Service name: `k-store-notification-service`
- Health checks via Spring Boot Actuator

### Database
- PostgreSQL database: `k_store_notifications`
- Database user: `kstore_user`
- JPA/Hibernate with automatic schema generation

### Message Queuing
- Redis integration for caching and session management
- WebSocket message broker for real-time notifications

## Usage Examples

### Send Welcome Email
```java
NotificationRequest request = NotificationRequest.builder()
    .userId(userId)
    .recipient("user@example.com")
    .templateName("WELCOME_EMAIL")
    .parameters(Map.of(
        "firstName", "John",
        "lastName", "Doe",
        "email", "user@example.com"
    ))
    .build();

NotificationResponse response = notificationService.createNotification(request);
CompletableFuture<NotificationResponse> future = notificationService.sendNotificationAsync(response.getId());
```

### Send Bulk Notifications
```java
BulkNotificationRequest bulkRequest = BulkNotificationRequest.builder()
    .templateName("ORDER_CONFIRMATION")
    .globalParameters(Map.of("companyName", "K-Store"))
    .recipients(Arrays.asList(
        RecipientData.builder()
            .userId(1L)
            .recipient("user1@example.com")
            .personalizedParameters(Map.of("orderNumber", "ORD-001"))
            .build(),
        RecipientData.builder()
            .userId(2L)
            .recipient("user2@example.com")
            .personalizedParameters(Map.of("orderNumber", "ORD-002"))
            .build()
    ))
    .build();

CompletableFuture<List<NotificationResponse>> future = notificationService.sendBulkNotificationsAsync(bulkRequest);
```

### WebSocket Integration
```javascript
// Client-side WebSocket connection
const socket = new SockJS('/ws-notifications');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    // Subscribe to user-specific notifications
    stompClient.subscribe('/user/queue/notifications', function(notification) {
        const notificationData = JSON.parse(notification.body);
        displayNotification(notificationData);
    });
});
```

## Monitoring and Maintenance

### Health Checks
- Spring Boot Actuator endpoints
- Database connectivity checks
- Email service connectivity
- AWS service health

### Logging
- Structured logging with correlation IDs
- Separate log levels for different channels
- Error tracking and notification delivery metrics

### Scheduled Tasks
- Automatic processing of scheduled notifications (every 30 seconds)
- Retry failed notifications (every 5 minutes)
- Cleanup of old notifications and parameters

## Security

### Authentication
- JWT token validation for API endpoints
- Secure WebSocket connections
- AWS credentials management

### Data Protection
- Sensitive parameter encryption in database
- Secure email credentials handling
- Firebase service account security

## Deployment

### Docker Support
- Dockerfile included for containerized deployment
- Docker Compose integration with dependent services
- Health checks and graceful shutdowns

### Dependencies
- PostgreSQL database
- Redis cache
- Eureka Discovery Server
- Email service (SMTP)
- AWS SNS service
- Firebase FCM (optional)

## Performance

### Async Processing
- Dedicated thread pools for each notification type
- Non-blocking I/O operations
- Concurrent processing of bulk notifications

### Caching
- Redis caching for templates and user preferences
- In-memory caching of frequently used templates
- Connection pooling for database operations

### Scalability
- Horizontal scaling support
- Load balancing compatible
- Queue-based processing for high volumes

This comprehensive notification service provides a robust foundation for all notification needs in the K-Store e-commerce platform, with support for multiple channels, async processing, and enterprise-grade features.
