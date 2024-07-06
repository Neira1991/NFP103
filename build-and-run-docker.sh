#!/bin/bash

./gradlew build -x test
docker build -t query-orchestrator:latest .
docker run \
 -v ~/.config/gcloud:/root/.config/gcloud \
 -m 1G \
 -p 8081:8080 \
 --name query-orchestrator \
 -e LOG_LEVEL=DEBUG \
 query-orchestrator:latest
