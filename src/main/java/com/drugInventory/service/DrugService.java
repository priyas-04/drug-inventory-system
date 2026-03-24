package com.drugInventory.service;

import com.drugInventory.dto.DrugRequest;
import com.drugInventory.exception.DuplicateEntryException;
import com.drugInventory.exception.ResourceNotFoundException;
import com.drugInventory.model.Drug;
import com.drugInventory.model.Inventory;
import com.drugInventory.model.Supplier;
import com.drugInventory.repository.DrugRepository;
import com.drugInventory.repository.InventoryRepository;
import com.drugInventory.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Drug Service - business logic for drug management.
 * Handles CRUD operations with business rules:
 * - Prevents duplicate batch numbers
 * - Validates expiry dates
 * - Creates inventory record when drug is added
 */
@Service
public class DrugService {

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    /**
     * Get all active drugs.
     */
    public List<Drug> getAllDrugs() {
        return drugRepository.findAllActiveDrugs();
    }

    /**
     * Get a single drug by ID.
     */
    public Drug getDrugById(Long id) {
        return drugRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Drug", "id", id));
    }

    /**
     * Create a new drug.
     * - Checks for duplicate batch number
     * - Creates an inventory record at WAREHOUSE location
     */
    @Transactional
    public Drug createDrug(DrugRequest request) {
        // Prevent duplicate batch numbers
        if (drugRepository.existsByBatchNumber(request.getBatchNumber())) {
            throw new DuplicateEntryException(
                    "Drug with batch number '" + request.getBatchNumber() + "' already exists");
        }

        // Fetch the supplier
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", request.getSupplierId()));

        // Map request DTO to Drug entity
        Drug drug = new Drug();
        drug.setName(request.getName());
        drug.setBatchNumber(request.getBatchNumber());
        drug.setExpiryDate(request.getExpiryDate());
        drug.setManufactureDate(request.getManufactureDate());
        drug.setQuantity(request.getQuantity());
        drug.setPrice(request.getPrice());
        drug.setCategory(request.getCategory());
        drug.setManufacturer(request.getManufacturer());
        drug.setDescription(request.getDescription());
        drug.setLowStockThreshold(request.getLowStockThreshold());
        drug.setSupplier(supplier);
        drug.setActive(true);

        Drug savedDrug = drugRepository.save(drug);

        // Automatically create inventory record at WAREHOUSE location
        Inventory inventory = new Inventory();
        inventory.setDrug(savedDrug);
        inventory.setLocation(Inventory.Location.WAREHOUSE);
        inventory.setQuantity(request.getQuantity());
        inventory.setLowStockThreshold(request.getLowStockThreshold());
        inventoryRepository.save(inventory);

        return savedDrug;
    }

    /**
     * Update an existing drug.
     */
    @Transactional
    public Drug updateDrug(Long id, DrugRequest request) {
        Drug drug = getDrugById(id);

        // If batch number changed, check it's not taken by another drug
        if (!drug.getBatchNumber().equals(request.getBatchNumber()) &&
                drugRepository.existsByBatchNumber(request.getBatchNumber())) {
            throw new DuplicateEntryException(
                    "Batch number '" + request.getBatchNumber() + "' is already in use");
        }

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", request.getSupplierId()));

        drug.setName(request.getName());
        drug.setBatchNumber(request.getBatchNumber());
        drug.setExpiryDate(request.getExpiryDate());
        drug.setManufactureDate(request.getManufactureDate());
        drug.setQuantity(request.getQuantity());
        drug.setPrice(request.getPrice());
        drug.setCategory(request.getCategory());
        drug.setManufacturer(request.getManufacturer());
        drug.setDescription(request.getDescription());
        drug.setLowStockThreshold(request.getLowStockThreshold());
        drug.setSupplier(supplier);

        return drugRepository.save(drug);
    }

    /**
     * Soft-delete a drug (set active = false instead of actually deleting).
     * This preserves the drug in transaction history.
     */
    @Transactional
    public void deleteDrug(Long id) {
        Drug drug = getDrugById(id);
        drug.setActive(false);
        drugRepository.save(drug);
    }

    /**
     * Search drugs by name (case-insensitive).
     */
    public List<Drug> searchDrugs(String name) {
        return drugRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Get all expired drugs (expiry date before today).
     */
    public List<Drug> getExpiredDrugs() {
        return drugRepository.findByExpiryDateBefore(LocalDate.now());
    }

    /**
     * Get drugs expiring within the next N days.
     */
    public List<Drug> getDrugsExpiringSoon(int days) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);
        return drugRepository.findByExpiryDateBetween(today, futureDate);
    }

    /**
     * Get all drugs with low stock.
     */
    public List<Drug> getLowStockDrugs() {
        return drugRepository.findByQuantityLessThanAndActiveTrue(10);
    }

    /**
     * Filter drugs by category.
     */
    public List<Drug> getDrugsByCategory(String category) {
        return drugRepository.findByCategoryIgnoreCase(category);
    }
}
