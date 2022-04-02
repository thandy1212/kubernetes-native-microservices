#!/bin/sh

# Sets up shell environment to point to local docker
eval "$(minikube -p minikube docker-env)"
# deploys the service locally to minikube
./mvnw clean package "-Dquarkus.kubernetes.deploy=true"