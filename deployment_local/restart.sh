#!/usr/bin/env bash

for f in data-proxy.yml sbf-manager.yml mobsos-data-processing.yml las2peer-bootstrap.yml ethereum.yml
do
    docker-compose -f $f down
done

docker volume rm docker-compose-local_ethdata docker-compose-local_las2peer-nodestorage

docker-compose -f ethereum.yml up -d
sleep 20
docker-compose -f las2peer-bootstrap.yml up -d
sleep 230
docker-compose -f mobsos-data-processing.yml up -d
sleep 60
docker-compose -f sbf-manager.yml up -d
docker-compose -f data-proxy.yml up -d