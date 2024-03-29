#!/bin/bash

# Build the Docker image
docker build -t api-service .

# Run the Docker container
docker run -d \
  --env-file ./ignore/backend.env \
  --name api_service \
  -p 8081:8081 \
  --link maria_db:app_db \
  -v "$(pwd):/app" \
  -w /app \
  api-service