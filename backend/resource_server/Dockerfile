# Stage 1 - Build Python
FROM python:3.9 AS python-builder

WORKDIR /app

COPY python_module/requirements.txt ./
RUN pip install --upgrade pip && \
    pip install -r requirements.txt

COPY python_module/modellek.py scripts/modellek.py

COPY --from=eclipse-temurin:17 /opt/java/openjdk /opt/java/openjdk
ENV JAVA_HOME=/opt/java/openjdk \
    PATH=/opt/java/openjdk/bin:$PATH
RUN pip install jep

# Stage 2 - Build ResourceServer App
FROM gradle:8.2.1-jdk17 AS gradle-builder

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
COPY src ./src
RUN gradle clean build


# Stage 3 - Deploy ResourceServer App With Python Module
FROM python-builder AS resource-server-app

WORKDIR /app

COPY --from=gradle-builder /app/build/libs/resource_server-0.0.1-SNAPSHOT.jar /resource_server_app/app.jar
COPY thumbnails/ ./thumbnails/
COPY python_module/ ./resource_server_app/python_module/

ENV PATH=/opt/java/openjdk/bin:$PATH \
    PYTHON_PATH=/app/resource_server_app/python_module \
    THUMBNAIL_PATH=/app/thumbnails \
    AUTH_SERVER_URI=https://cognitive-test-auth.azurewebsites.net \
    RESOURCE_SERVER_URL=https://cognitive-test-backend.azurewebsites.net

# Find the location of jep
RUN JEP_LOCATION=$(pip show jep | grep "Location" | awk '{print $2}') && \
    echo "JEP Location: $JEP_LOCATION"

EXPOSE 443
# Run with container profile and jep on path
ENTRYPOINT java -Dspring.profiles.active=container -Djava.library.path=/usr/local/lib/python3.9/site-packages/jep -jar /resource_server_app/app.jar