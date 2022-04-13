FROM openjdk:11-jre-slim AS builder
EXPOSE 4008
COPY build/libs/app.jar app.jar

ENV SPRING_PROFILE 'dev'

ENV TZ=Asia/Seoul
RUN apt-get update ; apt-get upgrade -y ; apt-get install -y tzdata curl

ENTRYPOINT ["java","-jar","-Dspring.profiles.active=${SPRING_PROFILE}", "/app.jar"]
