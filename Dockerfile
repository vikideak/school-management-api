FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY . .
RUN ./gradlew build -x test
EXPOSE 8080
ENTRYPOINT ["java","-jar","build/libs/school-management-api-0.0.1-SNAPSHOT.jar"]