package com.hasandag.courier.tracking.system.location.repository;


import com.hasandag.courier.tracking.system.location.model.CourierLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CourierLocationRepository extends JpaRepository<CourierLocation, Long> {

    List<CourierLocation> findByCourierIdOrderByTimestampAsc(String courierId);

    List<CourierLocation> findByCourierIdAndTimestampGreaterThanEqualAndTimestampLessThanEqualOrderByTimestampAsc(
            String courierId,
            LocalDateTime startTime,
            LocalDateTime endTime);

    Page<CourierLocation> findByCourierIdOrderByTimestampDesc(String courierId, Pageable pageable);

    @Query("SELECT DISTINCT cl.courierId FROM CourierLocation cl")
    List<String> findDistinctCourierIds();

}