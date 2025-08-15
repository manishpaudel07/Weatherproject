pipeline {
    agent any

    parameters {
        string(name: 'ROLLBACK_TO', defaultValue: '', description: 'Enter build number to rollback to (e.g., 41)')
        booleanParam(name: 'IS_ROLLBACK', defaultValue: false, description: 'Check to perform a rollback')
    }

    tools {
        maven 'Maven'
        nodejs 'Node'
        jdk 'JDK21'
    }

    environment {
        DOCKER_IMAGE = 'weather-app'
        DOCKER_TAG = "${params.IS_ROLLBACK ? params.ROLLBACK_TO : BUILD_NUMBER}"
    }

    triggers {
        githubPush()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    if (params.IS_ROLLBACK && !params.ROLLBACK_TO) {
                        error('ROLLBACK_TO parameter is required when IS_ROLLBACK is true')
                    }
                }
            }
        }

        stage('Skip Build for Rollback') {
            when {
                expression { params.IS_ROLLBACK == true }
            }
            steps {
                echo "Rollback mode enabled. Skipping build stages and proceeding to deployment with build #${params.ROLLBACK_TO}"
            }
        }

        stage('Backend Build') {
            when {
                expression { return !params.IS_ROLLBACK }
            }
            steps {
                sh '''
                docker run --rm \
                -v $PWD:/app \
                -w /app \
                maven:3.9.6-eclipse-temurin-21 \
                mvn clean package -DskipTests
                '''
            }
            post {
                failure {
                    echo 'Backend build failed'
                }
                success {
                    archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: false
                }
            }
        }

        stage('Backend Tests') {
            when {
                expression { return !params.IS_ROLLBACK }
            }
            steps {
                sh '''
                docker run --rm \
                -v $PWD:/app \
                -w /app \
                maven:3.9.6-eclipse-temurin-21 \
                mvn test
                '''
            }
            post {
                always {
                    publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
                }
                failure {
                    echo 'Backend tests failed'
                }
            }
        }

        stage('Frontend Build') {
            when {
                expression { return !params.IS_ROLLBACK }
            }
            steps {
                sh '''
                docker run --rm \
                -v $PWD/weather-frontend:/app \
                -w /app \
                node:18-alpine \
                sh -c "npm ci && npm run build"
                '''
            }
            post {
                failure {
                    echo 'Frontend build failed'
                }
                success {
                    archiveArtifacts artifacts: 'weather-frontend/build/**/*', allowEmptyArchive: false
                }
            }
        }

        stage('Frontend Tests') {
            when {
                expression { return !params.IS_ROLLBACK }
            }
            steps {
                sh '''
                docker run --rm \
                -v $PWD/weather-frontend:/app \
                -w /app \
                node:18-alpine \
                sh -c "npm ci && npm test -- --coverage --watchAll=false"
                '''
            }
            post {
                always {
                    publishTestResults testResultsPattern: 'weather-frontend/coverage/lcov-report/*.xml'
                }
                failure {
                    echo 'Frontend tests failed'
                }
            }
        }

        stage('Docker Build') {
            when {
                expression { return !params.IS_ROLLBACK }
            }
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                    sh "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
                }
            }
            post {
                failure {
                    echo 'Docker build failed'
                }
                success {
                    echo "Docker image built successfully: ${DOCKER_IMAGE}:${DOCKER_TAG}"
                }
            }
        }

        stage('Security Scan') {
            when {
                expression { return !params.IS_ROLLBACK }
            }
            steps {
                script {
                    try {
                        sh "docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy image --exit-code 1 --severity HIGH,CRITICAL ${DOCKER_IMAGE}:${DOCKER_TAG}"
                    } catch (Exception e) {
                        echo "Security scan found vulnerabilities: ${e.getMessage()}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    if (params.IS_ROLLBACK) {
                        echo "Rolling back to build #${params.ROLLBACK_TO}"
                        sh "docker stop weather-app-container || true"
                        sh "docker rm weather-app-container || true"
                        sh "docker run -d --name weather-app-container -p 8080:8080 ${DOCKER_IMAGE}:${params.ROLLBACK_TO}"
                    } else {
                        echo "Deploying build #${BUILD_NUMBER}"
                        sh "docker stop weather-app-container || true"
                        sh "docker rm weather-app-container || true"
                        sh "docker run -d --name weather-app-container -p 8080:8080 ${DOCKER_IMAGE}:${DOCKER_TAG}"
                    }
                }
            }
            post {
                success {
                    echo "Deployment successful"
                }
                failure {
                    echo "Deployment failed"
                }
            }
        }

        stage('Health Check') {
            steps {
                script {
                    sleep(time: 30, unit: 'SECONDS')
                    def response = sh(script: 'curl -f http://localhost:8080/health || echo "Health check failed"', returnStdout: true).trim()
                    if (response.contains("Health check failed")) {
                        error("Application health check failed")
                    } else {
                        echo "Application is healthy"
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo "Pipeline completed successfully"
        }
        failure {
            echo "Pipeline failed"
        }
        unstable {
            echo "Pipeline completed with warnings"
        }
    }
}