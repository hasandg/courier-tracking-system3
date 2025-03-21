# Courier Tracking System Environment Setup

## Services Overview

The Courier Tracking System consists of the following services:

1. **Config Server** (Port 8889)
    - Provides centralized configuration for all services

2. **Store Service** (Port 8082)
    - Manages store information
    - Tracks courier visits to stores

3. **Location Service** (Port 8084)
    - Records and retrieves courier locations

4. **Distance Calculation Service** (Port 8083)
    - Calculates distances traveled by couriers

## Security Setup

For local development, security is disabled using the following approach:

- Added `SecurityConfigDisabled` classes to all services
- Used `@ConditionalOnProperty(name = "spring.security.enabled", havingValue = "false")` to enable the disabled security
  config
- Added appropriate flags in the startup command to disable security

## Running the Services

### Prerequisites

- Java 17+
- Maven
- PostgreSQL running on port 5432 with database `courier_tracking`
- Redis running on port 6379
- Kafka running on ports 19092, 29092, 39092

### Build and Run

1. **Build all services**:
   ```
   mvn clean package -DskipTests
   ```

2. **Start all services**:
   ```
   ./start-services-local.sh
   ```

3. **Stop all services**:
   ```
   ./stop-services-local.sh
   ```

## API Endpoints

The system provides the following API endpoints:

### Store Service (Port 8082)

- Swagger UI: http://localhost:8082/swagger-ui.html
- API Documentation: http://localhost:8082/v3/api-docs

### Location Service (Port 8084)

- Swagger UI: http://localhost:8084/swagger-ui.html
- API Documentation: http://localhost:8084/v3/api-docs

### Distance Calculation Service (Port 8083)

- Swagger UI: http://localhost:8083/swagger-ui.html
- API Documentation: http://localhost:8083/v3/api-docs

## Testing the System

For testing the system API endpoints, refer to the `endpoint-test-plan.md` file which contains curl commands for all
available endpoints.

## Common Issues and Solutions

1. **Port conflicts**
    - The startup script automatically kills processes using the required ports before starting the services
    - If a service fails to start, check if there's a process already using its port

2. **Security issues**
    - All services are configured to run with security disabled for local development
    - Use the `--spring.security.enabled=false` flag when starting services manually

3. **Database connectivity**
    - Ensure PostgreSQL is running on port 5432
    - The database `courier_tracking` must exist and be accessible

4. **Kafka and Redis connectivity**
    - Ensure Kafka and Redis are running and accessible
    - The startup script configures services to connect to these on their default local ports 