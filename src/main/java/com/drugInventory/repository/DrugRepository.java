package com.drugInventory.repository;

import com.drugInventory.model.Drug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Drug entity.
 * Provides CRUD + custom queries for expiry tracking, search, and duplicate detection.
 */
@Repository
public interface DrugRepository extends JpaRepository<Drug, Long> {

    // Find drug by batch number (for duplicate check and lookup)
    Optional<Drug> findByBatchNumber(String batchNumber);

    // Check if a batch number already exists (prevent duplicates)
    boolean existsByBatchNumber(String batchNumber);

    // Search drugs by name (case-insensitive contains - for search bar)
    List<Drug> findByNameContainingIgnoreCase(String name);

    // Find all drugs that have already expired
    List<Drug> findByExpiryDateBefore(LocalDate date);

    // Find drugs expiring soon (between today and a given future date)
    List<Drug> findByExpiryDateBetween(LocalDate startDate, LocalDate endDate);

    // Find all drugs supplied by a specific supplier
    List<Drug> findBySupplierId(Long supplierId);

    // Find drugs with quantity below a threshold (low stock)
    List<Drug> findByQuantityLessThanAndActiveTrue(Integer threshold);

    // Custom JPQL query to find active drugs only
    @Query("SELECT d FROM Drug d WHERE d.active = true ORDER BY d.name")
    List<Drug> findAllActiveDrugs();

    // Find by category
    List<Drug> findByCategoryIgnoreCase(String category);
}
