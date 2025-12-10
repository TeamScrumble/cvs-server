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

dependencies {
    // --- Spring
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // -- discovery
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    // -- Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    // --- Security & JWT
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // --- .env
    implementation("me.paulschwarz:spring-dotenv:4.0.0")

    // --- modules
    implementation(project(":infra:infra-cache"))
    implementation(project(":infra:infra-security"))
    implementation(project(":infra:infra-db"))
    implementation(project(":infra:infra-mail"))
    implementation(project(":contract:contract-api"))
    implementation(project(":contract:contract-api:client"))
    implementation(project(":contract:contract-error"))
    implementation(project(":support:support-docs"))
}