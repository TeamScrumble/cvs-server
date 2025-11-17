# --- Build Stage ---
FROM gradle:8.8-jdk21 AS builder
WORKDIR /app

# 루트 cvs-server 전체 복사 (build.context가 cvs-server라면 .으로 충분)
COPY . .

# 루트 기준으로 crawler 모듈 빌드
RUN gradle :crawler:bootJar --no-daemon

# --- Runtime Stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# 빌드된 JAR 복사
COPY --from=builder /app/crawler/build/libs/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]