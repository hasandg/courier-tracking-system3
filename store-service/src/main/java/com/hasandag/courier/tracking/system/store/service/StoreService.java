package com.hasandag.courier.tracking.system.store.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasandag.courier.tracking.system.store.dto.StoreDto;
import com.hasandag.courier.tracking.system.store.model.Store;
import com.hasandag.courier.tracking.system.store.repository.StoreRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreService {

    private final StoreRepository storeRepository;
    private final ObjectMapper objectMapper;

    /**
     * Initialize store data from the stores.json file
     */
    @PostConstruct
    @Transactional
    public void initializeStores() {
        try {
            // Check if stores already exist
            if (storeRepository.count() > 0) {
                log.info("Stores are already initialized, skipping import");
                return;
            }

            // Load stores from JSON file
            ClassPathResource resource = new ClassPathResource("stores.json");
            try (InputStream is = resource.getInputStream()) {
                List<StoreDto> storeDtos = objectMapper.readValue(is, new TypeReference<List<StoreDto>>() {
                });

                List<Store> stores = storeDtos.stream()
                        .map(dto -> Store.builder()
                                .name(dto.getName())
                                .latitude(dto.getLatitude())
                                .longitude(dto.getLongitude())
                                .build())
                        .collect(Collectors.toList());

                storeRepository.saveAll(stores);
                log.info("Successfully imported {} stores from stores.json", stores.size());
            }
        } catch (IOException e) {
            log.error("Failed to initialize stores from stores.json", e);
            throw new RuntimeException("Failed to initialize stores", e);
        }
    }

    public Page<Store> getStoresPaginated(Pageable pageable) {
        return storeRepository.findAll(pageable);
    }

    public Optional<Store> getStoreById(Long id) {
        return storeRepository.findById(id);
    }

    public List<Store> getStoresNearby(double latitude, double longitude, double radiusInMeters) {
        return storeRepository.findStoresNearby(latitude, longitude, radiusInMeters);
    }
}
