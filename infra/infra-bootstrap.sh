#!/bin/bash

echo "🚀 Deploying stack to LocalStack..."

aws --endpoint-url=http://localhost:4566 \
    --region us-east-1 \
    cloudformation create-stack \
    --stack-name kafka-stack \
    --template-body file://stack.yaml

echo "✅ Stack deployed!"
