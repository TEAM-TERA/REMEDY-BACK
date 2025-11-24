FROM openjdk:26-ea-21-jdk-slim

WORKDIR /app

COPY build/libs/Remedy-0.0.1-SNAPSHOT.jar Remedy-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "Remedy-0.0.1-SNAPSHOT.jar"]