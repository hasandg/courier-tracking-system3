# Courier Tracking System

## Project Requirements

The main requirements were:

1. Build a RESTful API that can handle streaming courier location updates
2. Detect and log when couriers come within 100m of Migros store locations
3. Don't count repeated store visits if they happen within a minute of each other
4. Provide a way to query total distance traveled by any courier

## Architecture Overview

I went with a microservices approach to make the system scalable and easier to maintain. The main services are:

- **Location Service**: Processes and stores courier locations
- **Distance Calculation Service**: Tracks courier movements and calculates distances
- **Store Service**: Handles store data and proximity detection
- **Discovery Service**: Eureka server for service registration
- **Config Server**: Centralized configuration management
- **API Gateway**: Entry point for external API calls

The services communicate both through REST APIs (using Feign clients) and asynchronously via Kafka events.

## Key Features

- **Real-time tracking**: Uses Kafka to stream courier location events between services
- **Store entry detection**: Efficiently calculates when couriers are within 100m of stores using geospatial formulas
- **Duplicate entry prevention**: Tracks recent store visits to prevent counting multiple entries within 1 minute
- **Distance calculation**: Uses the haversine formula to calculate distances between GPS coordinates
- **Caching with Redis**: Improves performance by caching frequently accessed data
- **Resilient design**: Implements circuit breakers to handle service failures gracefully
- **Centralized logging**: ELK stack for log aggregation, analysis, and visualization
- **Comprehensive monitoring**: Prometheus and Grafana for metrics collection and visualization

## Tech Stack

- Spring Cloud 2024.0.0
- Spring Boot 3.4.2
- Java 21
- JUnit 5 and Mockito for testing
- PostgreSQL for persistent storage
- Redis for caching
- Kafka for event streaming
- Resilience4j for circuit breaking
- Spring Cloud Netflix for service discovery
- Docker & Docker Compose for containerization
- Maven for building
- ELK Stack (Elasticsearch, Logstash, Kibana) for centralized logging
- Prometheus and Grafana for monitoring and alerting

## Design Patterns I Used

Some of my implemented design patterns to make the code more maintainable:

1. **Circuit Breaker Pattern**: Used Resilience4j to prevent cascading failures when services are down. This has saved
   me a lot of headaches during testing with intentionally failed services.

2. **Observer Pattern**: Implemented through Kafka to allow services to react to location events asynchronously.

## Data Flow

1. Location data is received via the REST API.
2. The Location Tracking Service processes the data.
3. The Store Entry Service checks if the courier is within 100 meters of any store.
4. If a courier enters a store's vicinity, an entry log is created (if not a reentry within 1 minute).
5. The Distance Calculation Service updates the total distance traveled by the courier.
6. Distance queries are served from the stored distance data.

## Kafka Event Streaming

The system uses Kafka for real-time event streaming between services:

1. **Location Events**:
    - **Producer**: Location Service publishes location updates when couriers report their position
    - **Consumer**: Distance Calculation Service consumes these events to calculate travel distances
    - **Consumer**: Store Service consumes these events to detect proximity to stores

The Kafka topics are configured with appropriate partitioning (6 partitions) and replication (3 replicas)
to ensure scalability and fault tolerance.

## Redis Caching

Redis is used for two primary purposes:

1. **Performance Optimization**:
    - The Distance Calculation Service caches total travel distances to avoid expensive recalculations
    - Cache entries expire after a configurable TTL (3600 seconds by default)
    - Keys follow the pattern `courier-total-distance:{courierId}`

2. **Store Entry Deduplication**:
    - The Store Service uses Redis to track recent store entries by couriers
    - Prevents counting multiple entries within the configured timeout period (60 seconds)
    - Keys follow the pattern `store-entry:{courierId}:{storeId}`

## PostgreSQL earthdistance Extension

This project leverages PostgreSQL's `earthdistance` extension for efficient geospatial calculations. The `earthdistance`
module enables accurate great-circle distance calculations directly in the database.

I implemented this in the `StoreRepository` to efficiently find stores near a courier's location:

```java
@Query(value = "SELECT * FROM stores s " +
        "WHERE earth_box(ll_to_earth(?1, ?2), ?3) @> ll_to_earth(s.latitude, s.longitude)",
        nativeQuery = true)
List<Store> findStoresNearby(double latitude, double longitude, double radiusInMeters);
```

This query uses `earth_box` and `ll_to_earth` functions to create a bounding box for initial filtering, followed by
accurate distance calculation—much more efficient than calculating distances for every store in application code.

The extension provides two approaches:

1. **Cube-based**: For higher precision (used in this project)
2. **Point-based**: Simpler longitude/latitude calculations

By pushing geospatial calculations to the database layer, we gain improved performance, better accuracy, and can
leverage PostgreSQL's spatial indexing capabilities.

For more information, see
the [PostgreSQL earthdistance documentation](https://www.postgresql.org/docs/current/earthdistance.html).

## ELK Stack Integration

The project includes a complete ELK stack (Elasticsearch, Logstash, Kibana) setup for centralized logging and
monitoring:

### Architecture

- **Elasticsearch**: Document database for storing and indexing logs
- **Logstash**: Log processing pipeline for collecting, parsing, and transforming logs
- **Kibana**: Visualization platform for exploring, analyzing, and creating dashboards from log data

### Features

- **Centralized logging**: All microservices send their logs to a central location
- **Structured logging**: JSON-formatted logs with consistent fields and metadata
- **Service identification**: Each service's logs are tagged with service name and type
- **Full-text search**: Quickly find relevant logs using Elasticsearch's powerful search capabilities
- **Real-time monitoring**: View logs as they are generated
- **Visualization**: Create custom dashboards in Kibana to monitor system health and performance

### Configuration

Each service is configured to send logs to Logstash using:

- **logback-spring.xml**: Custom log configuration for each service
- **logstash-logback-encoder**: Formats logs as JSON and sends them to Logstash via TCP

### Accessing ELK

- **Elasticsearch**: http://localhost:9200
- **Kibana**: http://localhost:5601

### How to Use

1. Start the application using Docker Compose
2. Access Kibana at http://localhost:5601
3. Create an index pattern for "courier-tracking-logs-*"
4. Use the Discover tab to search and filter logs
5. Create visualizations and dashboards to monitor:
    - Error rates
    - Service response times
    - Courier activities
    - Store entries
    - System health

## Prometheus and Grafana Integration

The project includes a complete Prometheus and Grafana setup for monitoring and alerting:

### Architecture

- **Prometheus**: Time-series database for collecting and storing metrics
- **Grafana**: Visualization platform for creating dashboards and alerts based on metrics data
- **Micrometer**: Library for instrumentation of Spring Boot applications

### Features

- **Application metrics**: Collects metrics about application performance (JVM, HTTP requests, etc.)
- **Infrastructure metrics**: Monitors infrastructure components (CPU, memory, etc.)
- **Resilience metrics**: Tracks Resilience4j circuit breakers, bulkheads, and rate limiters
- **Custom dashboards**: Pre-configured dashboards for monitoring the entire system
- **Automated discovery**: Uses Eureka for service discovery to automatically monitor new instances

### Configuration

Each service is configured to expose metrics via Spring Boot Actuator:

- **Micrometer Prometheus**: Exposes metrics in Prometheus format
- **Actuator endpoints**: Provides health, info, and metrics endpoints
- **Auto-provisioned dashboards**: Grafana dashboards are automatically provisioned

### Provided Dashboards

- **Service Overview**: CPU, memory, and JVM metrics for all services
- **HTTP Metrics**: Request counts, response times, and error rates
- **Resilience4j**: Circuit breaker states and retry attempts
- **Infrastructure**: Host-level metrics for all containers

### Accessing Monitoring Tools

- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)

### How to Use

1. Start the application using Docker Compose
2. Access Grafana at http://localhost:3000 (login with admin/admin)
3. Navigate to "Dashboards" to see pre-configured dashboards
4. Use Prometheus at http://localhost:9090 for ad-hoc queries
5. Set up alerts in Grafana for important metrics

## Resilience4j Configuration

The system uses Resilience4j to implement fault tolerance patterns:

### Circuit Breaker

Prevents cascading failures by breaking the circuit when a dependency fails repeatedly:

- **Failure threshold**: 50% of requests must fail within the sliding window to open the circuit
- **Sliding window size**: 100 requests
- **Wait duration**: 10 seconds before attempting to close the circuit again

### Retry

Automatically retries failed operations:

- **Maximum attempts**: 3 retries before giving up
- **Wait duration**: 500ms between retries
- **Applied to**: Location service calls, Redis operations

### Bulkhead

Limits concurrent calls to prevent resource exhaustion:

- **Maximum concurrent calls**: 10
- **Maximum wait time**: 500ms

### Rate Limiter

Restricts the number of calls within a time period:

- **Limit**: 50 calls per 10 seconds
- **Timeout**: 5 seconds when limit is exceeded

## Getting Started

This is the third version of the project. This project contains the following changes:

- Added Simple Spring Security to Api Gateway
- Docker image size reduction from around 580 Mb to 120 Mb
- ELK Stack integration for centralized logging
- Prometheus and Grafana integration for comprehensive monitoring

### Prerequisites

You'll need:

- Java 21+
- Docker and Docker Desktop
- Maven
- Some basic knowledge of Spring Boot and Kafka

### Running the App

# Clone the repo

git clone https://github.com/hasandg/courier-tracking-system3.git

#### Option 1: The Easy Way (Docker)

The easiest way to run everything is with Docker Compose:

```bash
##### First Build Shared Objects
cd courier-tracking-system/shared-objects
mvn clean install

##### Build All Services(Skip tests)
cd ..
mvn clean install -DskipTests

cd courier_tracking_system_infra
```

```bash
docker-compose up -d
```

if any changes made in the code and want to rebuild the image then run below command  
(run maven clean install before running below command)

```bash
docker compose up -d --build
```

## Stopping the Application

```bash
docker compose down
```

if you want to remove the images as well then run below command

```bash
docker compose down --rmi all
```

if you want to remove the volumes as well then run below command

```bash
docker compose down -v
```

#### Option 2: Manual Setup

If you want more control or to run without Docker:

1. Make sure you have PostgreSQL, Redis, and Kafka running locally or update configs to point to your instances

2. Build and start each service (start them in this order):

```bash
mvn clean package

# Start in this order:
java -jar discovery-service/target/discovery-service.jar
java -jar config-server/target/config-server.jar
java -jar location-service/target/location-service.jar
java -jar distance-calculation-service/target/distance-calculation-service.jar
java -jar store-service/target/store-service.jar
java -jar api-gateway/target/api-gateway.jar
```

#### Option 3: Hybrid Setup (Advised if logs are wanted to be seen on console)

```bash
##### First Build Shared Objects
cd courier-tracking-system/shared-objects
mvn clean install

##### Build All Services(Skip tests)
cd ..
mvn clean install -DskipTests

cd courier_tracking_system_infra
```

```bash
docker-compose up -d
```

if any changes made in the code and want to rebuild the image then run below command  
(run maven clean install before running below command)

```bash
docker compose up -d --build
```

## Stop Distance Calculation, Store and Location Services

Using Docker Desktop, you can stop the services by clicking on the stop button on the container.

## Start Distance Calculation, Store and Location Services on IDE ( Eclipse, IDEA ...)

1. Open the project in IDE
2. Go to the respective service and run the main class

```bash

## Stopping the Application

```bash
docker compose down
```

if you want to remove the images as well then run below command

```bash
docker compose down --rmi all
```

if you want to remove the volumes as well then run below command

```bash
docker compose down -v
```

## API Examples

Once everything is running, you can access the Swagger UI to explore the APIs:

### Store Service

```
http://localhost:8082/swagger-ui.html
```

### Distance Calculation Service

```
http://localhost:8083/swagger-ui.html
```

### Location Service

```
http://localhost:8084/swagger-ui.html
```

## Monitoring and Management

### ELK Stack

- Kibana UI: http://localhost:5601
- Elasticsearch API: http://localhost:9200

### Prometheus & Grafana

- Prometheus UI: http://localhost:9090
- Grafana UI: http://localhost:3000 (login: admin/admin)
- Predefined dashboards:
    - Service Overview
    - HTTP Metrics
    - Resilience4j Monitoring
    - JVM Metrics

### Spring Boot Actuator

Each service exposes actuator endpoints for monitoring:

```
http://localhost:8082/actuator/health
http://localhost:8083/actuator/health
http://localhost:8084/actuator/health
http://localhost:8090/actuator/health
http://localhost:8082/actuator/prometheus
http://localhost:8083/actuator/prometheus
http://localhost:8084/actuator/prometheus
http://localhost:8090/actuator/prometheus
```

### Eureka Dashboard

Monitor service registrations:

```
http://localhost:8761
```

### Kafka UI

Monitor Kafka topics and messages:

```
http://localhost:9000
```

## Troubleshooting

### Common Issues

**Services can't connect to each other:**

- Check if Eureka Server is running
- Verify service registration in Eureka dashboard

**Kafka related errors:**

- Ensure Kafka brokers are running
- Check topic creation and consumer group configuration

**ELK Stack issues:**

- Verify Elasticsearch is running: `curl localhost:9200`
- Check Logstash is receiving logs: `docker logs logstash`
- Ensure Kibana is accessible: http://localhost:5601
- If you see `ClassNotFoundException: net.logstash.logback.appender.LogstashTcpSocketAppender` errors, make sure all
  services have the `logstash-logback-encoder` dependency in their pom.xml files

**Prometheus/Grafana issues:**

- Check Prometheus targets: http://localhost:9090/targets
- Verify metrics are being exposed: `curl localhost:8083/actuator/prometheus`
- Check Grafana datasource configuration
- If you see metrics-related errors, ensure all services have the `micrometer-registry-prometheus` dependency

### Logs

Check individual service logs:

```bash
docker logs courier_tracking_system_infra-distance-calculation-service-1
docker logs courier_tracking_system_infra-store-service-1
docker logs courier_tracking_system_infra-location-service-1
```

Or use Kibana to view all logs in one place.
