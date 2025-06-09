FROM eclipse-temurin:21-jdk-alpine
LABEL authors="ohnando"

WORKDIR /app

# copying source file to container
COPY src ./src
COPY META-INF ./META-INF

# compiling and creating .jar file
RUN javac -d out $(find src -name "*.java")

RUN cp src/config.properties out/

RUN jar cfm TCPServer.jar META-INF/MANIFEST.MF -C out .

# using port:8080
EXPOSE 8080

# run the app
ENTRYPOINT ["java", "-jar", "TCPServer.jar"]