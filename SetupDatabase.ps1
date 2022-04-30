$containerID = & docker ps -aqf "name=^mssql$"

& docker cp .\Docker\CreateModelDatabase.sql $containerID`:/var/opt/mssql

& docker-compose -f .\Docker\sql-compose.yml exec mssql /opt/mssql-tools/bin/sqlcmd -S localhost -U SA -i /var/opt/mssql/CreateModelDatabase.sql

