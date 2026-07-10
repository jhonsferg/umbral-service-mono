plugins {
    java
    id("org.springframework.boot") version "4.0.5"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.codesoftlabs"
version = "1.0.0"

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
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-websocket")

    // DevTools
    runtimeOnly("org.springframework.boot:spring-boot-devtools")

    // Database
    runtimeOnly("org.postgresql:postgresql")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // JWT & Security
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    implementation("org.springframework.security:spring-security-crypto")

    // 2FA (TOTP) & QR Code
    implementation("dev.samstevens.totp:totp-spring-boot-starter:1.7.1")
    implementation("com.google.zxing:core:3.5.1")
    implementation("com.google.zxing:javase:3.5.1")

    // Swagger / OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3")

    // Utilities
    implementation("commons-io:commons-io:2.11.0")
    implementation("com.google.guava:guava:32.1.3-jre")
    implementation("org.apache.commons:commons-lang3")

    // JSON Processing
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")

    // User Agent Parser
    implementation("eu.bitwalker:UserAgentUtils:1.21")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("com.h2database:h2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Spring Boot profiles support
if (project.hasProperty("profile")) {
    val profile = project.property("profile") as String
    springBoot {
        mainFunction.set("run")
    }
}
