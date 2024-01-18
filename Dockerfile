FROM maven:3.8.6-openjdk-11-slim as build

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package

FROM apache/nifi:latest
COPY --from=build target/XmlToJsonProcessor-1.0.nar /opt/nifi/nifi-current/lib/XmlToJsonProcessor.nar
