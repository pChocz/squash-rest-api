pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        withGradle() {
          sh './gradlew assemble'
        }

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