plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "ca.gbc.comp3095"
version = "0.0.1-SNAPSHOT"
description = "product-service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // TestContainers Bill-of-Materials (BOM)
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.21.3"))

    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Spring Boot DevTools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Spring Boot Testing Starters
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")

    // TestContainers Modules
    testImplementation("org.testcontainers:mongodb")
    testImplementation("org.testcontainers:junit-jupiter")
    //testImplementation("org.testcontainers:redis")

    // REST Assured for testing
    testImplementation("io.rest-assured:rest-assured")

    // Test Platform Launcher
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Week 12 - Swagger/OpenAPI Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8")
    testImplementation("org.springdoc:springdoc-openapi-starter-webmvc-api:2.8.8")
}

tasks.withType<Test> {
    useJUnitPlatform()
}