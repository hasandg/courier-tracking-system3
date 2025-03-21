package com.hasandag.courier.tracking.system.distancecalculation.client;

import com.hasandag.courier.tracking.system.distancecalculation.dto.CourierLocationDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@FeignClient(name = "location-service")
public interface LocationServiceClient {

    Logger log = LoggerFactory.getLogger(LocationServiceClient.class);

    @GetMapping("/api/locations/couriers/{courierId}/history")
    @CircuitBreaker(name = "locationService", fallbackMethod = "getCourierLocationHistoryFallback")
    List<CourierLocationDto> getCourierLocationHistory(@PathVariable("courierId") String courierId);

    @GetMapping("/api/locations/couriers/{courierId}/timerange")
    @CircuitBreaker(name = "locationService", fallbackMethod = "getCourierLocationsByTimeRangeFallback")
    List<CourierLocationDto> getCourierLocationsByTimeRange(
            @PathVariable("courierId") String courierId,
            @RequestParam("startTime") LocalDateTime startTime,
            @RequestParam("endTime") LocalDateTime endTime);

    // Fallback methods
    default List<CourierLocationDto> getCourierLocationHistoryFallback(String courierId, Exception ex) {
        log.error("Fallback for getCourierLocationHistory. CourierId: {}, Error: {}", courierId, ex.getMessage());
        return Collections.emptyList();
    }

    default List<CourierLocationDto> getCourierLocationsByTimeRangeFallback(
            String courierId, LocalDateTime startTime, LocalDateTime endTime, Exception ex) {
        log.error("Fallback for getCourierLocationsByTimeRange. CourierId: {}, Time range: {} to {}, Error: {}",
                courierId, startTime, endTime, ex.getMessage());
        return Collections.emptyList();
    }
}
