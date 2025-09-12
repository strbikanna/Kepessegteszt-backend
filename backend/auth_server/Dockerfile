FROM gradle:8.2.1-jdk17 AS builder
WORKDIR /workspace/app

COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
COPY src ./src

RUN gradle clean build -x test

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /opt/app
ENV URL=https://coglica.aut.bme.hu/auth \
    DATABASE_URL=host.docker.internal
EXPOSE 9000
COPY --from=builder /workspace/app/build/libs/auth_server-0.0.1-SNAPSHOT.jar /opt/app/app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=container", "-jar", "/opt/app/app.jar"]
