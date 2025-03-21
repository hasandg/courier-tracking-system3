package com.hasandag.courier.tracking.system.store.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "store_entries",
        indexes = {
                @Index(name = "idx_store_entry_courier_id", columnList = "courier_id"),
                @Index(name = "idx_store_entry_store_id", columnList = "store_id"),
                @Index(name = "idx_store_entry_timestamp", columnList = "entry_time")
        })
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "courier_id", nullable = false)
    private String courierId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "entry_time", nullable = false)
    private LocalDateTime entryTime;

    @Column(name = "courier_latitude", nullable = false)
    private double courierLatitude;

    @Column(name = "courier_longitude", nullable = false)
    private double courierLongitude;
}
