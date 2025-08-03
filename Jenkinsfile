pipeline {
    agent any

    tools {
        maven 'Maven'
        nodejs 'Node'
    }

    environment {
        DOCKER_IMAGE = 'weather-app'
        DOCKER_TAG = "${BUILD_NUMBER}"
    }

    triggers {
        githubPush()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Backend Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
            post {
                failure {
                    echo 'Backend build failed'
                }
            }
        }

        stage('Frontend Build') {
            steps {
                dir('weather-frontend') {
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
            post {
                failure {
                    echo 'Frontend build failed'
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                sh "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
            }
            post {
                failure {
                    echo 'Docker build failed'
                }
            }
        }
    }


    post {
        success {
            echo 'Pipeline completed successfully'
        }
        failure {
            echo 'Pipeline failed'
        }
    }
}