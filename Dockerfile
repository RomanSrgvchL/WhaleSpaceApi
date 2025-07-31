FROM openjdk:21-jdk-slim

WORKDIR /app

COPY build/libs/whale-space-api-1.0.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]