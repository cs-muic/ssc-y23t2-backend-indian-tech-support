#!/bin/bash

# Build the Docker image
docker build -t api-service .

# Run the Docker container
docker run -d \
  --restart unless-stopped \
  --env-file ./ignore/backend.env \
  --name api-service \
  -p 8081:8081 \
  --link maria_db:app_db \
  -v "$(pwd):/app" \
  -w /app \
  api-service

  # export DOCKER_DEFAULT_PLATFORM=linux/arm64/v8
# export DOCKER_DEFAULT_PLATFORM=linux/amd64/v3

 # docker save api-service > backend.tar  
    # scp -rp ./backend.tar anish@139.59.231.241:~/home
    # docker load --input backend.tar