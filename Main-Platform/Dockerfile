FROM ubuntu:latest AS builder
LABEL authors="ohnando"

ARG JAR_FILE_PATH=target/Main-Platform-0.0.1-SNAPSHOT.jar

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw package -DskipTests

FROM ubuntu:lastest

WORKDIR /app

COPY --from=builder /app/target/Main-Platform-0.0.1-SNAPSHOT.jar App.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "App.jar"]