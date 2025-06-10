#!/bin/bash

mvn clean package

docker compose -f src/main/docker/docker-compose.yaml build
docker compose -f src/main/docker/docker-compose.yaml up
