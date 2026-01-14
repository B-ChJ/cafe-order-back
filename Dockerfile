FROM gradle:8.5.0-jdk17-alpine AS build

WORKDIR /home/gradle/src

COPY --chown=gradle:gradle . .

RUN ./gradlew bootJar

FROM amazoncorretto:17-alpine

WORKDIR /app

COPY --from=build /home/gradle/src/build/libs/*.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
