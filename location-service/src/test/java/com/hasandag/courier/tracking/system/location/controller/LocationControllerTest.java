package com.hasandag.courier.tracking.system.location.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasandag.courier.tracking.system.location.dto.LocationRequest;
import com.hasandag.courier.tracking.system.location.model.CourierLocation;
import com.hasandag.courier.tracking.system.location.service.LocationService;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LocationController.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LocationService locationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRecordLocation() throws Exception {
        // Given
        LocationRequest request = new LocationRequest();
        request.setCourierId("courier1");
        request.setLatitude(40.9923307);
        request.setLongitude(29.1244229);

        CourierLocation location = CourierLocation.builder()
                .id(1L)
                .courierId("courier1")
                .latitude(40.9923307)
                .longitude(29.1244229)
                .timestamp(LocalDateTime.now())
                .build();

        when(locationService.recordLocation(any(LocationRequest.class))).thenReturn(location);

        // When & Then
        mockMvc.perform(post("/api/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.courierId").value("courier1"))
                .andExpect(jsonPath("$.latitude").value(40.9923307))
                .andExpect(jsonPath("$.longitude").value(29.1244229));
    }

    @Test
    void shouldGetCourierLocations() throws Exception {
        // Given
        CourierLocation location = CourierLocation.builder()
                .id(1L)
                .courierId("courier1")
                .latitude(40.9923307)
                .longitude(29.1244229)
                .timestamp(LocalDateTime.now())
                .build();

        when(locationService.getRecentLocations(eq("courier1"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(location)));

        // When & Then
        mockMvc.perform(get("/api/locations/couriers/courier1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].courierId").value("courier1"));
    }
}