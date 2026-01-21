# =============================================================================
# Merchandise Service Dockerfile
# Multi-stage build for optimized image size
# =============================================================================

# -----------------------------------------------------------------------------
# Stage 1: Build Stage
# -----------------------------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy parent POM and install it first
COPY pom.xml /build/pom.xml

# Install parent POM to local repository (skip modules)
RUN mvn -N install -DskipTests

# Copy service POM and source
COPY services/merchandise-service/pom.xml /build/services/merchandise-service/
COPY services/merchandise-service/src /build/services/merchandise-service/src

# Build the application (skip tests for faster builds)
WORKDIR /build/services/merchandise-service
RUN mvn clean package -DskipTests -Dmaven.wagon.http.retryHandler.count=3

# -----------------------------------------------------------------------------
# Stage 2: Runtime Stage
# -----------------------------------------------------------------------------
FROM eclipse-temurin:17-jre

LABEL maintainer="Nebula Team"
LABEL service="merchandise-service"
LABEL version="1.0.0"

# Create non-root user for security
RUN groupadd -g 1001 nebula && \
    useradd -u 1001 -g nebula -s /bin/bash nebula

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /build/services/merchandise-service/target/merchandise-service.jar app.jar

# Change ownership to non-root user
RUN chown nebula:nebula app.jar

# Switch to non-root user
USER nebula

# Expose service port
EXPOSE 8084

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8084/actuator/health || exit 1

# JVM optimizations for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
