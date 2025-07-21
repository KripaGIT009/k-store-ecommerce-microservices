FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="K-Store Team"
LABEL version="1.0.0"
LABEL description="K-Store API Gateway"

# Create app user
RUN addgroup -g 1001 -S appuser && adduser -u 1001 -S appuser -G appuser

# Set working directory
WORKDIR /app

# Copy JAR file
COPY target/k-store-api-gateway-1.0.0.jar app.jar

# Change ownership
RUN chown -R appuser:appuser /app

# Switch to app user
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Expose port
EXPOSE 8080

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
