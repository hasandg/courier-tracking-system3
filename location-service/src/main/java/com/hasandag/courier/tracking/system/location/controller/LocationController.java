package com.hasandag.courier.tracking.system.location.controller;


import com.hasandag.courier.tracking.system.location.dto.LocationRequest;
import com.hasandag.courier.tracking.system.location.model.CourierLocation;
import com.hasandag.courier.tracking.system.location.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@Tag(name = "Courier Location", description = "API endpoints for managing courier locations")
public class LocationController {

    private final LocationService locationService;

    @Operation(summary = "Record a new courier location", description = "Records the current location of a courier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Location successfully recorded"),
            @ApiResponse(responseCode = "400", description = "Invalid location data provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<CourierLocation> recordLocation(@Valid @RequestBody LocationRequest request) {
        log.info("Received location request: {}", request);
        CourierLocation location = locationService.recordLocation(request);
        return new ResponseEntity<>(location, HttpStatus.CREATED);
    }

    @Operation(summary = "Get recent locations for a courier", description = "Returns paginated recent locations for a specific courier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved locations"),
            @ApiResponse(responseCode = "404", description = "Courier not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @GetMapping("/couriers/{courierId}")
    public ResponseEntity<Page<CourierLocation>> getCourierLocations(
            @PathVariable String courierId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CourierLocation> locations = locationService.getRecentLocations(courierId, pageable);
        return ResponseEntity.ok(locations);
    }

    @Operation(summary = "Get full location history for a courier", description = "Returns the complete location history for a specific courier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved location history"),
            @ApiResponse(responseCode = "404", description = "Courier not found")
    })
    @GetMapping("/couriers/{courierId}/history")
    public ResponseEntity<List<CourierLocation>> getLocationHistory(@PathVariable String courierId) {
        List<CourierLocation> history = locationService.getLocationHistory(courierId);
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Get all courier IDs", description = "Returns a list of all courier IDs in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved courier IDs")
    })
    @GetMapping("/couriers")
    public ResponseEntity<List<String>> getAllCourierIds() {
        List<String> courierIds = locationService.getAllCourierIds();
        return ResponseEntity.ok(courierIds);
    }

    @Operation(summary = "Get locations by time range", description = "Returns locations for a specific courier within a given time range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved locations"),
            @ApiResponse(responseCode = "400", description = "Invalid time range provided"),
            @ApiResponse(responseCode = "404", description = "Courier not found")
    })
    @GetMapping("/couriers/{courierId}/timerange")
    public ResponseEntity<List<CourierLocation>> getLocationsByTimeRange(
            @PathVariable String courierId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<CourierLocation> locations = locationService.getLocationsByTimeRange(courierId, startTime, endTime);
        return ResponseEntity.ok(locations);
    }
}
