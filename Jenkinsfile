pipeline {
    agent any

    triggers {
        // Runs every day at 8:00 AM — adjust cron as needed
        cron('0 8 * * *')
    }

    tools {
        maven 'Maven'   // must match the name in Jenkins → Global Tool Configuration
        jdk   'JDK21'   // must match the name in Jenkins → Global Tool Configuration
    }

    environment {
        ALLURE_RESULTS = 'target/allure-results'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/YOUR_USERNAME/YOUR_REPO.git'
                    // If private repo, use credentialsId:
                    // credentialsId: 'github-token',
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile -B -q'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test -B -q'
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