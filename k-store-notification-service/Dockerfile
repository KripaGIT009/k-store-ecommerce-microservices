FROM openjdk:17-jre-slim

LABEL maintainer="K-Store Team"
LABEL service="notification-service"

# Create app directory
RUN mkdir -p /app/logs

# Copy the JAR file
COPY target/k-store-notification-service-1.0.0.jar /app/app.jar

# Create non-root user
RUN addgroup --system appuser && adduser --system --group appuser
RUN chown -R appuser:appuser /app
USER appuser

# Set working directory
WORKDIR /app

# Expose port
EXPOSE 8085

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8085/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
