FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY staging/Devops-vg-1.*.jar /app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]