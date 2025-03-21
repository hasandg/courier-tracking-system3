package com.hasandag.courier.tracking.system.distancecalculation.service;

import com.hasandag.courier.tracking.system.distancecalculation.model.CourierDistance;
import com.hasandag.courier.tracking.system.distancecalculation.repository.CourierDistanceRepository;
import com.hasandag.courier.tracking.system.dto.LocationEvent;
import com.hasandag.courier.tracking.system.utils.GeoUtils;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class DistanceCalculationService {

    private final CourierDistanceRepository distanceRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private final long CACHE_TTL_SECONDS = 3600;

    private final Map<String, LocationEvent> lastKnownLocations = new HashMap<>();

    /**
     * Process a location event by calculating distance from the last known location
     */
    @Transactional
    @CircuitBreaker(name = "kafkaProcessing", fallbackMethod = "processLocationEventFallback")
    public void processLocationEvent(LocationEvent event) {

        String courierId = event.getCourierId();

        LocationEvent lastLocation = lastKnownLocations.get(courierId);

        if (lastLocation != null) {
            double distance = GeoUtils.calculateDistance(
                    lastLocation.getLatitude(), lastLocation.getLongitude(),
                    event.getLatitude(), event.getLongitude());

            if (distance > 1.0) {
                updateDailyDistance(courierId, distance);
                invalidateTotalDistanceCache(courierId);
            }
        }

        lastKnownLocations.put(courierId, event);
    }

    /**
     * Fallback method for processing location events
     */
    public void processLocationEventFallback(LocationEvent event, Throwable t) {
        log.error("Circuit breaker triggered for processLocationEvent: {}", t.getMessage());
        // Store the event for later processing or send to a dead letter queue
        log.warn("Failed to process location event: {}", event);
        // We still update the lastKnownLocations to maintain at least some state
        lastKnownLocations.put(event.getCourierId(), event);
    }

    @CircuitBreaker(name = "redisService", fallbackMethod = "getTotalTravelDistanceFallback")
    public Double getTotalTravelDistance(String courierId) {

        String cacheKey = "courier-total-distance:" + courierId;
        String cachedValue = redisTemplate.opsForValue().get(cacheKey);

        if (cachedValue != null) {
            try {
                return Double.parseDouble(cachedValue);
            } catch (NumberFormatException e) {
                log.warn("Invalid cached distance value for courier {}: {}", courierId, cachedValue);
            }
        }

        Double totalDistance = distanceRepository.sumTotalDistanceByCourierId(courierId)
                .orElse(0.0);

        redisTemplate.opsForValue().set(cacheKey, totalDistance.toString());
        redisTemplate.expire(cacheKey, CACHE_TTL_SECONDS, TimeUnit.SECONDS);

        return totalDistance;
    }

    /**
     * Fallback method for getting total travel distance
     */
    public Double getTotalTravelDistanceFallback(String courierId, Throwable t) {
        log.error("Circuit breaker triggered for getTotalTravelDistance: {}", t.getMessage());

        // Directly query the repository as fallback
        return distanceRepository.sumTotalDistanceByCourierId(courierId)
                .orElse(0.0);
    }

    @CircuitBreaker(name = "locationService", fallbackMethod = "getTotalTravelDistanceInDateRangeFallback")
    public Double getTotalTravelDistanceInDateRange(String courierId, LocalDate startDate, LocalDate endDate) {
        return distanceRepository.sumTotalDistanceByCourierIdAndDateBetween(courierId, startDate, endDate)
                .orElse(0.0);
    }

    /**
     * Fallback method for getting travel distance in date range
     */
    public Double getTotalTravelDistanceInDateRangeFallback(String courierId, LocalDate startDate, LocalDate endDate, Throwable t) {
        log.error("Circuit breaker triggered for getTotalTravelDistanceInDateRange: {}", t.getMessage());

        // Return a sensible default or cached value
        return 0.0;
    }

    @CircuitBreaker(name = "redisService", fallbackMethod = "invalidateTotalDistanceCacheFallback")
    private void invalidateTotalDistanceCache(String courierId) {
        String cacheKey = "courier-total-distance:" + courierId;
        redisTemplate.delete(cacheKey);
    }

    /**
     * Fallback method for cache invalidation
     */
    private void invalidateTotalDistanceCacheFallback(String courierId, Throwable t) {
        log.error("Failed to invalidate cache for courier {}: {}", courierId, t.getMessage());
        // Nothing else to do here, since it's just cache invalidation
    }

    @CircuitBreaker(name = "locationService", fallbackMethod = "updateDailyDistanceFallback")
    private void updateDailyDistance(String courierId, double additionalDistance) {
        LocalDate today = LocalDate.now();

        CourierDistance dailyDistance = distanceRepository
                .findByCourierIdAndDate(courierId, today)
                .orElseGet(() -> CourierDistance.builder()
                        .courierId(courierId)
                        .date(today)
                        .totalDistance(0.0)
                        .segmentCount(0)
                        .build());

        dailyDistance.setTotalDistance(dailyDistance.getTotalDistance() + additionalDistance);
        dailyDistance.setSegmentCount(dailyDistance.getSegmentCount() + 1);

        distanceRepository.save(dailyDistance);

        log.debug("Updated daily distance for courier {}: +{} meters, total: {} meters",
                courierId, String.format("%.2f", additionalDistance),
                String.format("%.2f", dailyDistance.getTotalDistance()));
    }

    /**
     * Fallback method for updating daily distance
     */
    private void updateDailyDistanceFallback(String courierId, double additionalDistance, Throwable t) {
        log.error("Circuit breaker triggered for updateDailyDistance: {}", t.getMessage());
        // Could store in a local buffer for retry later
        log.warn("Failed to update daily distance for courier {}: +{} meters",
                courierId, String.format("%.2f", additionalDistance));
    }
}