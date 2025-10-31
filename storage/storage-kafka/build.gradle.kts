dependencies {
    implementation("org.springframework.kafka:spring-kafka")
    implementation(project(":storage:storage-kafka:kafka-event"))
}