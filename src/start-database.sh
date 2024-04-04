#!/bin/bash

docker run -p 127.0.0.1:13306:3306 --name project_mariadb -v /data:/var/lib/mysql -e MARIADB_ROOT_PASSWORD=root -d --restart=always mariadb:10