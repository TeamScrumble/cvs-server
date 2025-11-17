plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    // --- Webflux
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // --- Jwt
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // --- PasswordEncoder
    implementation("org.springframework.security:spring-security-crypto:6.4.4")

    // --- Modules
    implementation(project(":contract:contract-api"))
}

tasks.getByName<Jar>("jar") {
    enabled = true
}