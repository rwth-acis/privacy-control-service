#!/usr/bin/env bash

docker-compose -f ethereum.yml up -d
sleep 120
docker-compose -f las2peer-bootstrap.yml up -d
sleep 180
docker-compose -f mobsos-data-processing.yml up -d
sleep 60
#docker-compose -f sbf-manager.yml up -d
docker-compose -f data-proxy.yml up -d
#sleep 30
#docker-compose -f mentoring-cockpit.yml up -d