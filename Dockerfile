FROM gradle:8.5-jdk21-alpine as builder
WORKDIR /build

# 그래들 파일이 변경되었을 때만 새롭게 의존패키지 다운로드 받게함.
COPY build.gradle settings.gradle /build/
RUN gradle build -x test --parallel --continue > /dev/null 2>&1 || true

# 빌더 이미지에서 애플리케이션 빌드
COPY . /build
RUN gradle build -x test --parallel

# APP
FROM openjdk:21-slim
WORKDIR /app

# 환경 변수 설정
ENV API_KEY=${API_KEY}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV KAKAO_CLIENT_ID=${KAKAO_CLIENT_ID}
ENV KAKAO_CLIENT_SECRET=${KAKAO_CLIENT_SECRET}
ENV KAKAO_REDIRECT_URI=${KAKAO_REDIRECT_URI}
ENV MAIL_PASSWORD=${MAIL_PASSWORD}
ENV MAIL_USERNAME=${MAIL_USERNAME}
ENV WEATHER_API_KEY=${WEATHER_API_KEY}


# 빌더 이미지에서 jar 파일만 복사
COPY --from=builder /build/build/libs/i-cross-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

# root 대신 nobody 권한으로 실행
USER nobody
ENTRYPOINT [ \
   "java", \
   "-jar", \
   "-Djava.security.egd=file:/dev/./urandom", \
   "-Dsun.net.inetaddr.ttl=0", \
   "i-cross-0.0.1-SNAPSHOT.jar" \
]