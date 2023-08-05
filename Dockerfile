FROM maven:3.8.4-openjdk-17 as BUILD

EXPOSE 8080

COPY . /opt/universi-api

WORKDIR /opt/universi-api

RUN mvn clean install -DskipTests

ENTRYPOINT ["java","-jar","/opt/universi-api/target/universi-api-0.0.1-SNAPSHOT.jar"]