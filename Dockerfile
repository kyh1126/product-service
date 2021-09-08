FROM openjdk:11-jre-slim AS builder
EXPOSE 4008
COPY build/libs/app.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]