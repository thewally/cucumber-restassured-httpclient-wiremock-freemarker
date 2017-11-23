pipeline {
    agent none
    stages {
       stage('Preparation') {
          git 'https://github.com/jglick/simple-maven-project-with-tests.git'
       }
       stage('Build') {
             sh "mvn clean install"
       }
    }
}