FROM openjdk:11-jre-slim AS builder
EXPOSE 4008
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]