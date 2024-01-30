FROM openjdk:23-slim-bullseye

EXPOSE 8080

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]