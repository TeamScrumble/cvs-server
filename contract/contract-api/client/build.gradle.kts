plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation(project(":contract:contract-api"))
    implementation(project(":contract:contract-error"))

}

tasks.getByName<Jar>("jar") {
    enabled = true
}