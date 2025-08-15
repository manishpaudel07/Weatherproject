# Stage 1: Build the Spring Boot backend
FROM maven:3.9.6-eclipse-temurin-21 AS backend-build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Build the React frontend
FROM node:18 AS frontend-build
WORKDIR /app
COPY weather-frontend/package*.json ./
RUN npm install
COPY weather-frontend/ .
RUN npm run build

# Stage 3: Final image
FROM openjdk:21-slim
WORKDIR /app
COPY --from=backend-build /app/target/*.jar app.jar
COPY --from=frontend-build /app/build /app/static
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]