# K-Store E-commerce Microservices Application

A comprehensive e-commerce microservices application built with Java 21, Spring Boot 3.x, Spring Cloud, and designed for AWS deployment.

## 🏗️ Architecture Overview

This application follows a microservices architecture pattern with the following components:

### Core Services
- **API Gateway** (Port 8080) - Entry point for all client requests
- **Config Server** (Port 8888) - Centralized configuration management
- **Discovery Server** (Port 8761) - Service registration and discovery using Eureka

### Business Services
- **User Service** (Port 8081) - User management and authentication
- **Product Service** (Port 8082) - Product catalog and inventory management
- **Order Service** (Port 8083) - Order processing and management
- **Payment Service** (Port 8084) - Payment processing integration
- **Notification Service** (Port 8085) - Email, SMS, and push notifications

### Infrastructure
- **PostgreSQL** - Database for each service
- **Redis** - Caching layer
- **AWS Services** - Cloud infrastructure

## 🚀 Technology Stack

- **Java 21** - Programming language
- **Spring Boot 3.2.x** - Application framework
- **Spring Cloud 2023.0.x** - Microservices framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence
- **PostgreSQL** - Database
- **Redis** - Caching
- **Lombok** - Code generation
- **Maven** - Build tool
- **Docker** - Containerization
- **AWS** - Cloud platform

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL (for local development)
- Redis (for local development)
- AWS Account (for cloud deployment)

## 🛠️ Local Development Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd k-store
```

### 2. Start Infrastructure with Docker
```bash
docker-compose up -d postgres-users postgres-products postgres-orders postgres-payments redis
```

### 3. Build All Services
```bash
mvn clean install
```

### 4. Start Services in Order

1. **Config Server**
```bash
cd k-store-config-server
mvn spring-boot:run
```

2. **Discovery Server**
```bash
cd k-store-discovery-server
mvn spring-boot:run
```

3. **Business Services** (can be started in parallel)
```bash
# User Service
cd k-store-user-service
mvn spring-boot:run

# Product Service
cd k-store-product-service
mvn spring-boot:run

# Order Service
cd k-store-order-service
mvn spring-boot:run

# Payment Service
cd k-store-payment-service
mvn spring-boot:run

# Notification Service
cd k-store-notification-service
mvn spring-boot:run
```

4. **API Gateway**
```bash
cd k-store-api-gateway
mvn spring-boot:run
```

### 5. Access the Application
- API Gateway: http://localhost:8080
- Eureka Dashboard: http://localhost:8761
- Config Server: http://localhost:8888

## 🐳 Docker Deployment

### Build and Run All Services
```bash
docker-compose up --build
```

### Run Specific Services
```bash
docker-compose up config-server discovery-server api-gateway user-service product-service
```

## 🔧 Configuration

### Environment Variables
Create `.env` file in the root directory:
```env
DB_USERNAME=kstore
DB_PASSWORD=password
JWT_SECRET=mySecretKey
AWS_REGION=us-east-1
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
```

### Database Configuration
Each service uses its own PostgreSQL database:
- User Service: `kstore_users` (Port 5432)
- Product Service: `kstore_products` (Port 5433)
- Order Service: `kstore_orders` (Port 5434)
- Payment Service: `kstore_payments` (Port 5435)

## 🌐 API Endpoints

### User Service
- `POST /api/users/register` - User registration
- `POST /api/users/login` - User authentication
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update user profile

### Product Service
- `GET /api/products` - List products
- `GET /api/products/{id}` - Get product details
- `POST /api/products` - Create product (Admin/Seller only)
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

### Order Service
- `POST /api/orders` - Create order
- `GET /api/orders` - List user orders
- `GET /api/orders/{id}` - Get order details
- `PUT /api/orders/{id}/status` - Update order status

### Payment Service
- `POST /api/payments/process` - Process payment
- `GET /api/payments/{orderId}` - Get payment details
- `POST /api/payments/refund` - Process refund

### Notification Service
- `POST /api/notifications/email` - Send email notification
- `POST /api/notifications/sms` - Send SMS notification

## 🔐 Security

The application implements JWT-based authentication:
- Public endpoints: Registration, login
- Protected endpoints: All other APIs require valid JWT token
- Role-based access control: USER, ADMIN, SELLER roles

### Getting JWT Token
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"your-username","password":"your-password"}'
```

### Using JWT Token
```bash
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer your-jwt-token"
```

## 📊 Monitoring

### Health Checks
- Config Server: http://localhost:8888/actuator/health
- Discovery Server: http://localhost:8761/actuator/health
- API Gateway: http://localhost:8080/actuator/health
- User Service: http://localhost:8081/actuator/health

### Metrics
All services expose metrics at `/actuator/metrics` endpoint.

## ☁️ AWS Deployment

### Prerequisites
- AWS CLI configured
- EKS cluster or EC2 instances
- RDS PostgreSQL instances
- ElastiCache Redis cluster
- Application Load Balancer
- Route 53 (optional)

### AWS Services Used
- **EKS/ECS** - Container orchestration
- **RDS PostgreSQL** - Managed database
- **ElastiCache Redis** - Managed caching
- **Application Load Balancer** - Load balancing
- **API Gateway** - API management
- **SES** - Email service
- **SNS** - SMS service
- **CloudWatch** - Monitoring and logging
- **Systems Manager** - Parameter store for configuration

## 🧪 Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### API Testing with Postman
Import the provided Postman collection from `docs/postman/` directory.

## 📝 Development Guidelines

### Code Style
- Follow Java coding conventions
- Use Lombok to reduce boilerplate code
- Implement proper error handling
- Add comprehensive logging

### Database Migrations
- Use Liquibase or Flyway for database migrations
- Version all database changes
- Test migrations on staging before production

### API Design
- Follow RESTful principles
- Use proper HTTP status codes
- Implement consistent error responses
- Version your APIs

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For questions and support, please contact:
- Email: support@kstore.com
- Documentation: [Wiki](https://github.com/your-org/k-store/wiki)
- Issues: [GitHub Issues](https://github.com/your-org/k-store/issues)

## 🗺️ Roadmap

- [ ] Implement circuit breaker pattern
- [ ] Add distributed tracing
- [ ] Implement event-driven architecture
- [ ] Add Kubernetes deployment manifests
- [ ] Implement API rate limiting
- [ ] Add comprehensive monitoring dashboard
- [ ] Implement automated testing pipeline
- [ ] Add multi-tenant support
