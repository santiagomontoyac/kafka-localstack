#!/bin/bash
echo "🚀 Deploying stack to LocalStack..."

aws --endpoint-url=http://localhost:4566 --profile localstack \
    cloudformation create-stack \
    --stack-name event-driven-app \
    --template-body file://stack.yaml

echo "✅ Stack deployed!"
