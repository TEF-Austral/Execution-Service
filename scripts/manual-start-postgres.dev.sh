#!/usr/bin/env bash

docker run --name=postgres_db_execution --rm -p 5432:5432 \
  -e POSTGRES_PASSWORD=password \
  -e POSTGRES_USER=sa \
  -e POSTGRES_DB=execution-db \
  postgres:16