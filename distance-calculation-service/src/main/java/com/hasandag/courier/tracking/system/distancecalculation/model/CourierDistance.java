package com.hasandag.courier.tracking.system.distancecalculation.model;

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
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "courier_distances",
        indexes = {
                @Index(name = "idx_courier_distance_courier_id", columnList = "courier_id"),
                @Index(name = "idx_courier_distance_date", columnList = "date")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourierDistance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "courier_id", nullable = false)
    private String courierId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "total_distance", nullable = false)
    private double totalDistance;

    @Column(name = "segment_count", nullable = false)
    private int segmentCount;
}

