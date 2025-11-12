# 빌드 스테이지
FROM openjdk:21-slim

WORKDIR /app

# JAR 파일 복사
COPY build/libs/*.jar app.jar

# 컨테이너 포트 노출
EXPOSE 8080

# 힙 메모리 설정 및 기타 JVM 옵션
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"] 