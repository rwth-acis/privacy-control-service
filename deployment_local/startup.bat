#!/usr/bin/env bash

docker-compose -f ethereum.yml up -d
timeout /t 20
docker-compose -f las2peer-bootstrap.yml up -d
timeout /t 180
docker-compose -f mobsos-data-processing.yml up -d
timeout /t 15
#docker-compose -f sbf-manager.yml up -d
docker-compose -f data-proxy.yml up -d
#timeout /t 15
#docker-compose -f mentoring-cockpit.yml up -d
timeout /t 10
docker-compose -f pcs-compose.yml up -d
timeout /t 10
docker-compose -f pcs-frontend.yml up -d
pause