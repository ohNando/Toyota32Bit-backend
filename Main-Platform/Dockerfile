FROM eclipse-temurin:21-jdk-jammy AS builder
LABEL authors="ohnando"

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B
COPY src ./src

RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/target/Main-Platform-0.0.1-SNAPSHOT.jar main-platform-app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "main-platform-app.jar"]