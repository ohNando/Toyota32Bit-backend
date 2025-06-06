FROM ubuntu:latest
LABEL authors="ohnando"

WORKDIR /app

#Copying JAR file to Docker image
COPY target/Toyota-Backend-0.0.1-SNAPSHOT.jar App.jar
#Specify the server port
EXPOSE 8080
#Command that starting the RestApp
ENTRYPOINT ["java", "-jar","App.jar"]