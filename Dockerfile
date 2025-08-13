# ======================
# Stage 1: Build
# ======================
FROM gradle:8.5.0-jdk17 AS build
WORKDIR /app

# Copy everything (including Gradle wrapper)
COPY . .

# Build the application
RUN gradle clean build -x test

# ======================
# Stage 2: Run
# ======================
FROM amazoncorretto:17.0.11
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the port used by your Spring Boot app
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
