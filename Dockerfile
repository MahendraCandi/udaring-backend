FROM maven:3-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17
WORKDIR /app
COPY --from=build /app/target/*.jar /application.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/application.jar"]
