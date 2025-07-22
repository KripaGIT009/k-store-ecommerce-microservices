# K-Store Notification Service with Kafka Integration

## Overview
The K-Store Notification Service now includes **Apache Kafka** integration for event-driven notifications, providing better scalability, reliability, and decoupling between microservices.

## 🚀 **Kafka Integration Benefits**

### **Event-Driven Architecture**
- **Decoupled Microservices**: Services publish events without knowing about notification consumers
- **Scalable Processing**: Kafka's distributed nature handles high-volume notification events
- **Reliability**: Message persistence and replication ensure no notifications are lost
- **Real-time Processing**: Low-latency event processing for immediate notifications

### **Asynchronous Communication**
- **Non-blocking**: Services don't wait for notification delivery
- **Fault Tolerance**: Failed notifications are retried automatically
- **Load Distribution**: Multiple consumer instances can process notifications in parallel

## 📋 **Architecture Components**

### **Kafka Infrastructure**
- **Zookeeper**: Coordination service for Kafka cluster
- **Kafka Broker**: Message broker for event streaming
- **Kafka UI**: Web interface for monitoring topics and messages (port 8090)

### **Kafka Topics**
```yaml
Topics:
  - notification-events      # Direct notification requests
  - bulk-notification-events # Bulk notification processing
  - user-events             # User lifecycle events
  - order-events            # Order status changes
  - payment-events          # Payment processing events
  - product-events          # Product and inventory events
```

### **Event Listeners**
- **NotificationEventListener**: Processes notification events from Kafka topics
- **UserEventListener**: Handles user registration, login, profile changes
- **OrderEventListener**: Processes order creation, updates, shipping notifications
- **PaymentEventListener**: Handles payment success/failure notifications

### **Event Producers**
- **NotificationEventProducer**: Publishes events to Kafka topics
- **Service Integration**: Other microservices can publish events via REST API

## 🔧 **Configuration**

### **Docker Compose - Kafka Setup**
```yaml
# Kafka Infrastructure
zookeeper:
  image: confluentinc/cp-zookeeper:7.4.0
  environment:
    ZOOKEEPER_CLIENT_PORT: 2181

kafka:
  image: confluentinc/cp-kafka:7.4.0
  ports:
    - "9092:9092"
    - "29092:29092"
  environment:
    KAFKA_BROKER_ID: 1
    KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
    KAFKA_AUTO_CREATE_TOPICS_ENABLE: true

kafka-ui:
  image: provectuslabs/kafka-ui:latest
  ports:
    - "8090:8080"
  environment:
    KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:9092
```

### **Spring Kafka Configuration**
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: notification-service
      auto-offset-reset: earliest
      enable-auto-commit: false
    producer:
      acks: all
      retries: 3
    listener:
      ack-mode: manual_immediate
```

## 📨 **Event-Driven Notification Flow**

### **1. User Registration Example**
```flow
User Service → [user-events] → Notification Listener → Welcome Email
```

**User Service publishes:**
```json
{
  "eventType": "USER_REGISTERED",
  "userId": 123,
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "registeredAt": "2025-07-22T10:00:00"
}
```

**Notification Service automatically:**
1. Consumes user registration event
2. Creates welcome email notification
3. Sends via SMTP using template
4. Updates delivery status

### **2. Order Processing Example**
```flow
Order Service → [order-events] → Notification Listener → Order Confirmation Email + SMS
```

**Order Service publishes:**
```json
{
  "eventType": "ORDER_CREATED",
  "orderId": 456,
  "orderNumber": "ORD-001",
  "userId": 123,
  "customerEmail": "user@example.com",
  "totalAmount": 99.99,
  "createdAt": "2025-07-22T10:05:00"
}
```

**Notification Service automatically:**
1. Consumes order creation event
2. Creates order confirmation email
3. Sends SMS notification (optional)
4. Updates order status in real-time via WebSocket

## 🌐 **API Endpoints for Event Publishing**

### **Direct Event Publishing**
```http
POST /api/events/notification
Content-Type: application/json

{
  "userId": 123,
  "recipient": "user@example.com",
  "notificationType": "WELCOME",
  "channel": "EMAIL",
  "templateName": "WELCOME_EMAIL",
  "parameters": {
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

### **Service-Specific Events**
```http
# User Events
POST /api/events/user/USER_REGISTERED

# Order Events  
POST /api/events/order/ORDER_CREATED

# Payment Events
POST /api/events/payment/PAYMENT_PROCESSED

# Product Events
POST /api/events/product/STOCK_LOW
```

## 🔄 **Event Processing Patterns**

### **Guaranteed Delivery**
- Manual acknowledgment after successful processing
- Automatic retry for failed notifications
- Dead letter queues for permanent failures

### **Idempotency**
- Event IDs prevent duplicate processing
- Database constraints ensure single notification per event

### **Scalability**
- Multiple consumer instances for parallel processing
- Partitioned topics for load distribution
- Configurable thread pools per notification channel

## 📊 **Monitoring and Observability**

### **Kafka UI Dashboard**
- **URL**: http://localhost:8090
- **Features**:
  - Topic browsing and message inspection
  - Consumer group monitoring
  - Partition and offset tracking
  - Performance metrics

### **Application Metrics**
```yaml
# Exposed via Spring Boot Actuator
/actuator/health     # Service health including Kafka connectivity
/actuator/metrics    # Kafka consumer/producer metrics
/actuator/prometheus # Metrics for monitoring tools
```

### **Logging**
```yaml
# Structured logging for event processing
logging:
  level:
    org.apache.kafka: INFO
    com.kstore.notification.listener: DEBUG
```

## 🚀 **Getting Started**

### **1. Start the Infrastructure**
```bash
# Start all services including Kafka
docker-compose up -d

# Check Kafka is running
docker-compose ps kafka
```

### **2. Monitor Kafka Topics**
- Open Kafka UI: http://localhost:8090
- Verify topics are created automatically
- Monitor message flow in real-time

### **3. Test Event-Driven Notifications**
```bash
# Publish a user registration event
curl -X POST http://localhost:8085/api/events/user/USER_REGISTERED \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "User"
  }'

# Check notification was created and sent
curl http://localhost:8085/api/notifications/user/1
```

### **4. Integration with Other Services**

**User Service Integration:**
```java
@Autowired
private KafkaTemplate<String, Object> kafkaTemplate;

public void registerUser(User user) {
    // Save user logic...
    
    // Publish user registration event
    UserRegisteredEvent event = UserRegisteredEvent.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .registeredAt(LocalDateTime.now())
        .build();
        
    kafkaTemplate.send("user-events", "USER_REGISTERED", event);
}
```

## 🔒 **Security and Best Practices**

### **Message Security**
- Kafka SASL/SSL configuration for production
- Event payload encryption for sensitive data
- Access control for topic operations

### **Error Handling**
- Comprehensive exception handling in listeners
- Circuit breaker pattern for external service calls
- Structured error logging and alerting

### **Performance Optimization**
- Batch processing for bulk notifications
- Connection pooling for database operations
- Caching for frequently used templates

## 📈 **Scalability Features**

### **Horizontal Scaling**
- Multiple notification service instances
- Kafka consumer group load balancing
- Database connection pooling

### **Volume Handling**
- Configurable consumer concurrency
- Bulk message processing
- Rate limiting per notification channel

### **Resource Management**
- Dedicated thread pools per notification type
- Memory-efficient event processing
- Configurable retry policies

This Kafka integration transforms the notification service into a truly scalable, event-driven system that can handle enterprise-level notification volumes while maintaining reliability and performance.
