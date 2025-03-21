# Courier Tracking System API Test Plan

## Store Service (Port 8082)

### Get Paginated Stores

```
curl -s http://localhost:8082/api/stores/paginated?page=0&size=10
```

### Get Store by ID

```
curl -s http://localhost:8082/api/stores/1
```

### Get Entries for Store

```
curl -s http://localhost:8082/api/store-entries/stores/1?page=0&size=10
```

### Get Entries for Courier

```
curl -s http://localhost:8082/api/store-entries/couriers/courier-123
```

### Get Paginated Entries for Courier

```
curl -s http://localhost:8082/api/store-entries/couriers/courier-123/paginated?page=0&size=10
```

## Location Service (Port 8084)

### Record New Location

```
curl -s -X POST -H "Content-Type: application/json" -d '{"courierId": "courier-123", "latitude": 41.0082, "longitude": 28.9784}' http://localhost:8084/api/locations
```

### Get All Courier IDs

```
curl -s http://localhost:8084/api/locations/couriers
```

### Get Courier Locations

```
curl -s "http://localhost:8084/api/locations/couriers/courier-123?page=0&size=10"
```

### Get Locations by Time Range

```
curl -s "http://localhost:8084/api/locations/couriers/courier-123/timerange?startTime=2025-03-21T00:00:00Z&endTime=2025-03-23T23:59:59Z"
```

### Get Full Location History

```
curl -s http://localhost:8084/api/locations/couriers/courier-123/history
```

## Distance Calculation Service (Port 8083)

### Get Total Travel Distance

```
curl -s http://localhost:8083/api/distances/couriers/courier-123/total
```

### Get Distance in Date Range

```
curl -s "http://localhost:8083/api/distances/couriers/courier-123/daterange?startDate=2025-03-21&endDate=2025-03-23"
```

### Get Daily Distances

```
curl -s http://localhost:8083/api/distances/couriers/courier-123/daily
```
