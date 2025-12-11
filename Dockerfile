# ========================
# 1. Builder Stage
# ========================
FROM amazoncorretto:17 AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x gradlew
RUN ./gradlew clean bootJar --no-daemon

# ========================
# 2. Runtime Stage
# ========================
FROM amazoncorretto:17

WORKDIR /app

COPY --from=builder /app/build/libs/app.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]