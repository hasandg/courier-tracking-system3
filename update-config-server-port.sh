#!/bin/bash

# Update Config Server port in application.properties/bootstrap.properties for all services
echo "Updating Config Server port in all services..."

# Services to update
services=("store-service" "location-service" "distance-calculation-service")

for service in "${services[@]}"; do
  # Check for application.properties
  if [ -f "$service/src/main/resources/application.properties" ]; then
    echo "Updating $service/src/main/resources/application.properties"
    # Replace or add the Config Server URI
    grep -q "spring.cloud.config.uri" "$service/src/main/resources/application.properties" && \
      sed -i '' 's|spring.cloud.config.uri=http://localhost:8888|spring.cloud.config.uri=http://localhost:8889|g' "$service/src/main/resources/application.properties" || \
      echo "spring.cloud.config.uri=http://localhost:8889" >> "$service/src/main/resources/application.properties"
    
    # Replace spring.config.import if it exists
    grep -q "spring.config.import" "$service/src/main/resources/application.properties" && \
      sed -i '' 's|spring.config.import=optional:configserver:http://localhost:8888|spring.config.import=optional:configserver:http://localhost:8889|g' "$service/src/main/resources/application.properties"
  fi
  
  # Check for bootstrap.properties
  if [ -f "$service/src/main/resources/bootstrap.properties" ]; then
    echo "Updating $service/src/main/resources/bootstrap.properties"
    # Replace or add the Config Server URI
    grep -q "spring.cloud.config.uri" "$service/src/main/resources/bootstrap.properties" && \
      sed -i '' 's|spring.cloud.config.uri=http://localhost:8888|spring.cloud.config.uri=http://localhost:8889|g' "$service/src/main/resources/bootstrap.properties" || \
      echo "spring.cloud.config.uri=http://localhost:8889" >> "$service/src/main/resources/bootstrap.properties"
    
    # Replace spring.config.import if it exists
    grep -q "spring.config.import" "$service/src/main/resources/bootstrap.properties" && \
      sed -i '' 's|spring.config.import=optional:configserver:http://localhost:8888|spring.config.import=optional:configserver:http://localhost:8889|g' "$service/src/main/resources/bootstrap.properties"
  fi
  
  # Check for application.yml
  if [ -f "$service/src/main/resources/application.yml" ]; then
    echo "Updating $service/src/main/resources/application.yml"
    # This is a simple approach - in practice you might want to use a YAML parser
    sed -i '' 's|uri: http://localhost:8888|uri: http://localhost:8889|g' "$service/src/main/resources/application.yml"
    sed -i '' 's|optional:configserver:http://localhost:8888|optional:configserver:http://localhost:8889|g' "$service/src/main/resources/application.yml"
  fi
  
  # Check for bootstrap.yml
  if [ -f "$service/src/main/resources/bootstrap.yml" ]; then
    echo "Updating $service/src/main/resources/bootstrap.yml"
    # This is a simple approach - in practice you might want to use a YAML parser
    sed -i '' 's|uri: http://localhost:8888|uri: http://localhost:8889|g' "$service/src/main/resources/bootstrap.yml"
    sed -i '' 's|optional:configserver:http://localhost:8888|optional:configserver:http://localhost:8889|g' "$service/src/main/resources/bootstrap.yml"
  fi
done

echo "Config Server port updated in all services." 