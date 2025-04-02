FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -T 8 -U -DskipTests
FROM eclipse-temurin:21-jre-alpine
MAINTAINER erdemiryigit
WORKDIR /app
COPY --from=builder /app/target/*.jar brokage-firm.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/brokage-firm.jar"]
