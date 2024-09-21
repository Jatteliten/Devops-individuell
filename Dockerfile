FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle gradle

RUN ./gradlew dependencies

COPY src src

RUN ./gradlew build -x test

FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
