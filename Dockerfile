FROM gradle:8.5-jdk21 AS build

WORKDIR /home/gradle/project
COPY --chown=gradle:gradle build.gradle gradle/ ./
COPY --chown=gradle:gradle src/main/ src/main/

RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]