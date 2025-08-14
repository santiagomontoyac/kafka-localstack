#!/bin/bash

echo "🚀 Deploying stack to LocalStack..."

# Export dummy credentials for LocalStack
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=us-east-1

aws --endpoint-url=http://localhost:4566 \
    cloudformation create-stack \
    --stack-name kafka-stack \
    --template-body file://infra/stack.yaml

echo "✅ Stack deployed!"
