pipeline {
    agent any
    stages {
        stage('Built') {
            steps {
                sh 'mvn clean install'
                cucumber fileIncludePattern: '**/*.json', jsonReportDirectory: 'target/cucumber', sortingMethod: 'ALPHABETICAL'
            }
        }
    }
}