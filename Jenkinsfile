pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh 'gradle assemble'
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