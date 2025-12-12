# Stage 1: 의존성 설치 스테이지
FROM eclipse-temurin:21-jre-alpine AS dependencies

# Chromium 및 필수 패키지 설치
RUN apk add --no-cache \
    chromium \
    chromium-chromedriver \
    # 폰트 및 그래픽 라이브러리
    ttf-freefont \
    font-noto-emoji \
    # 추가 필수 라이브러리
    freetype \
    harfbuzz \
    ca-certificates \
    nss \
    && rm -rf /var/cache/apk/*

# Stage 2: 최종 실행 이미지
FROM eclipse-temurin:21-jre-alpine

# Stage 1에서 설치한 Chromium 관련 파일들 복사
COPY --from=dependencies /usr/bin/chromium-browser /usr/bin/chromium-browser
COPY --from=dependencies /usr/bin/chromedriver /usr/bin/chromedriver
COPY --from=dependencies /usr/lib/chromium /usr/lib/chromium
COPY --from=dependencies /usr/share/fonts /usr/share/fonts
COPY --from=dependencies /etc/chromium /etc/chromium

# 런타임 의존성만 설치
RUN apk add --no-cache \
    freetype \
    harfbuzz \
    ca-certificates \
    nss \
    curl \
    libstdc++ \
    libgcc \
    && rm -rf /var/cache/apk/*

# 사용자 및 그룹 생성
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Chromium 실행을 위한 디렉토리 권한 설정
RUN mkdir -p /tmp/.X11-unix \
    && chmod 1777 /tmp/.X11-unix \
    && mkdir -p /home/spring/.config/chromium \
    && chown -R spring:spring /home/spring

WORKDIR /app

# 애플리케이션 JAR 복사
COPY --chown=spring:spring build/libs/*.jar app.jar

USER spring:spring

EXPOSE 8084

# Chromium 환경변수 설정
ENV CHROME_BIN=/usr/bin/chromium-browser \
    CHROME_DRIVER=/usr/bin/chromedriver

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Dwebdriver.chrome.driver=/usr/bin/chromedriver", \
    "-jar", "app.jar"]