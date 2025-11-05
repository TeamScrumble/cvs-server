# --- Build Stage ---
FROM gradle:8.8-jdk21 AS builder
WORKDIR /app

COPY . .
RUN gradle :core:core-api:bootJar --no-daemon

# --- Runtime Stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/core/core-api/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]