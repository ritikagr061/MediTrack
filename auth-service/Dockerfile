
# -------------------------
# 1) BUILD STAGE
# -------------------------
# Use Maven with Amazon Corretto 21 JDK to compile and package the app.
# Progress so far: We have a clean Linux environment with Maven + JDK 21 ready.
FROM maven:3.9.9-amazoncorretto-21 AS builder

# Set the working directory inside the container.
# Progress so far: We've created /app where all subsequent paths are relative.
WORKDIR /app

# Copy only the Maven descriptor first to maximize layer caching.
# Progress so far: pom.xml is in place; this lets us pre-download deps before copying source.
COPY pom.xml .

# Pre-fetch all dependencies and plugins declared in pom.xml.
# Flags:
#  -q              : quiet (less log noise)
#  -B              : batch mode (non-interactive)
#  -DskipTests     : don't run tests while priming the cache
# Result: Dependencies are cached in a Docker layer; future builds are faster
# as long as pom.xml hasn't changed.
# Progress so far: Maven local repo inside the image now has all required deps.
RUN mvn -q -B -DskipTests dependency:go-offline

# Copy the actual source code (this invalidates the cache only when code changes).
# Progress so far: The full project (src/ + resources) is now present in /app.
COPY src ./src

# Build the fat JAR but SKIP TESTS so Docker builds don't fail on test-only wiring,
# and to keep image builds fast. If you have integration tests, also skip them via -DskipITs.
# Progress so far: A runnable JAR should now exist under /app/target/*.jar.
RUN mvn -q -B -DskipTests -DskipITs package


# -------------------------
# 2) RUNTIME STAGE
# -------------------------
# Lightweight, production runtime: Amazon Corretto 21 (Alpine-based).
# Progress so far: We’re switching to a smaller, cleaner image that only has the JRE.
FROM amazoncorretto:21-alpine AS runner

# Create a working directory for the runtime container.
# Progress so far: /app exists and will hold just the artifacts we need to run.
WORKDIR /app

# Copy the packaged JAR produced in the builder stage, and rename it to app.jar.
# Using wildcard avoids hard-coding the version (e.g., *-SNAPSHOT.jar).
# Progress so far: The runnable artifact (app.jar) is now present in the runtime image.
COPY --from=builder /app/target/*.jar /app/app.jar

# Declare/tells the port your Spring Boot app listens on on the container not the local machine(informational for users/tools).
# it doesn't actually go and run the application on that port, that is controlled by application.properties inside the jar. This simply declares it.
# Progress so far: Consumers of the image know the service expects to listen on 8080.
# (1) Declare both app and debug ports
EXPOSE 8080 6006

# (2) Add environment variable to enable remote debugging
#JAVA_TOOL_OPTIONS → injects the Java Debug Wire Protocol (JDWP) agent into your JVM.
# transport=dt_socket: communicate via TCP socket.
# server=y: JVM listens for a debugger to attach.
# suspend=n: don’t pause the app waiting for debugger.
# address=*:${DEBUG_PORT:-6006}: listen on all interfaces, port 6006 (default).
# The DEBUG_PORT environment variable is optional — if not provided, it defaults to 5005.
ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${DEBUG_PORT:-6006}"

# Start the Spring Boot application.
# Progress so far: The image is fully runnable; `docker run` will launch the app.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
