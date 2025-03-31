# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the packaged jar file into the container
COPY target/*.jar app.jar

# Make port 8080 available to the world outside this container
EXPOSE 8081

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]