# Weather Project

A full-stack weather application that provides real-time weather information for cities worldwide. The application features a Spring Boot backend REST API and a React frontend, with complete CI/CD pipeline and infrastructure as code.

## 🌟 Overview

This weather application allows users to search for current weather conditions in any city. It fetches real-time weather data from the OpenWeatherMap API and displays temperature, weather description, timezone, and other meteorological information.

## 🏗️ Architecture

The project follows a modern microservices architecture:

- **Backend**: Spring Boot 3.3.3 REST API (Java 21)
- **Frontend**: React 18.2.0 Single Page Application
- **Database**: PostgreSQL (configurable)
- **API Integration**: OpenWeatherMap API
- **Containerization**: Docker multi-stage build
- **CI/CD**: Jenkins pipeline with automated testing and deployment
- **Infrastructure**: Terraform for AWS provisioning

## 🚀 Features

- **Real-time Weather Data**: Fetch current weather information for any city
- **RESTful API**: Clean REST endpoints for weather data retrieval
- **Cross-Origin Support**: CORS enabled for frontend-backend communication
- **API Documentation**: Swagger/OpenAPI integration at `/swagger-ui.html`
- **Responsive UI**: Simple and intuitive React interface
- **Health Checks**: Application health monitoring endpoints
- **Security Scanning**: Trivy integration for container vulnerability scanning
- **Rollback Support**: Jenkins pipeline supports rollback to previous builds

## 💻 Technology Stack

### Backend
- **Framework**: Spring Boot 3.3.3
- **Language**: Java 21
- **Build Tool**: Maven 3.9.6
- **Dependencies**:
  - Spring Web
  - Spring Data JPA
  - PostgreSQL Driver
  - Lombok
  - Jackson Databind
  - SpringDoc OpenAPI

### Frontend
- **Framework**: React 18.2.0
- **HTTP Client**: Axios
- **Build Tool**: Create React App
- **UI Components**: AG Grid (for potential data grid features)
- **Testing**: Jest, React Testing Library

### DevOps
- **Containerization**: Docker
- **CI/CD**: Jenkins
- **Infrastructure**: Terraform (AWS)
- **Security**: Trivy vulnerability scanner

## 📋 Prerequisites

- Java 21 or higher
- Node.js 18 or higher
- Maven 3.9.6 or higher
- Docker (optional, for containerized deployment)
- OpenWeatherMap API key

## 🔧 Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/manishpaudel07/Weatherproject.git
cd Weatherproject
```

### 2. Configure API Key

Set your OpenWeatherMap API key as an environment variable:

```bash
export WEATHER_API_KEY=your_api_key_here
```

Or configure it in `src/main/resources/application.properties`.

### 3. Backend Setup

```bash
# Build the backend
mvn clean package

# Run the backend (default port: 8081)
java -jar target/WeatherApp-1.0-SNAPSHOT.jar
```

### 4. Frontend Setup

```bash
cd weather-frontend

# Install dependencies
npm install

# Start development server (default port: 3000)
npm start
```

## 🐳 Docker Deployment

The project includes a multi-stage Dockerfile for optimized containerized deployment:

```bash
# Build the Docker image
docker build -t weather-app .

# Run the container
docker run -d -p 8081:8081 --name weather-app-container weather-app
```

The Docker image:
- Builds the Spring Boot backend using Maven
- Builds the React frontend using Node.js
- Combines both into a single optimized container with OpenJDK 21 slim
- Serves frontend static files from the backend

## 📡 API Endpoints

### Get Weather Information

```
GET /weather/{city}
```

**Parameters**:
- `city` (path parameter): Name of the city

**Response Example**:
```json
{
  "name": "London",
  "main": {
    "temp": 15.5,
    "feels_like": 14.2,
    "temp_min": 14.0,
    "temp_max": 17.0,
    "pressure": 1013,
    "humidity": 72
  },
  "weather": [
    {
      "description": "partly cloudy"
    }
  ],
  "timezone": 3600
}
```

### API Documentation

Access Swagger UI at: `http://localhost:8081/swagger-ui.html`

## 🏗️ Infrastructure Deployment (AWS)

The project includes Terraform configuration for AWS deployment:

```bash
cd terraform

# Initialize Terraform
terraform init

# Plan the deployment
terraform plan

# Apply the configuration
terraform apply
```

**Resources Created**:
- EC2 instance (t3.micro) running Amazon Linux 2
- Security group with ports 22 (SSH), 3000 (Frontend), 8081 (Backend)
- Public IP address for application access

## 🔄 CI/CD Pipeline

The Jenkins pipeline (`Jenkinsfile`) provides:

1. **Automated Build**: Backend (Maven) and Frontend (npm)
2. **Testing**: Backend unit tests and Frontend tests
3. **Docker Build**: Multi-stage containerization
4. **Security Scanning**: Trivy vulnerability scanning
5. **Deployment**: Automated container deployment
6. **Health Checks**: Post-deployment health verification
7. **Rollback Support**: Ability to rollback to previous builds

### Trigger Pipeline

```bash
# Push to GitHub to trigger automatic build
git push origin main
```

### Rollback to Previous Build

Use Jenkins UI to trigger build with parameters:
- `IS_ROLLBACK`: true
- `ROLLBACK_TO`: build number (e.g., 41)

## 📁 Project Structure

```
Weatherproject/
├── src/main/java/org/example/       # Backend Java source code
│   ├── WeatherApplication.java      # Spring Boot main class
│   ├── WeatherController.java       # REST controller
│   ├── WeatherService.java          # Business logic
│   └── WeatherResponse.java         # Response models
├── src/main/resources/              # Backend resources
│   └── application.properties       # Application configuration
├── weather-frontend/                # React frontend application
│   ├── src/                         # Frontend source code
│   │   └── App.js                   # Main React component
│   ├── public/                      # Static assets
│   └── package.json                 # Frontend dependencies
├── terraform/                       # Infrastructure as Code
│   └── main.tf                      # Terraform AWS configuration
├── Dockerfile                       # Multi-stage Docker build
├── Jenkinsfile                      # CI/CD pipeline definition
└── pom.xml                          # Backend Maven configuration
```

## 🧪 Testing

### Backend Tests

```bash
mvn test
```

### Frontend Tests

```bash
cd weather-frontend
npm test
```

### CI Testing

```bash
cd weather-frontend
npm run test:ci
```

## 🔒 Security

- **Vulnerability Scanning**: Trivy scans Docker images for HIGH and CRITICAL vulnerabilities
- **Security Groups**: AWS security groups restrict access to necessary ports only
- **Environment Variables**: Sensitive data (API keys) stored in environment variables
- **CORS Configuration**: Cross-origin requests properly configured

## 🛠️ Development

### Backend Development

The backend uses:
- Spring Boot for rapid development
- Lombok to reduce boilerplate code
- JPA for database operations (when needed)
- RESTful architecture principles

### Frontend Development

The frontend uses:
- React hooks (useState) for state management
- Axios for HTTP requests
- Responsive design principles

## 📝 Configuration

Key configuration files:
- `application.properties`: Backend configuration (port, API URLs, logging)
- `application-postgres.properties`: PostgreSQL database configuration
- `package.json`: Frontend dependencies and scripts
- `pom.xml`: Backend dependencies and build configuration

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is open source and available for educational and commercial use.

## 👥 Authors

- Manish Paudel - [@manishpaudel07](https://github.com/manishpaudel07)

## 🙏 Acknowledgments

- OpenWeatherMap API for providing weather data
- Spring Boot community for excellent framework
- React community for robust frontend library
