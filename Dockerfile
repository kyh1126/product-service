FROM openjdk:11-jre-slim AS builder
EXPOSE 4008
COPY build/libs/app.jar app.jar

ENV SPRING_PROFILE 'dev'
ENTRYPOINT java -jar \
 -Dspring.profiles.active="${SPRING_PROFILE}" \
 /app.jar \

ENV TZ=Asia/Seoul
RUN apt-get install -y tzdata
