#!/bin/bash

IMAGE_NAME="event-handler"
REPOSITORY="nikolaypervukhin"

./mvnw clean package

docker build -t $IMAGE_NAME .
docker tag $IMAGE_NAME $REPOSITORY/$IMAGE_NAME
docker login --username $DOCKER_LOGIN --password $DOCKER_PASSWORD
docker push $REPOSITORY/$IMAGE_NAME