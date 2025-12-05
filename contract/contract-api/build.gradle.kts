plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("io.spring.dependency-management")
}

dependencies {
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation(project(":contract:contract-error"))
    implementation(project(":support:support-docs"))
}

tasks.getByName<Jar>("jar") {
    enabled = true
}