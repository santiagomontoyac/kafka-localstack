# Kafka Event-Driven POC with LocalStack, Jenkins & Docker

This project is a simulation of a real-world production environment leveraging:

- **Apache Kafka** for event-driven communication
- **AWS services via LocalStack** (S3, SQS, Secrets Manager)
- **Spring Boot Kotlin app**
- **Jenkins** for automated infrastructure provisioning and deployment

---

## 🔧 Project Structure

```
demo-kafka/
├── infra/
│   └── jenkins/
│       ├── Dockerfile
│       ├── jenkins-compose.yml
│       ├── plugins.txt
│       ├── infra-bootstrap.sh
│       └── Jenkinsfile
├── src/
│   └── main/
│       └── resources/
│           └── AwsConfigFiles/
│               ├── EventNotification.json
│               └── example.csv
├── compose.yaml (Main Docker Compose)
├── stack.yaml (CloudFormation stack)
├── build.gradle.kts
└── Dockerfile (Kotlin App)
```

---

## 📦 Technologies Used

- **Kafka + Zookeeper** (Confluent Images)
- **Kafka UI**: http://localhost:8083/
- **LocalStack**: Emulates AWS services locally
- **Jenkins**: http://localhost:8085/
- **Spring Boot (Kotlin)** for producing and consuming messages
- **CloudFormation (via LocalStack)** for infrastructure setup

---

## 🚀 Running the Project Locally

### 1. Create the Docker Network
```bash
docker network create store-net
```

### 2. Start All Services
```bash
docker compose up -d
```

### 3. Run the Kotlin App (optional if not in compose)
```bash
docker compose up --build kafka-app
```

---

## 🔄 LocalStack CLI Commands

### Create S3 Bucket
```bash
aws --endpoint-url=http://localhost:4566 --profile localstack s3 mb s3://kafka-bucket
```

### Create SQS Queue
```bash
aws --endpoint-url=http://localhost:4566 --profile localstack sqs create-queue --queue-name s3-events-queue
```

### Create S3 Event Notification
```bash
aws --endpoint-url=http://localhost:4566 --profile localstack s3api put-bucket-notification-configuration \
  --bucket kafka-bucket \
  --notification-configuration "file://<path>/EventNotification.json"
```

### Validate Notification
```bash
aws --endpoint-url=http://localhost:4566 --profile localstack s3api get-bucket-notification-configuration \
  --bucket kafka-bucket
```

### Create Secret
```bash
aws --endpoint-url=http://localhost:4566 --profile localstack secretsmanager create-secret \
  --name secret-key \
  --description "Token to consume API" \
  --secret-string '{"token":"YOUR_TOKEN_HERE"}'
```

### Upload File to S3
```bash
aws --endpoint-url=http://localhost:4566 --profile localstack s3 cp example.csv s3://kafka-bucket
```

### Purge SQS Queue
```bash
aws --endpoint-url=http://localhost:4566 --profile localstack sqs purge-queue \
  --queue-url http://localhost:4566/000000000000/s3-events-queue
```

---

## 🐳 Jenkins Setup (Local)

### Run Jenkins Docker
```bash
docker compose -f infra/jenkins/jenkins-compose.yml up --build --force-recreate
```

### Initial Login
- Username: `admin`
- Password: Run:
  ```bash
  docker exec -it jenkins cat /var/jenkins_home/secrets/initialAdminPassword
  ```

### Recommended Plugins:
- Docker
- Git
- Pipeline
- Blue Ocean (optional)

---

## 🧪 Jenkins Pipeline (infra/jenkins/Jenkinsfile)

```groovy
pipeline {
  agent any

  stages {
    stage('Start Infra') {
      steps {
        sh 'docker network inspect store-net || docker network create store-net'
        sh 'docker compose up localstack zookeeper kafka kafka-ui'
        sh 'sleep 30'
      }
    }

    stage('Deploy CloudFormation') {
      steps {
        dir('infra') {
          sh 'chmod +x infra-bootstrap.sh'
          sh './infra-bootstrap.sh'
        }
      }
    }

    stage('Deploy Microservices') {
      steps {
        sh 'docker compose up --build kafka-app'
      }
    }
  }
}
```

> ⚠️ Make sure you **remove `-d`** when running `docker compose` in Jenkins stages to avoid issues with background processes that never complete.

---

## 🧪 Testing API

### Sample JSON Payload

```json
{
  "name": "Kafka",
  "description": "Kafkaaaaaaaaaaaa",
  "stock": 20,
  "price": 250.50,
  "idCategory": 1
}
```

### Sample Auth Header

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.ey...
```

---

## 🧹 Docker Utilities

| Action                    | Command                                        |
|---------------------------|------------------------------------------------|
| Create Network            | `docker network create store-net`             |
| Stop All Containers       | `docker stop $(docker ps -q)`                 |
| Remove All Containers     | `docker compose down --rmi all`               |
| Remove Containers Only    | `docker rm $(docker ps -q)`                   |
| Remove All Images         | `docker rmi $(docker images -q)`              |

---

## ✅ Final Notes

- This setup simulates a real CI/CD pipeline with AWS-like resources but entirely **locally**.
- Your infrastructure is defined declaratively (CloudFormation).
- Kafka acts as your event backbone, pushing S3 events to the Kotlin app.
- Jenkins automates both provisioning and service deployment, creating a **realistic local DevOps experience**.

---
