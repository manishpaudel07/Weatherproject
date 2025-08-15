# Stage 1: Build the Spring Boot backend
FROM maven:3.9.6-eclipse-temurin-21 AS backend-build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Build the React frontend
FROM node:18 AS frontend-build
WORKDIR /app
# First copy only package files for better caching
COPY weather-frontend/package*.json ./
# Remove package-lock.json to avoid conflicts
RUN rm -f package-lock.json
# Install dependencies and update lockfile
RUN npm install
# Copy the rest of the application
COPY weather-frontend/ .
# Build the application
RUN npm run build

# Stage 3: Final image
FROM openjdk:21-slim
RUN apt-get update && apt-get upgrade -y
WORKDIR /app
COPY --from=backend-build /app/target/*.jar app.jar
COPY --from=frontend-build /app/build /app/static
EXPOSE 8081
CMD ["java", "-jar", "app.jar"]