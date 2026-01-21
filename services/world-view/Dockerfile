# =============================================================================
# World View Service Dockerfile
# Multi-stage build for optimized image size
# =============================================================================

# -----------------------------------------------------------------------------
# Stage 1: Build Stage
# -----------------------------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build

# Create toolchains.xml for Maven toolchains plugin
RUN mkdir -p /root/.m2 && \
    echo '<?xml version="1.0" encoding="UTF-8"?>\
<toolchains xmlns="http://maven.apache.org/TOOLCHAINS/1.1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/TOOLCHAINS/1.1.0 http://maven.apache.org/xsd/toolchains-1.1.0.xsd">\
  <toolchain>\
    <type>jdk</type>\
    <provides>\
      <version>17</version>\
      <vendor>any</vendor>\
    </provides>\
    <configuration>\
      <jdkHome>/opt/java/openjdk</jdkHome>\
    </configuration>\
  </toolchain>\
</toolchains>' > /root/.m2/toolchains.xml

# Copy parent POM and install it first
COPY pom.xml /build/pom.xml

# Install parent POM to local repository (skip modules)
RUN mvn -N install -DskipTests

# Copy service POM and source
COPY services/world-view/pom.xml /build/services/world-view/
COPY services/world-view/src /build/services/world-view/src

# Build the application (skip tests)
WORKDIR /build/services/world-view
RUN mvn clean package -DskipTests -Dmaven.wagon.http.retryHandler.count=3

# -----------------------------------------------------------------------------
# Stage 2: Runtime Stage
# -----------------------------------------------------------------------------
FROM eclipse-temurin:17-jre

LABEL maintainer="Nebula Team"
LABEL service="world-view"
LABEL version="1.0.0"

# Create non-root user for security
RUN groupadd -g 1001 nebula && \
    useradd -u 1001 -g nebula -s /bin/bash nebula

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /build/services/world-view/target/world-view.jar app.jar

# Change ownership to non-root user
RUN chown nebula:nebula app.jar

# Switch to non-root user
USER nebula

# Expose service port
EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8082/actuator/health || exit 1

# JVM optimizations for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
