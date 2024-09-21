FROM eclipse-temurin:21-jdk-alpine

ARG BUILD_NUMBER

WORKDIR /app

COPY build/libs/Devops-vg-1.${BUILD_NUMBER}.1.jar /app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
