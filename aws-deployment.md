# AWS Production Deployment Architecture & Readiness

This document outlines the production deployment specification for running the **Plant Care Service Platform Backend** on AWS with zero hardcoded credentials, adhering strictly to IAM roles and AWS best practices.

---

## 1. Architecture Overview

- **Compute**: AWS ECS Fargate (serverless container orchestration) behind an Application Load Balancer (ALB).
- **Container Registry**: AWS ECR (Elastic Container Registry) storing multi-stage Docker images.
- **Database**: AWS RDS PostgreSQL 16 (Multi-AZ with automated backups and encrypted storage).
- **Blob / Asset Storage**: AWS S3 bucket for plant images and inspection before/after photos.
- **Secrets Management**: AWS Secrets Manager (storing RDS credentials and application secrets, mounted via ECS task definition).
- **Observability**: AWS CloudWatch (application logs, container metrics, alarm monitoring).
- **Security**: IAM Task Execution & Task Roles, security groups with minimal egress/ingress.

---

## 2. Environment Variables & Secret Ingestion

All configuration is passed via environment variables (configured in `application-prod.yml`):

| Environment Variable | Description | Source |
|----------------------|-------------|--------|
| `SPRING_PROFILES_ACTIVE` | Active profile (`prod`) | ECS Task Definition |
| `PORT` | Listening HTTP port (`8080`) | ECS Task Definition |
| `DB_URL` | PostgreSQL JDBC URL | Secret ARN (`/prod/plantcare/db_url`) |
| `DB_USERNAME` | PostgreSQL master username | Secret ARN (`/prod/plantcare/db_username`) |
| `DB_PASSWORD` | PostgreSQL master password | Secret ARN (`/prod/plantcare/db_password`) |
| `AWS_REGION` | AWS Region (e.g. `us-east-1`) | ECS Task Definition |
| `S3_BUCKET_NAME` | S3 bucket for plant images | ECS Task Definition |

---

## 3. ECS Task Definition Specification (Fargate)

```json
{
  "family": "plantcare-backend-prod",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "1024",
  "memory": "2048",
  "executionRoleArn": "arn:aws:iam::ACCOUNT_ID:role/ecsTaskExecutionRole",
  "taskRoleArn": "arn:aws:iam::ACCOUNT_ID:role/plantcareTaskRole",
  "containerDefinitions": [
    {
      "name": "backend",
      "image": "ACCOUNT_ID.dkr.ecr.AWS_REGION.amazonaws.com/plantcare-backend:latest",
      "essential": true,
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "environment": [
        { "name": "SPRING_PROFILES_ACTIVE", "value": "prod" },
        { "name": "PORT", "value": "8080" }
      ],
      "secrets": [
        {
          "name": "DB_URL",
          "valueFrom": "arn:aws:secretsmanager:AWS_REGION:ACCOUNT_ID:secret:prod/plantcare/db-credentials:url::"
        },
        {
          "name": "DB_USERNAME",
          "valueFrom": "arn:aws:secretsmanager:AWS_REGION:ACCOUNT_ID:secret:prod/plantcare/db-credentials:username::"
        },
        {
          "name": "DB_PASSWORD",
          "valueFrom": "arn:aws:secretsmanager:AWS_REGION:ACCOUNT_ID:secret:prod/plantcare/db-credentials:password::"
        }
      ],
      "healthCheck": {
        "command": ["CMD-SHELL", "curl -f http://localhost:8080/actuator/health || exit 1"],
        "interval": 30,
        "timeout": 5,
        "retries": 3,
        "startPeriod": 40
      },
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/plantcare-backend-prod",
          "awslogs-region": "AWS_REGION",
          "awslogs-stream-prefix": "backend"
        }
      }
    }
  ]
}
```

---

## 4. IAM Role Permissions

### 4.1 Task Execution Role (`ecsTaskExecutionRole`)
Allows ECS to pull images from ECR, stream logs to CloudWatch, and decrypt secrets:
- `ecr:GetAuthorizationToken`
- `ecr:BatchCheckLayerAvailability`
- `ecr:GetDownloadUrlForLayer`
- `ecr:BatchGetImage`
- `logs:CreateLogStream`, `logs:PutLogEvents`
- `secretsmanager:GetSecretValue`

### 4.2 Application Task Role (`plantcareTaskRole`)
Allows the application container to access AWS services directly without hardcoded keys:
- `s3:PutObject`, `s3:GetObject`, `s3:DeleteObject` on `arn:aws:s3:::plantcare-media-*`
- `cloudwatch:PutMetricData`

---

## 5. Deployment Commands

```bash
# 1. Login to AWS ECR
aws ecr get-login-password --region <region> | docker login --username AWS --password-stdin <account_id>.dkr.ecr.<region>.amazonaws.com

# 2. Build Docker Image
docker build -t plantcare-backend .

# 3. Tag and Push
docker tag plantcare-backend:latest <account_id>.dkr.ecr.<region>.amazonaws.com/plantcare-backend:latest
docker push <account_id>.dkr.ecr.<region>.amazonaws.com/plantcare-backend:latest

# 4. Trigger ECS Rolling Deployment
aws ecs update-service --cluster plantcare-cluster --service plantcare-backend-service --force-new-deployment
```
