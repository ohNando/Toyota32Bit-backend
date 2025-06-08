FROM eclipse-temurin:21-jdk-jammy AS builder
LABEL authors="ohnando"

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B
COPY src ./src

RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-jammy

ARG FINAL_JAR_NAME=kafka-consumer-app

WORKDIR /app

COPY --from=builder /app/target/Kafka-0.0.1-SNAPSHOT.jar kafka-consumer-app.jar

ENTRYPOINT ["java", "-jar", "kafka-consumer-app.jar"]