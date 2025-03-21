package com.hasandag.courier.tracking.system.location.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "courier_locations",
        indexes = {
                @Index(name = "idx_location_courier_id", columnList = "courier_id"),
                @Index(name = "idx_location_timestamp", columnList = "timestamp")
        })
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Represents a courier's location at a specific point in time")
public class CourierLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the location record", example = "1")
    private Long id;

    @Column(name = "courier_id", nullable = false)
    @Schema(description = "Identifier of the courier", example = "courier-123")
    private String courierId;

    @Column(nullable = false)
    @Schema(description = "Latitude coordinate of the location", example = "41.0082")
    private double latitude;

    @Column(nullable = false)
    @Schema(description = "Longitude coordinate of the location", example = "28.9784")
    private double longitude;

    @Column(nullable = false)
    @Schema(description = "Timestamp when the location was recorded", example = "2023-05-20T14:30:15")
    private LocalDateTime timestamp;
}