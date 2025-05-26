FROM openjdk:23-jdk-slim

WORKDIR /app

COPY target/whale-space-api-1.0.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]