FROM gradle:8-jdk21-alpine AS builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

# Build the JAR (skip tests for faster container builds; tests run in CI)
RUN gradle clean build -x test --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

# Run as non-root user
RUN addgroup --system spring && adduser --system --group spring

COPY --from=builder /home/gradle/src/build/libs/*.jar app.jar
RUN chown -R spring:spring /app
USER spring:spring

EXPOSE 8083

# Ensure the container uses application-docker.properties
ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]