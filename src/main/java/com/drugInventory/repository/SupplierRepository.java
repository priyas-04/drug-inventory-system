package com.drugInventory.repository;

import com.drugInventory.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Supplier entity.
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    // Find supplier by email
    Optional<Supplier> findByEmail(String email);

    // Check if license number already registered (prevent duplicates)
    boolean existsByLicenseNumber(String licenseNumber);

    // Search suppliers by name
    List<Supplier> findByNameContainingIgnoreCase(String name);

    // Find all active suppliers only
    List<Supplier> findByActiveTrue();
}
