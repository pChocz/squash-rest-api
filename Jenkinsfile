pipeline {
  agent {
    docker {
      image 'openjdk:17-alpine'
    }

  }
  stages {
    stage('Build') {
      steps {
        echo 'gradle assemble'
        sh 'chmod +x gradlew'
        sh './gradlew assemble'
      }
    }

    stage('Test') {
      steps {
        sh './gradlew test'
      }
    }

    stage('Build Docker image') {
      steps {
        sh './gradlew docker'
      }
    }

  }
}