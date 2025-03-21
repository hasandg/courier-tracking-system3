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
public class StoreEntryEvent {
    private String courierId;
    private Long storeId;
    private String storeName;
    private double storeLatitude;
    private double storeLongitude;
    private double courierLatitude;
    private double courierLongitude;
    private LocalDateTime entryTime;
    private String eventId;
}
