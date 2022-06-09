$containerID = & docker ps -aqf "name=^pcs-mssql$"

& docker cp .\Docker\CreateModelDatabase.sql $containerID`:/var/opt/mssql

& docker-compose -f .\deployment_local\pcs-compose.yml exec pcs-mssql /opt/mssql-tools/bin/sqlcmd -S localhost -U SA -i /var/opt/mssql/CreateModelDatabase.sql

