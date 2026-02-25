# Multi-stage Dockerfile for building and running a Spring Boot application, 2 stages: build jar and then run the jar.

FROM maven:3.9.9-amazoncorretto-21 AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package

FROM amazoncorretto:21-alpine AS runner

WORKDIR /app

COPY --from=builder ./app/target/billing-service-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8080
EXPOSE 9002
EXPOSE 8081

# Add environment variable to enable remote debugging
ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8081"

ENTRYPOINT ["java", "-jar", "app.jar"]