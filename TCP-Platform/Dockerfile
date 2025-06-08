FROM eclipse-temurin:21-jdk-alpine
LABEL authors="ohnando"

WORKDIR /app
#Copying JAR file to Docker image
COPY TCPServer.jar app.jar
#Specify the server port
EXPOSE 8080
#Command that starting the TCPApp
ENTRYPOINT ["java", "-jar", "app.jar"]