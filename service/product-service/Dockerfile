알겠습니다! CI에서 이미 빌드가 완료된 상태라면 더 단순하고 가벼운 이미지로 만들 수 있습니다.
dockerfileFROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 보안을 위한 전용 사용자 생성
RUN addgroup --system spring && adduser --system spring --ingroup spring

# 빌드된 jar 파일 복사
COPY build/libs/*.jar app.jar

# 소유권 변경
RUN chown spring:spring app.jar

# 비root 사용자로 전환
USER spring:spring

EXPOSE 8083

# JVM 최적화 옵션
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", \
    "app.jar"]