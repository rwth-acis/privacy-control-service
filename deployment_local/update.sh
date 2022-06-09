#!/usr/bin/env bash

docker-compose -f sbf-manager.yml down
docker build -t sbf-manager ../repositories/las2peer-social-bot-manager-service
docker-compose -f sbf-manager.yml up -d
docker logs sbf-manager -f