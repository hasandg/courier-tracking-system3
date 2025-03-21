package com.hasandag.courier.tracking.system.store.repository;


import com.hasandag.courier.tracking.system.store.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query(value = "SELECT * FROM stores s " +
            "WHERE earth_box(ll_to_earth(?1, ?2), ?3) @> ll_to_earth(s.latitude, s.longitude)",
            nativeQuery = true)
    List<Store> findStoresNearby(double latitude, double longitude, double radiusInMeters);
}
