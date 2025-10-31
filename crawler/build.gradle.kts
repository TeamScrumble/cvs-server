tasks.getByName("bootJar") {
    enabled = true
}

tasks.getByName("jar") {
    enabled = false
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("org.seleniumhq.selenium:selenium-java:4.26.0")
    implementation("org.seleniumhq.selenium:selenium-chrome-driver:4.26.0")

    implementation(project(":storage:storage-kafka:kafka-event"))
}