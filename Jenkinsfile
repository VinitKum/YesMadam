pipeline {

    agent any

    tools {
        maven 'Maven3'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                bat 'mvn clean test "-Dtest=OrderTest,WireMockPaymentTest,AsyncPaymentTest,JDBCValidationTest"'
            }
        }
    }

    post {
        always {
            echo 'Test execution completed'
        }

        success {
            echo 'API Automation Suite PASSED'
        }

        failure {
            echo 'API Automation Suite FAILED'
        }
    }
}
