FROM openjdk:11-jre-slim AS builder
EXPOSE 4008
COPY build/libs/app.jar app.jar

ENV SPRING_PROFILE='dev' \
    TZ='Asia/Seoul'

RUN apt-get update ; apt-get upgrade -y ; apt-get install -y tzdata curl

#HEALTHCHECK --interval=5s --timeout=3s CMD curl -f http://localhost:${SPRING_PORT}/fresh-networks/fn-product/health || exit 1

ENTRYPOINT ["java","-jar","-Dspring.profiles.active=${SPRING_PROFILE}", "/app.jar"]
