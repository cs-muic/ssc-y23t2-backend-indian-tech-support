#!/bin/bash
# -e MYSQL_USER=root \
# -e MYSQL_PASSWORD=root \

-p 172.17.0.1:13306:3306 \

docker run -d \
  --name maria_db \
  -p 13306:3306 \
  -p 3306:3306 \
  -e MYSQL_DATABASE=budget_webapp \
  -e MARIADB_ROOT_PASSWORD=root \
  -e MYSQL_ROOT_PASSWORD=root \
  -v /data:/var/lib/mysql \
  mariadb:latest

  # docker exec -it maria_db mariadb --user root -proot