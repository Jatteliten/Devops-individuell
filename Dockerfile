FROM amazoncorretto-21 AS build

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle gradle

RUN ./gradlew dependencies

COPY src src

RUN ./gradlew build -x test

FROM amazoncorretto-21

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
