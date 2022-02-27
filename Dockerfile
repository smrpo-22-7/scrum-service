FROM openjdk:11-jre-slim

RUN mkdir /app

WORKDIR /app

ADD ./v1/api/target/ /app

ENV JAVA_ENV=PRODUCTION

ENV KUMULUZEE_ENV_NAME=prod
ENV KUMULUZEE_ENV_PROD=true

ENV KUMULUZEE_LOGS_LOGGERS0_LEVEL=INFO

ENV KUMULUZEE_DATASOURCES0_CONNECTIONURL=jdbc:postgresql://localhost:5432/scrum-service
ENV KUMULUZEE_DATASOURCES0_USERNAME=not_set
ENV KUMULUZEE_DATASOURCES0_PASSWORD=not_set

EXPOSE 8080

CMD ["java", "-server", "-cp", "classes:dependency/*", "com.kumuluz.ee.EeApplication"]
