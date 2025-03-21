package com.hasandag.courier.tracking.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationEvent {
    private String courierId;
    private double latitude;
    private double longitude;
    private LocalDateTime timestamp;
    private String eventId;
}
