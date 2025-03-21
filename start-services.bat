@echo off
echo ===============================================================================
echo IMPORTANT: Make sure Docker services are running first!
echo Run the following command in another terminal window if not already running:
echo docker-compose -f courier_tracking_system_infra/docker-compose.yml up -d
echo ===============================================================================
echo.

REM Start Config Server
echo Starting Config Server...
echo URL: http://localhost:8888
start /B java -jar config-server/target/config-server-0.0.1-SNAPSHOT.jar --logging.level.net.logstash=OFF

REM Wait for Config Server to initialize
echo Waiting for Config Server to initialize...
timeout /t 5 /nobreak >nul

REM Start Location Service
echo.
echo Starting Location Service...
echo URL: http://localhost:8084
echo NOTE: If Location Service fails to start because of OpenAPI bean conflicts, see:
echo       location-service/README-SWAGGER-FIX.md for detailed instructions.
start /B java -jar location-service/target/location-service-0.0.1-SNAPSHOT.jar --logging.level.net.logstash=OFF --spring.main.allow-bean-definition-overriding=true

REM Start Store Service
echo.
echo Starting Store Service...
echo URL: http://localhost:8082
start /B java -jar store-service/target/store-service-0.0.1-SNAPSHOT.jar --logging.level.net.logstash=OFF

REM Start Distance Calculation Service
echo.
echo Starting Distance Calculation Service...
echo URL: http://localhost:8083
start /B java -jar distance-calculation-service/target/distance-calculation-service-0.0.1-SNAPSHOT.jar --logging.level.net.logstash=OFF

echo.
echo All services have been started!
echo.
echo Check individual terminal windows for service logs or issues. 