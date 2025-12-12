FROM eclipse-temurin:21-jre-alpine

# Chromium 및 ChromeDriver 설치
RUN apk add --no-cache \
    chromium \
    chromium-chromedriver \
    # 필수 의존성
    ttf-freefont \
    font-noto-emoji \
    freetype \
    harfbuzz \
    ca-certificates \
    nss \
    # 추가 필수 라이브러리
    udev \
    ttf-opensans \
    && rm -rf /var/cache/apk/*

# 사용자 및 그룹 생성
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Chromium 실행을 위한 디렉토리 생성 및 권한 설정
RUN mkdir -p /home/spring/.config/chromium \
    && mkdir -p /home/spring/.cache \
    && mkdir -p /tmp/.X11-unix \
    && mkdir -p /dev/shm \
    && chmod 1777 /tmp/.X11-unix \
    && chmod 777 /dev/shm \
    && chown -R spring:spring /home/spring

# ChromeDriver 권한 설정
RUN chmod +x /usr/bin/chromedriver \
    && chmod +x /usr/bin/chromium-browser

WORKDIR /app

# 애플리케이션 JAR 복사
COPY --chown=spring:spring build/libs/*.jar app.jar

# spring 사용자로 전환하기 전에 권한 확인
RUN chown -R spring:spring /app

USER spring:spring

EXPOSE 8084

# 환경변수 설정
ENV CHROME_BIN=/usr/bin/chromium-browser \
    CHROME_DRIVER=/usr/bin/chromedriver \
    DISPLAY=:99

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Dwebdriver.chrome.driver=/usr/bin/chromedriver", \
    "-jar", "app.jar"]