#!/bin/bash

docker run -d \
  --name maria_db \
  -p 13306:3306 \
  -e MYSQL_DATABASE=budget_webapp \
  -e MYSQL_USER=root \
  -e MYSQL_PASSWORD=root \
  -e MYSQL_ROOT_PASSWORD=root \
  mariadb:latest
