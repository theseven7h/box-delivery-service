package com.delivery.box.repository;

import com.delivery.box.entity.Box;
import com.delivery.box.entity.BoxState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoxRepository extends JpaRepository<Box, Long> {

    Optional<Box> findByTxref(String txref);

    @Query("SELECT b FROM Box b WHERE b.state = :state AND b.batteryCapacity >= 25")
    List<Box> findAvailableBoxesForLoading(BoxState state);

    boolean existsByTxref(String txref);
}