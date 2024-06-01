FROM maven:3-eclipse-temurin-17 as build
# Copy jar file
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app.jar

RUN jdeps --ignore-missing-deps -q  \
    --recursive  \
    --multi-release 17  \
    --print-module-deps  \
    /app.jar > deps.info
RUN jlink \
    --add-modules $(cat deps.info),java.xml,jdk.unsupported,java.sql,java.naming,java.desktop,java.management,java.security.jgss,java.instrument,jdk.crypto.ec \
    --strip-debug \
    --compress 2 \
    --no-header-files \
    --no-man-pages \
    --output /myjre

FROM debian:bookworm-slim

ENV JAVA_HOME /user/java/jdk17
ENV PATH $JAVA_HOME/bin:$PATH
COPY --from=build /myjre $JAVA_HOME

# Set version in environment variables
ARG BUILD_HASH
ENV BUILD_HASH=${BUILD_HASH}

COPY --from=build /app.jar /app.jar
COPY build.hash /build.hash
COPY entrypoint.sh /entrypoint.sh

RUN apt-get update && apt-get install -y curl

# Expose port
EXPOSE 8080

RUN ["chmod", "+x", "/entrypoint.sh"]
CMD ["/entrypoint.sh"]