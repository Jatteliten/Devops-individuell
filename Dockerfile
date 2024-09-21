FROM gradle:8.9-alpine as gradle-build

COPY ./ /app/

WORKDIR /app

RUN gradle clean build -x test --profile production

FROM amazoncorretto:21-alpine

COPY --from=gradle-build /app/build/libs/Devops-vg-0.0.1-SNAPSHOT.jar /app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app.jar"]
