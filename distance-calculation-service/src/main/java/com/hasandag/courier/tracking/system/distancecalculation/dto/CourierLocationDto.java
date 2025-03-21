package com.hasandag.courier.tracking.system.distancecalculation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourierLocationDto {
    private Long id;
    private String courierId;
    private double latitude;
    private double longitude;
    private LocalDateTime timestamp;
}
