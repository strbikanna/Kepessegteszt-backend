
# Stage 1 - Build ResourceServer App
FROM gradle:8.2.1-jdk17 AS gradle-builder

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
COPY src ./src
RUN gradle clean build -x test


# Stage 2 - Deploy ResourceServer App
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=gradle-builder /app/build/libs/resource_server-0.0.1-SNAPSHOT.jar /resource_server_app/app.jar
COPY thumbnails/ ./thumbnails/

ENV THUMBNAIL_PATH=/app/thumbnails \
    AUTH_SERVER_URL=https://coglica.aut.bme.hu/auth \
    RESOURCE_SERVER_URL=https://coglica.aut.bme.hu/api \
    DATABASE_URL=host.docker.internal \
    MISTRAL_API_KEY=

EXPOSE 8090
# Run with container profile and jep on path
ENTRYPOINT java -Dspring.profiles.active=container -jar /resource_server_app/app.jar