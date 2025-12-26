# ============================================
# STAGE 1: BUILD
# Purpose: Compile Java code and create JAR
# ============================================
FROM eclipse-temurin:21-jdk AS build

# Set working directory inside container
WORKDIR /app

# Copy all project files into container
# This includes: src/, build.gradle.kts, gradlew, etc.
COPY . .

# Make gradlew executable (needed on Linux containers)
RUN chmod +x gradlew

# Run Gradle build
# -x test: Skip tests (we already ran them in Week 4)
# --no-daemon: Don't start Gradle daemon (not needed in container)
RUN ./gradlew clean build -x test --no-daemon

# ============================================
# STAGE 2: RUNTIME
# Purpose: Create minimal runtime image
# ============================================
FROM eclipse-temurin:21-jre

# Set working directory
WORKDIR /app

# Create a non-root user for security
# Running as root in containers is a security risk
RUN groupadd -r spring && useradd -r -g spring spring

# Copy JAR file from build stage
# --from=build: Copy from the "build" stage
# Only copy the JAR, not the source code or build tools
COPY --from=build /app/build/libs/*.jar app.jar

# Change ownership of the JAR to the spring user
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring:spring

# Document which port the application listens on
# This doesn't actually open the port (docker-compose does that)
# It's documentation for other developers
EXPOSE 8084

# Set environment variable to activate Docker profile
# This will make Spring Boot use application-docker.properties
ENV SPRING_PROFILES_ACTIVE=docker

# Define the command to run when container starts
# java -jar app.jar: Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]