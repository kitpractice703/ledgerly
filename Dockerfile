# 1단계: JAR 빌드 (JDK 필요)
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /build

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew bootJar -x test

# 2단계: 실행 이미지 (JRE만 포함 - 경량화)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=builder /build/build/libs/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
