package com.drugInventory.repository;

import com.drugInventory.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for StockMovement entity - tracks supply chain transfers.
 */
@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    // Find all movements for a specific drug
    List<StockMovement> findByDrugIdOrderByMovedAtDesc(Long drugId);

    // Find movements initiated by a specific user
    List<StockMovement> findByMovedByIdOrderByMovedAtDesc(Long userId);
}
