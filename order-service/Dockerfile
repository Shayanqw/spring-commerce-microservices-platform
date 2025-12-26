# ============================================
# Stage 1: Build Stage
# ============================================
FROM eclipse-temurin:21-jdk AS builder

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Copy source code
COPY src src

# Make gradlew executable
RUN chmod +x gradlew

# Build the application (skip tests for faster builds)
RUN ./gradlew clean build -x test

# ============================================
# Stage 2: Runtime Stage
# ============================================
FROM eclipse-temurin:21-jre

# Set working directory
WORKDIR /app

# Copy JAR from build stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Create non-root user for security
RUN addgroup --system spring && adduser --system --group spring

# Change ownership of app directory
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE 8082

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=docker

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8082/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]