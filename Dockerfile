# Stage 1: Build the React frontend first (so it can be packaged into the backend JAR)
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

# Stage 2: Build the Spring Boot backend and include frontend build into resources/static
FROM maven:3.9.6-eclipse-temurin-21 AS backend-build
WORKDIR /app
COPY pom.xml .
# Copy backend source
COPY src ./src
# Copy frontend build into backend resources so it gets packaged into the final jar
# Create the target directory inside the backend source if it doesn't exist
RUN mkdir -p src/main/resources/static
COPY --from=frontend-build /app/build src/main/resources/static
# Build the backend jar (skip tests for faster builds)
RUN mvn clean package -DskipTests

# Stage 3: Final image
# Use Temurin JRE (Debian-based) which is available on Docker Hub
FROM eclipse-temurin:21-jre-jammy
# Update packages, install certificates and cleanup lists to keep image smaller
RUN apt-get update && apt-get install -y --no-install-recommends ca-certificates && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=backend-build /app/target/*.jar app.jar
EXPOSE 8081
CMD ["java", "-jar", "app.jar"]