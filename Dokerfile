# 1. 빌드 스테이지
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /build

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew bootJar -x test

# 2. 실행 스테이지
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=builder /build/build/libs/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]