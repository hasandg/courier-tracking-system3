#!/bin/bash

# Stop any running services first
./stop-services-local.sh 2>/dev/null || true

echo "Killing any processes using our required ports..."
# Kill any processes using our required ports
for port in 8889 8082 8086 8083; do
  lsof -i :$port -sTCP:LISTEN -t | xargs kill -9 2>/dev/null || true
done

echo "Checking and creating Kafka topics if needed..."
# Ensure Kafka topics exist
docker exec -it courier-tracking-system3-kafka-1 /opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic courier-locations 2>/dev/null || true

echo "Starting Config Server..."
# Start Config Server
java -jar config-server/target/config-server-0.0.1-SNAPSHOT.jar --server.port=8889 --spring.profiles.active=native > logs/config-server.log 2>&1 &

# Wait for Config Server to start
sleep 15

echo "Starting Store Service..."
# Start Store Service with security disabled
java -jar store-service/target/store-service-0.0.1-SNAPSHOT.jar \
  --server.port=8082 \
  --spring.datasource.url=jdbc:postgresql://localhost:5433/courier_tracking \
  --spring.redis.host=localhost \
  --spring.redis.port=6380 \
  --spring.cloud.config.uri=http://localhost:8889 \
  --logging.config=store-service/src/main/resources/logback-nologstash.xml \
  --spring.profiles.active=dev \
  --spring.main.allow-bean-definition-overriding=true \
  --spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration \
  > logs/store-service.log 2>&1 &

# Wait for Store Service to start
sleep 15

echo "Starting Location Service..."
# Start Location Service with security disabled
java -jar location-service/target/location-service-0.0.1-SNAPSHOT.jar \
  --server.port=8086 \
  --spring.datasource.url=jdbc:postgresql://localhost:5433/courier_tracking \
  --spring.kafka.bootstrap-servers=localhost:9092 \
  --spring.cloud.config.uri=http://localhost:8889 \
  --logging.config=location-service/src/main/resources/logback-nologstash.xml \
  --spring.profiles.active=dev \
  --spring.main.allow-bean-definition-overriding=true \
  --spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration \
  > logs/location-service.log 2>&1 &

# Wait for Location Service to start
sleep 15

echo "Starting Distance Calculation Service..."
# Start Distance Calculation Service with security disabled
java -jar distance-calculation-service/target/distance-calculation-service-0.0.1-SNAPSHOT.jar \
  --server.port=8083 \
  --spring.datasource.url=jdbc:postgresql://localhost:5433/courier_tracking \
  --spring.kafka.bootstrap-servers=localhost:9092 \
  --spring.redis.host=localhost \
  --spring.redis.port=6380 \
  --spring.cloud.config.uri=http://localhost:8889 \
  --logging.config=distance-calculation-service/src/main/resources/logback-nologstash.xml \
  --spring.profiles.active=dev \
  --spring.main.allow-bean-definition-overriding=true \
  --spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration \
  > logs/distance-calculation-service.log 2>&1 &

echo "All services started."
echo "Use './stop-services-local.sh' to stop all services."
echo ""
echo "Service endpoints:"
echo "- Config Server: http://localhost:8889"
echo "- Store Service: http://localhost:8082/swagger-ui.html"
echo "- Location Service: http://localhost:8086/swagger-ui.html"
echo "- Distance Calculation Service: http://localhost:8083/swagger-ui.html" 