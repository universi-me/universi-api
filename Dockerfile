FROM openjdk:11-jre-slim

EXPOSE 8080

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar

RUN ls -a

ENTRYPOINT ["java", "-jar", "app.jar"]