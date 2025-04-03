FROM maven:3-eclipse-temurin-17 AS build

# Build the application
COPY . .
RUN mvn clean package -DskipTests

# Copy jar file
RUN cp -f target/*.jar /app.jar

# Extract module dependencies from the jar file
RUN jdeps --ignore-missing-deps -q  \
    --recursive  \
    --multi-release 17  \
    --print-module-deps  \
    /app.jar > deps.info

# Create a custom optimized JRE with only the required modules, for free space
RUN jlink \
    --add-modules $(cat deps.info),java.xml,jdk.unsupported,java.sql,java.naming,java.desktop,java.management,java.security.jgss,java.instrument,jdk.crypto.ec \
    --strip-debug \
    --compress 2 \
    --no-header-files \
    --no-man-pages \
    --output /myjre

FROM debian:bookworm-slim

# Setup custom JRE
ENV JAVA_HOME /user/java/jdk17
ENV PATH $JAVA_HOME/bin:$PATH
COPY --from=build /myjre $JAVA_HOME

# Set version in environment variables
ARG BUILD_HASH
ENV BUILD_HASH=${BUILD_HASH}

# Copy the application jar & run script from the build image
COPY --from=build /app.jar /app.jar
COPY --from=build build*.hash /build.hash
COPY --from=build entrypoint.sh /entrypoint.sh

# Install curl
RUN apt-get update && apt-get install -y curl

# Expose port
EXPOSE 8080

RUN ["chmod", "+x", "/entrypoint.sh"]
CMD ["/entrypoint.sh"]