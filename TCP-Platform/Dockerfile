FROM eclipse-temurin:21-jre-jammy
LABEL authors="ohnando"

WORKDIR /app
#Copying JAR file to Docker image
COPY TCPServer.jar tcp-platform.jar
#Specify the server port
EXPOSE 8081
#Command that starting the TCPApp
ENTRYPOINT ["java", "-jar", "tcp-platform.jar"]