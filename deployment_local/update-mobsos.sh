#!/usr/bin/env bash

docker-compose -f mobsos-data-processing.yml down
docker-compose -f data-proxy.yml down
docker-compose -f sbf-manager.yml down
docker-compose -f mentoring-cockpit.yml down
#docker build -t mobsos ../../git_repos/mobsos-data-processing/
docker-compose -f mobsos-data-processing.yml up -d
sleep 30
docker-compose -f data-proxy.yml up -d
docker-compose -f sbf-manager.yml up -d
docker-compose -f mentoring-cockpit.yml up -d
docker logs sbf-manager -f