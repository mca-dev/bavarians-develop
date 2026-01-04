# Stage 1: Build
FROM maven:3.8-openjdk-11-slim AS build
WORKDIR /build

# Copy all source files
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY src src

# Build application
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:11-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S bavarians && adduser -S bavarians -G bavarians

# Copy the built JAR from build stage
COPY --from=build /build/target/bavarians-3.0.7-SNAPSHOT.jar app.jar

# Change ownership to non-root user
RUN chown bavarians:bavarians app.jar

# Switch to non-root user
USER bavarians

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
