#!/bin/sh
# wait-for-mariadb.sh

set -e

host="$1"
shift
cmd="$@"

until nc -z -v -w30 $host 3306
do
  echo "Waiting for MariaDB connection..."
  # wait for 5 seconds before check again
  sleep 5
done

>&2 echo "MariaDB is up - executing command"
exec $cmd