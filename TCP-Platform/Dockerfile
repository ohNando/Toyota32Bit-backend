FROM ubuntu:latest
LABEL authors="ohnando"

WORKDIR /app
#Copying JAR file to Docker image
COPY TCPServer.jar App.jar
#Specify the server port
EXPOSE 8081
#Command that starting the TCPApp
ENTRYPOINT ["java", "-jar", "App.jar"]