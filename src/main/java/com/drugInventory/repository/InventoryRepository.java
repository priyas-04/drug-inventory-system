package com.drugInventory.repository;

import com.drugInventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Inventory entity.
 * Provides stock tracking and low-stock alert queries.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Find inventory record for a specific drug at a specific location
    Optional<Inventory> findByDrugIdAndLocation(Long drugId, Inventory.Location location);

    // Get all inventory records for a specific drug across all locations
    List<Inventory> findByDrugId(Long drugId);

    // Get all inventory records for a specific location
    List<Inventory> findByLocation(Inventory.Location location);

    // Find all inventory records where quantity is low (below threshold)
    @Query("SELECT i FROM Inventory i WHERE i.quantity <= i.lowStockThreshold")
    List<Inventory> findLowStockItems();

    // Find out-of-stock items
    List<Inventory> findByQuantityLessThanEqual(Integer quantity);
}
