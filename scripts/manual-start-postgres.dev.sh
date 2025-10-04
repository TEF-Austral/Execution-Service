#!/usr/bin/env bash

docker run --name=execution_db --rm -p 5435:5432 \
  -e POSTGRES_PASSWORD=password \
  -e POSTGRES_USER=sa \
  -e POSTGRES_DB=execution-db \
  postgres:16