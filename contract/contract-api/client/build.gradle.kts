plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework:spring-webflux:6.2.2")
    implementation("io.projectreactor.netty:reactor-netty:1.1.19")

    implementation(project(":contract:contract-api"))

}

tasks.getByName<Jar>("jar") {
    enabled = true
}