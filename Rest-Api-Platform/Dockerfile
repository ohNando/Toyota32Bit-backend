FROM maven:3.9.4-eclipse-temurin-21 AS builder
LABEL authors="ohnando"

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine
LABEL authors="ohnando"

WORKDIR /app

#Copying JAR file to Docker image
COPY --from=builder /app/target/*.jar app.jar
#Specify the server port
EXPOSE 8080
#Command that starting the RestApp
ENTRYPOINT ["java", "-jar","app.jar"]