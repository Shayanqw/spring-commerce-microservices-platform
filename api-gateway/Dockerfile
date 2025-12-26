# ------------------
#  Build Stage
# ------------------
FROM gradle:8-jdk21-alpine AS builder

COPY --chown=gradle:gradle . /home/gradle/src

WORKDIR /home/gradle/src

RUN gradle build -x test

# ------------------
#  Package Stage
# ------------------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Run as non-root user
RUN addgroup --system spring && adduser --system --group spring

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/api-gateway.jar

RUN chown -R spring:spring /app
USER spring:spring

EXPOSE 9001

# Ensure the container uses application-docker.properties
ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "/app/api-gateway.jar"]