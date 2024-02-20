FROM openjdk:23-slim-bullseye

# Set version in environment variables
ARG BUILD_HASH
ENV BUILD_HASH=${BUILD_HASH}

# Copy jar file
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app.jar

COPY build.hash /build.hash

# Expose port
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]