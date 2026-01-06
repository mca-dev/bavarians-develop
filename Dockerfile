# Production Dockerfile - Optimized for CI/CD
# This Dockerfile expects a pre-built JAR file from GitHub Actions
# For local development, use Dockerfile.local instead (builds JAR inside Docker)

FROM openjdk:11-jre-slim

WORKDIR /app

# Copy the pre-built JAR from the build job
COPY target/bavarians-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]