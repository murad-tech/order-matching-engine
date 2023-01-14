FROM maven:3.9.12-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY order-service ./order-service
COPY matching-engine ./matching-engine

RUN mvn clean package -DskipTests -pl order-service -am

FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

COPY --from=build /app/order-service/target/order-service-1.0-SNAPSHOT.jar order-service.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "order-service.jar"]
