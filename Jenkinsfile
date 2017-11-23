pipeline {
    agent none
    stages {
       stage('Preparation') {
          git 'https://github.com/thewally/cucumber-restassured-wiremock-freemarker.git'
       }
       stage('Build') {
             sh "mvn clean install"
       }
    }
}