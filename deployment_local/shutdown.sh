#!/usr/bin/env bash

for f in mentoring-cockpit.yml data-proxy.yml sbf-manager.yml mobsos-data-processing.yml las2peer-bootstrap.yml ethereum.yml
do
    docker-compose -f $f down
done

docker volume rm mentoring-bot_ethdata mentoring-bot_las2peer-nodestorage