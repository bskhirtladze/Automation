pipeline {
    agent any

    triggers {

        cron('0 8 * * *')
    }

    tools {
        maven 'Maven'
        jdk   'JDK21'
    }

    environment {
        ALLURE_RESULTS = 'target/allure-results'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/bskhirtladze/Automation.git'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean compile -B -q'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn test -B -q'
            }
            post {
                always {
                    // Publish TestNG XML results
                    junit testResults: 'target/surefire-reports/*.xml',
                          allowEmptyResults: true
                }
            }
        }

        stage('Allure Report') {
            steps {
                allure([
                    includeProperties: false,
                    jdk              : '',
                    results          : [[path: "${ALLURE_RESULTS}"]],
                    reportBuildPolicy: 'ALWAYS'
                ])
            }
        }
    }

    post {
        always {
            echo "Pipeline finished — Status: ${currentBuild.currentResult}"
            cleanWs()   // clean workspace after each run
        }
        failure {
            echo "Build FAILED — check Allure report for details"
        }
        success {
            echo "All tests passed ✅"
        }
    }
}