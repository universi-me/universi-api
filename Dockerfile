FROM openjdk:23-slim-bullseye

# Set version in environment variables
ARG BUILD_HASH
ENV BUILD_HASH=${BUILD_HASH}

# Copy jar file
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app.jar

COPY build.hash /build.hash
COPY entrypoint.sh /entrypoint.sh

RUN sed -i '/^deb .*http/ s|^deb h|deb [trusted=yes] h|' /etc/apt/sources.list
RUN apt-get update && apt-get install -y curl

# Expose port
EXPOSE 8080

RUN ["chmod", "+x", "/entrypoint.sh"]
CMD ["/entrypoint.sh"]