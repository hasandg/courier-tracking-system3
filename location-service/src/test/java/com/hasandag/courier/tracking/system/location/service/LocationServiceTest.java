package com.hasandag.courier.tracking.system.location.service;

import com.hasandag.courier.tracking.system.dto.LocationEvent;
import com.hasandag.courier.tracking.system.location.dto.LocationRequest;
import com.hasandag.courier.tracking.system.location.kafka.LocationProducer;
import com.hasandag.courier.tracking.system.location.model.CourierLocation;
import com.hasandag.courier.tracking.system.location.repository.CourierLocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private CourierLocationRepository locationRepository;

    @Mock
    private LocationProducer locationProducer;

    @InjectMocks
    private LocationService locationService;

    @Captor
    private ArgumentCaptor<CourierLocation> locationCaptor;

    @Captor
    private ArgumentCaptor<LocationEvent> eventCaptor;

    private LocationRequest validRequest;
    private CourierLocation savedLocation;

    @BeforeEach
    void setUp() {
        validRequest = new LocationRequest();
        validRequest.setCourierId("courier1");
        validRequest.setLatitude(40.9923307);
        validRequest.setLongitude(29.1244229);

        LocalDateTime now = LocalDateTime.now();
        savedLocation = CourierLocation.builder()
                .id(1L)
                .courierId("courier1")
                .latitude(40.9923307)
                .longitude(29.1244229)
                .timestamp(now)
                .build();
    }

    @Test
    void shouldRecordLocationAndSendEvent() {
        // Given
        when(locationRepository.save(any(CourierLocation.class))).thenReturn(savedLocation);
        doNothing().when(locationProducer).sendLocationEvent(any(LocationEvent.class));

        // When
        CourierLocation result = locationService.recordLocation(validRequest);

        // Then
        verify(locationRepository).save(locationCaptor.capture());
        CourierLocation capturedLocation = locationCaptor.getValue();

        assertEquals(validRequest.getCourierId(), capturedLocation.getCourierId());
        assertEquals(validRequest.getLatitude(), capturedLocation.getLatitude(), 0.0001);
        assertEquals(validRequest.getLongitude(), capturedLocation.getLongitude(), 0.0001);
        assertNotNull(capturedLocation.getTimestamp());

        verify(locationProducer).sendLocationEvent(eventCaptor.capture());
        LocationEvent capturedEvent = eventCaptor.getValue();

        assertEquals(savedLocation.getCourierId(), capturedEvent.getCourierId());
        assertEquals(savedLocation.getLatitude(), capturedEvent.getLatitude(), 0.0001);
        assertEquals(savedLocation.getLongitude(), capturedEvent.getLongitude(), 0.0001);
        assertEquals(savedLocation.getTimestamp(), capturedEvent.getTimestamp());
        assertNotNull(capturedEvent.getEventId());

        assertEquals(savedLocation, result);
    }
}