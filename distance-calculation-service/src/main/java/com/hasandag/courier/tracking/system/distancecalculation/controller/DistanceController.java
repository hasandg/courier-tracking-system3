package com.hasandag.courier.tracking.system.distancecalculation.controller;

import com.hasandag.courier.tracking.system.distancecalculation.model.CourierDistance;
import com.hasandag.courier.tracking.system.distancecalculation.repository.CourierDistanceRepository;
import com.hasandag.courier.tracking.system.distancecalculation.service.DistanceCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/distances")
@RequiredArgsConstructor
public class DistanceController {

    private final DistanceCalculationService distanceCalculationService;
    private final CourierDistanceRepository distanceRepository;

    @GetMapping("/couriers/{courierId}/total")
    public ResponseEntity<Double> getTotalTravelDistance(@PathVariable String courierId) {
        Double totalDistance = distanceCalculationService.getTotalTravelDistance(courierId);
        return ResponseEntity.ok(totalDistance);
    }

    @GetMapping("/couriers/{courierId}/daily")
    public ResponseEntity<List<CourierDistance>> getDailyDistances(@PathVariable String courierId) {
        List<CourierDistance> distances = distanceRepository.findByCourierIdOrderByDateDesc(courierId);
        return ResponseEntity.ok(distances);
    }

    @GetMapping("/couriers/{courierId}/daterange")
    public ResponseEntity<Double> getDistanceInDateRange(
            @PathVariable String courierId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Double totalDistance = distanceCalculationService.getTotalTravelDistanceInDateRange(
                courierId, startDate, endDate);
        return ResponseEntity.ok(totalDistance);
    }
}
