pipeline {
    agent any

    tools {
        maven 'Maven'
        nodejs 'Node'
    }

    triggers{
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
        }

        stage('Frontend Build') {
            steps {
                dir('weather-frontend') {
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t weather-app .'
            }
        }
    }
}