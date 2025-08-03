# Stage 1: Build the Spring Boot backend
FROM maven:3.8.4-openjdk-17 AS backend-build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Build the React frontend
FROM node:16 AS frontend-build
WORKDIR /app
COPY weather-frontend/package*.json ./
RUN npm install
COPY weather-frontend/ .
RUN npm run build

# Stage 3: Final image
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=backend-build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]