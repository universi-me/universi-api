FROM maven:3.8.4-openjdk-17 as BUILD

EXPOSE 8080

WORKDIR /opt/universi-api

COPY pom.xml .

RUN mvn dependency:go-offline

COPY . .

RUN mvn clean install -DskipTests

ENTRYPOINT ["java","-jar","/opt/universi-api/target/universi-api-0.0.1-SNAPSHOT.jar"]