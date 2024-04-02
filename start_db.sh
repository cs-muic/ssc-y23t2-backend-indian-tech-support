#!/bin/bash
# -e MYSQL_USER=root \
# -e MYSQL_PASSWORD=root \

docker run -d \
  --name maria_db \
  -p 13306:3306 \
  -p 3306:3306 \
  -p 172.17.0.1:13306:3306 \
  -e MYSQL_DATABASE=budget_webapp \
  -e MYSQL_ROOT_PASSWORD=root \
  mariadb:latest