#!/bin/sh

set -e

DB_TIMEOUT=${DB_TIMEOUT:-30}

echo "Waiting for PostgreSQL to be ready..."
i=0
until pg_isready -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -t 1; do
  sleep 1
  i=$((i+1))
  if [ $i -ge $DB_TIMEOUT ]; then
    echo "PostgreSQL not ready - aborting"
    exit 1
  fi
done

echo "PostgreSQL is ready - starting application"
exec java -jar /app/app.jar