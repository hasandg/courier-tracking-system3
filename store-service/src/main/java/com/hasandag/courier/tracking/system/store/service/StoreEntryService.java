package com.hasandag.courier.tracking.system.store.service;

import com.hasandag.courier.tracking.system.dto.LocationEvent;
import com.hasandag.courier.tracking.system.store.model.Store;
import com.hasandag.courier.tracking.system.store.model.StoreEntry;
import com.hasandag.courier.tracking.system.store.repository.StoreEntryRepository;
import com.hasandag.courier.tracking.system.store.repository.StoreRepository;
import com.hasandag.courier.tracking.system.utils.GeoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreEntryService {

    private final StoreRepository storeRepository;
    private final StoreEntryRepository storeEntryRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${application.store.proximity-radius-meters}")
    private double proximityRadiusMeters;

    @Value("${application.store.reentry-timeout-seconds}")
    private long reentryTimeoutSeconds;

    @Transactional
    public void processLocationEvent(LocationEvent locationEvent) {
        String courierId = locationEvent.getCourierId();
        double latitude = locationEvent.getLatitude();
        double longitude = locationEvent.getLongitude();
        LocalDateTime timestamp = locationEvent.getTimestamp();

        List<Store> nearbyStores = storeRepository.findStoresNearby(
                latitude, longitude, proximityRadiusMeters);

        for (Store store : nearbyStores) {
            if (GeoUtils.isWithinRadius(
                    latitude, longitude,
                    store.getLatitude(), store.getLongitude(),
                    proximityRadiusMeters)) {

                String redisKey = getStoreEntryRedisKey(courierId, store.getId());

                if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))) {

                    StoreEntry entry = StoreEntry.builder()
                            .courierId(courierId)
                            .store(store)
                            .entryTime(timestamp)
                            .courierLatitude(latitude)
                            .courierLongitude(longitude)
                            .build();

                    storeEntryRepository.save(entry);
                    log.info("Recorded entry for courier {} to store {}", courierId, store.getName());

                    redisTemplate.opsForValue().set(redisKey, "1");
                    redisTemplate.expire(redisKey, reentryTimeoutSeconds, TimeUnit.SECONDS);
                } else {
                    log.debug("Ignored reentry for courier {} to store {}", courierId, store.getName());
                }
            }
        }
    }

    private String getStoreEntryRedisKey(String courierId, Long storeId) {
        return "store-entry:" + courierId + ":" + storeId;
    }

    public List<StoreEntry> getEntriesForCourier(String courierId) {
        return storeEntryRepository.findByCourierIdOrderByEntryTimeDesc(courierId);
    }

    public Page<StoreEntry> getEntriesForCourierPaginated(String courierId, Pageable pageable) {
        return storeEntryRepository.findByCourierIdOrderByEntryTimeDesc(courierId, pageable);
    }

    public Page<StoreEntry> getEntriesForStore(Long storeId, Pageable pageable) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found with id: " + storeId));
        return storeEntryRepository.findByStoreOrderByEntryTimeDesc(store, pageable);
    }
}
