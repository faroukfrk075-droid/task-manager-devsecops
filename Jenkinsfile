pipeline {
    agent any

    tools {
        maven 'Maven 3.9.11'
    }

    environment {
        DOCKER_IMAGE = 'farouk1234/task-manager'
        IMAGE_TAG    = "${env.BUILD_NUMBER}"
        DOCKER_CREDS = 'dockerhub-creds'
    }

    stages {
        stage('1. Checkout') {
            steps {
                checkout scm
            }
        }

        stage('2. Build & Unit Tests') {
            steps {
                sh 'mvn clean test package'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('3. SonarQube Analysis (SAST)') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar'
                }
            }
        }

        stage('4. OWASP Dependency-Check') {
            steps {
                sh 'mvn org.owasp:dependency-check-maven:check'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'target/dependency-check-report.*', allowEmptyArchive: true
                }
            }
        }

        stage('5. Build Docker Image') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE}:${IMAGE_TAG} -t ${DOCKER_IMAGE}:latest ."
            }
        }

        stage('6. Trivy Image Scan') {
            steps {
                sh """
                    trivy image --timeout 15m --exit-code 0 --severity HIGH,CRITICAL --format table -o trivy-report.txt ${DOCKER_IMAGE}:${IMAGE_TAG}
                    cat trivy-report.txt
                """
            }
            post {
                always {
                    archiveArtifacts artifacts: 'trivy-report.txt', allowEmptyArchive: true
                }
            }
        }

        stage('7. Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: "${DOCKER_CREDS}", usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh """
                        echo "\$DOCKER_PASS" | docker login -u "\$DOCKER_USER" --password-stdin
                        docker push ${DOCKER_IMAGE}:${IMAGE_TAG}
                        docker push ${DOCKER_IMAGE}:latest
                        docker logout
                    """
                }
            }
        }

        stage('8. Deploy to Kubernetes (Minikube)') {
            steps {
                sh """
                    kubectl apply -f k8s/deployment.yaml
                    kubectl apply -f k8s/service.yaml
                    kubectl set image deployment/task-manager-deployment task-manager=${DOCKER_IMAGE}:${IMAGE_TAG}
                    kubectl rollout status deployment/task-manager-deployment --timeout=120s
                """
            }
        }

        stage('9. OWASP ZAP Dynamic Scan (DAST)') {
            steps {
                script {
                    def minikubeIp = sh(script: "kubectl get nodes -o jsonpath='{.items[0].status.addresses[?(@.type=="InternalIP")].address}'", returnStdout: true).trim()
                    def targetUrl  = "http://${minikubeIp}:30080"
                    echo "Running OWASP ZAP Baseline Scan against ${targetUrl}"
                    sh """
                        mkdir -p zap-reports
                        chmod 777 zap-reports
                        docker run --rm --network host -v \$(pwd)/zap-reports:/zap/wrk/:rw ghcr.io/zaproxy/zaproxy:stable zap-baseline.py -t ${targetUrl} -r zap-report.html || true
                    """
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: 'zap-reports/zap-report.html', allowEmptyArchive: true
                }
            }
        }
    }

    post {
        success {
            echo 'DevSecOps Pipeline completed successfully!'
        }
        failure {
            echo 'DevSecOps Pipeline failed. Check stage logs for details.'
        }
    }
}
