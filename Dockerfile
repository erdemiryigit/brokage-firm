FROM eclipse-temurin:17-jdk-alpine
MAINTAINER erdemiryigit
COPY target/brokage-firm.jar brokage-firm.jar
ENTRYPOINT ["java","-jar","/brokage-firm.jar"]
EXPOSE 8081