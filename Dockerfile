# Multi-stage Dockerfile for Plant Care Service Platform Backend
# ===============================================================
# Stage 1: Build the Spring Boot application using Maven & Java 21
# ===============================================================
FROM maven:3.9.8-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build production executable jar skipping test execution
RUN mvn clean package -Dmaven.test.skip=true -B

# ===============================================================
# Stage 2: Minimal, secure runtime image using Eclipse Temurin JRE 21
# ===============================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install curl for container health check
RUN apk add --no-cache curl

# Create non-root user and group for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy compiled jar from build stage
COPY --from=builder /app/target/project-*.jar app.jar

# Set ownership to non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose HTTP port
EXPOSE 8080

# Configure production JVM flags and environment variables
ENV SPRING_PROFILES_ACTIVE=prod \
    PORT=8080 \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dnet.bytebuddy.experimental=true -XX:+EnableDynamicAgentLoading --enable-native-access=ALL-UNNAMED"

# Healthcheck monitoring Spring Boot Actuator
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:${PORT}/actuator/health || exit 1

# Launch application
ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar app.jar"]
