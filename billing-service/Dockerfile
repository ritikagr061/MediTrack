# Multi-stage Dockerfile for building and running a Spring Boot application, 2 stages: build jar and then run the jar.

FROM maven:3.9.9-eclipse-temurin-21 AS builder
#Use Maven with Eclipse Temurin JDK 21 as the build environment

WORKDIR /app
# Set the working directory inside the container to /app

COPY pom.xml .
# Copy the pom.xml file to the working directory

RUN mvn dependency:go-offline -B
# mvn command Downloads all the dependencies specified in the pom.xml file, go offline means cache the dependencies unless change is detected.
# -B flag runs Maven in batch mode, which is useful for automated builds.

COPY src ./src
# Copy the source code to the working directory

RUN mvn clean package
# mvn command to clean any previous builds and package the application into a JAR file.

FROM openjdk:21-jdk AS runner
# Use Eclipse Temurin JDK 21 as the runtime environment

WORKDIR /app
# Set the working directory inside the container to /app

COPY --from=builder ./app/target/billing-service-0.0.1-SNAPSHOT.jar ./app.jar
# Copy the packaged JAR file from the builder stage to the working directory
# The --from=builder flag specifies that the file should be copied from the builder stage.
# The source path is ./app/target/patient-service-0.0.1-SNAPSHOT.jar, and the destination path is ./app.jar.
# This results in the JAR file being renamed to app.jar in the runner stage.

EXPOSE 5002
EXPOSE 9002
# Expose port 5001 for the application to listen on
ENTRYPOINT ["java", "-jar", "app.jar"]
# Set the entry point to run the application using the java -jar command
# This command will be executed when the container starts, launching the Spring Boot application.
# The ENTRYPOINT instruction specifies the command that will be run when the container starts.
# Here, it runs the Java application by executing the JAR file created in the previous stage.
# The application will start and listen for incoming requests on the specified port (4000).
