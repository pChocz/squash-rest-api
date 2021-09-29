pipeline {
  agent {
    node {
      label 'master'
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