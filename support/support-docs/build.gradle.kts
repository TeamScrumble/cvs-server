plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    api("org.springdoc:springdoc-openapi-starter-webflux-api:2.6.0")

    implementation(project(":contract:contract-error"))
}

tasks.getByName<Jar>("jar") {
    enabled = true
}