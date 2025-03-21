package com.hasandag.courier.tracking.system.location.service;


import com.hasandag.courier.tracking.system.dto.LocationEvent;
import com.hasandag.courier.tracking.system.location.dto.LocationRequest;
import com.hasandag.courier.tracking.system.location.kafka.LocationProducer;
import com.hasandag.courier.tracking.system.location.model.CourierLocation;
import com.hasandag.courier.tracking.system.location.repository.CourierLocationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private final CourierLocationRepository locationRepository;
    private final LocationProducer locationProducer;

    @Transactional
    public CourierLocation recordLocation(LocationRequest request) {

        LocalDateTime timestamp = LocalDateTime.now();
        CourierLocation location = CourierLocation.builder()
                .courierId(request.getCourierId())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .timestamp(timestamp)
                .build();

        CourierLocation savedLocation = locationRepository.save(location);
        log.info("Recorded location for courier: {}, lat: {}, lng: {}",
                request.getCourierId(), request.getLatitude(), request.getLongitude());

        LocationEvent event = LocationEvent.builder()
                .courierId(savedLocation.getCourierId())
                .latitude(savedLocation.getLatitude())
                .longitude(savedLocation.getLongitude())
                .timestamp(savedLocation.getTimestamp())
                .eventId(UUID.randomUUID().toString())
                .build();

        locationProducer.sendLocationEvent(event);
        log.debug("Published location event: {}", event);

        return savedLocation;
    }

    public List<CourierLocation> getLocationHistory(String courierId) {
        return locationRepository.findByCourierIdOrderByTimestampAsc(courierId);
    }

    public Page<CourierLocation> getRecentLocations(String courierId, Pageable pageable) {
        return locationRepository.findByCourierIdOrderByTimestampDesc(courierId, pageable);
    }

    public List<String> getAllCourierIds() {
        return locationRepository.findDistinctCourierIds();
    }

    public List<CourierLocation> getLocationsByTimeRange(String courierId, LocalDateTime startTime, LocalDateTime endTime) {
        return locationRepository.findByCourierIdAndTimestampGreaterThanEqualAndTimestampLessThanEqualOrderByTimestampAsc(courierId, startTime, endTime);
    }
}
