# Multi-stage Dockerfile for building and running a Spring Boot application, 2 stages: build jar and then run the jar.

FROM maven:3.9.9-amazoncorretto-21 AS builder
# Use Maven with JDK 21 as the build environment

WORKDIR /app
# Set the working directory inside the container to /app

COPY pom.xml .
# Copy the pom.xml file to the working directory

RUN mvn dependency:go-offline -B
# Download and cache dependencies so later builds are faster unless pom.xml changes

COPY src ./src
# Copy the source code to the working directory

RUN mvn clean package
# Clean previous builds and package the application into a JAR file

FROM amazoncorretto:21-alpine AS runner
# Use JDK 21 as the runtime environment

WORKDIR /app
# Set the working directory inside the container to /app

COPY --from=builder ./app/target/notification-service-0.0.1-SNAPSHOT.jar ./app.jar
# Copy the packaged JAR file from the builder stage and rename it to app.jar

EXPOSE 8080 8081
# Expose port 8080 for the application and 8081 for remote debugging

# Add environment variable to enable remote debugging
ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8081"

ENTRYPOINT ["java", "-jar", "app.jar"]
# Run the Spring Boot application when the container starts
