FROM eclipse-temurin:21-jre-alpine
MAINTAINER erdemiryigit
COPY docker/brokage-firm.jar brokage-firm.jar
ENTRYPOINT ["java","-jar","/brokage-firm.jar"]
EXPOSE 8081