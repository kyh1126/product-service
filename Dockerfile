FROM openjdk:11-jre-slim AS builder
EXPOSE 4008
COPY build/libs/app.jar app.jar

ENV SPRING_PROFILE='dev' \
    SPRING_PORT='4008' \
    TZ='Asia/Seoul'

EXPOSE ${SPRING_PORT}

RUN apt-get update ; apt-get upgrade -y ; apt-get install -y tzdata curl

HEALTHCHECK --interval=10s --timeout=3s --start-period=200s --retries=10  CMD curl -f http://localhost:${SPRING_PORT}/fresh-networks/fn-product/health || exit 1

ENTRYPOINT ["java","-jar","-Dspring.profiles.active=${SPRING_PROFILE}", "/app.jar"]