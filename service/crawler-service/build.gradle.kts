tasks.getByName("bootJar") {
    enabled = true
}

tasks.getByName("jar") {
    enabled = false
}

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.seleniumhq.selenium") {
            useVersion("4.26.0")
        }
    }
}

dependencies {
    // --- Spring
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // -- discovery
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    // --- Selenium
    implementation("org.seleniumhq.selenium:selenium-java:4.26.0")
    implementation("org.seleniumhq.selenium:selenium-chrome-driver:4.26.0")
    implementation("org.seleniumhq.selenium:selenium-remote-driver:4.26.0")

    // --- Kafka
    implementation("org.springframework.kafka:spring-kafka")

    // --- Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    // --- Modules
    implementation(project(":contract:contract-event"))
    implementation(project(":support:support-docs"))
    implementation(project(":contract:contract-api"))
    implementation(project(":contract:contract-api:client"))
    implementation(project(":contract:contract-error"))
    implementation(project(":infra:infra-db"))
    implementation(project(":infra:infra-security"))
}