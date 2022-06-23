#!/usr/bin/env bash

FOR %%f IN (pcs-frontend.yml pcs-compose.yml,mentoring-cockpit.yml,data-proxy.yml,sbf-manager.yml,mobsos-data-processing.yml,las2peer-bootstrap.yml,ethereum.yml) DO (
    docker-compose -f %%f down
)

docker volume rm mentoring-bot_ethdata mentoring-bot_las2peer-nodestorage
pause