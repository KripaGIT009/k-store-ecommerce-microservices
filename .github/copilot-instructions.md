<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

# K-Store E-commerce Microservices - Copilot Instructions

## Project Overview
This is a comprehensive e-commerce microservices application built with Java 21, Spring Boot 3.x, Spring Cloud, and designed for AWS deployment. The application follows microservices architecture patterns with proper separation of concerns.

## Technology Stack
- **Java 21** with modern language features
- **Spring Boot 3.2.x** - Latest stable version
- **Spring Cloud 2023.0.x** - For microservices patterns
- **Spring Security** - For authentication and authorization
- **Spring Data JPA** - For data persistence
- **PostgreSQL** - Primary database
- **Redis** - For caching
- **Docker** - For containerization
- **AWS** - Cloud platform
- **Maven** - Build tool
- **Lombok** - For reducing boilerplate code

## Architecture Patterns
1. **Microservices Architecture** - Each service is independently deployable
2. **API Gateway Pattern** - Single entry point for all client requests
3. **Service Discovery** - Using Eureka for service registration
4. **Config Server** - Centralized configuration management
5. **Circuit Breaker** - For fault tolerance
6. **CQRS** - Command Query Responsibility Segregation where applicable
7. **Event-Driven Architecture** - For loose coupling between services

## Code Guidelines

### General Practices
- Use Java 21 features like records, pattern matching, and text blocks
- Follow Spring Boot best practices and conventions
- Implement proper error handling with custom exceptions
- Use Lombok annotations to reduce boilerplate code
- Write comprehensive unit and integration tests
- Follow RESTful API design principles
- Implement proper logging with SLF4J

### Naming Conventions
- Use PascalCase for class names
- Use camelCase for method and variable names
- Use UPPER_SNAKE_CASE for constants
- Use kebab-case for configuration properties
- Prefix interfaces with 'I' if needed
- Use meaningful and descriptive names

### Database Guidelines
- Use JPA entities with proper annotations
- Implement repository interfaces extending JpaRepository
- Use database migrations with Liquibase or Flyway
- Follow database naming conventions (snake_case)
- Implement soft deletes where appropriate
- Add proper indexes for performance

### Security Guidelines
- Implement JWT-based authentication
- Use role-based access control (RBAC)
- Validate all inputs
- Sanitize outputs to prevent XSS
- Use HTTPS in production
- Implement rate limiting
- Never store passwords in plain text

### API Guidelines
- Use proper HTTP status codes
- Implement consistent error response format
- Version your APIs appropriately
- Document APIs with OpenAPI/Swagger
- Implement pagination for list endpoints
- Use DTOs for request/response objects

### Microservices Specific
- Each service should have its own database
- Implement health checks for all services
- Use circuit breakers for external service calls
- Implement proper service-to-service communication
- Use asynchronous messaging where appropriate
- Implement distributed tracing

### AWS Integration
- Use AWS SDK for Java v2
- Implement proper AWS credentials management
- Use AWS services like RDS, ElastiCache, SES, SNS
- Implement CloudWatch logging and monitoring
- Use AWS Parameter Store for configuration
- Follow AWS security best practices

### Testing Guidelines
- Write unit tests for all business logic
- Implement integration tests for APIs
- Use TestContainers for database testing
- Mock external service dependencies
- Achieve minimum 80% code coverage
- Use BDD style for test naming

## Service Structure
Each microservice should follow this structure:
```
src/
├── main/
│   ├── java/
│   │   └── com/kstore/{service}/
│   │       ├── {ServiceName}Application.java
│   │       ├── config/
│   │       ├── controller/
│   │       ├── service/
│   │       ├── repository/
│   │       ├── entity/
│   │       ├── dto/
│   │       └── exception/
│   └── resources/
│       ├── application.yml
│       └── db/migration/
└── test/
```

## Dependencies to Consider
When suggesting dependencies, prefer:
- Spring Boot starters over individual libraries
- Official Spring libraries
- Well-maintained open-source libraries
- AWS SDK for cloud integrations
- TestContainers for testing
- Micrometer for metrics

## Performance Considerations
- Implement caching with Redis where appropriate
- Use database connection pooling
- Implement lazy loading for JPA entities
- Use pagination for large data sets
- Consider asynchronous processing for heavy operations
- Implement proper indexing strategies

## Security Considerations
- Always validate input data
- Use parameterized queries to prevent SQL injection
- Implement proper CORS configuration
- Use secure headers
- Implement audit logging
- Follow OWASP security guidelines

When providing code suggestions, always consider these guidelines and ensure the code follows the established patterns and conventions used in this project.
