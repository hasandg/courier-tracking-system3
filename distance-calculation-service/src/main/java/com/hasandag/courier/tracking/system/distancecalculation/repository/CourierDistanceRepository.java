package com.hasandag.courier.tracking.system.distancecalculation.repository;

import com.hasandag.courier.tracking.system.distancecalculation.model.CourierDistance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourierDistanceRepository extends JpaRepository<CourierDistance, Long> {

    Optional<CourierDistance> findByCourierIdAndDate(String courierId, LocalDate date);

    List<CourierDistance> findByCourierIdOrderByDateDesc(String courierId);

    @Query("SELECT SUM(cd.totalDistance) FROM CourierDistance cd WHERE cd.courierId = :courierId")
    Optional<Double> sumTotalDistanceByCourierId(@Param("courierId") String courierId);

    @Query("SELECT SUM(cd.totalDistance) FROM CourierDistance cd WHERE cd.courierId = :courierId AND cd.date BETWEEN :startDate AND :endDate")
    Optional<Double> sumTotalDistanceByCourierIdAndDateBetween(
            @Param("courierId") String courierId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

}
