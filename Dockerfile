FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy all dependencies
COPY build/libs/*.jar app.jar
COPY build/dependency/* /app/

ENTRYPOINT ["java", "-jar", "app.jar"]
