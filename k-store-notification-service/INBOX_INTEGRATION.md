# Notification Inbox Integration

## Overview
The K-Store notification service now integrates with Kafka for real-time notifications and stores them in a database inbox for user retrieval.

## Architecture
```
Kafka Event → Notification Service → Database Storage → REST API → Frontend
```

## Features

### 📮 Database Storage
- **User Inbox**: All web notifications are stored in `notification_inbox` table
- **Smart Expiration**: Different notification types have different expiration periods
- **Priority Management**: LOW, MEDIUM, HIGH, CRITICAL priority levels
- **Read/Unread Tracking**: Track user interaction with notifications
- **Archive Support**: Users can archive notifications

### 🔄 Integration Flow
1. **Event Received**: Kafka consumer receives notification event
2. **Process Notification**: WebNotificationService processes the event
3. **Store in Database**: Notification saved to user's inbox
4. **API Access**: Frontend retrieves via REST API endpoints

### 📋 API Endpoints

#### Get User Notifications
```http
GET /api/notifications/inbox/user/{userId}
GET /api/notifications/inbox/user/{userId}/unread
GET /api/notifications/inbox/user/{userId}/active
```

#### Filter Notifications
```http
GET /api/notifications/inbox/user/{userId}/type/{type}
GET /api/notifications/inbox/user/{userId}/priority/{priority}
```

#### Manage Notifications
```http
PUT /api/notifications/inbox/{id}/user/{userId}/read
PUT /api/notifications/inbox/user/{userId}/read-all
PUT /api/notifications/inbox/{id}/user/{userId}/archive
DELETE /api/notifications/inbox/{id}/user/{userId}
```

#### Get Notification Count
```http
GET /api/notifications/inbox/user/{userId}/unread-count
```

### 🗄️ Database Schema
```sql
CREATE TABLE notification_inbox (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    notification_id BIGINT,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    type VARCHAR(50),
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    is_read BOOLEAN DEFAULT FALSE,
    is_archived BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP,
    archived_at TIMESTAMP,
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### ⏰ Expiration Policies
- **Promotional**: 7 days
- **System Alerts**: 3 days  
- **Order/Payment**: 90 days
- **Security**: 1 day
- **Default**: 30 days

### 🧹 Cleanup
- **Scheduled Task**: Daily cleanup at 2 AM
- **Automatic**: Removes expired notifications
- **Performance**: Maintains optimal database size

## Example Usage

### Frontend Integration
```javascript
// Get unread notifications
const response = await fetch('/api/notifications/inbox/user/123/unread');
const notifications = await response.json();

// Mark as read
await fetch('/api/notifications/inbox/456/user/123/read', { method: 'PUT' });

// Get unread count for badge
const countResponse = await fetch('/api/notifications/inbox/user/123/unread-count');
const count = await countResponse.json();
```

### Kafka Event Example
```java
// When order is created, this triggers a notification
@EventListener
public void handleOrderCreated(OrderCreatedEvent event) {
    NotificationEvent notification = NotificationEvent.builder()
        .userId(event.getUserId())
        .type(NotificationType.ORDER_CONFIRMATION)
        .channel(NotificationChannel.WEB_NOTIFICATION)
        .subject("Order Confirmed")
        .content("Your order #" + event.getOrderNumber() + " has been confirmed")
        .build();
    
    kafkaTemplate.send("notification-events", notification);
}
```

This integration provides a complete solution for web notifications using Kafka for real-time delivery and database storage for user inbox management.
