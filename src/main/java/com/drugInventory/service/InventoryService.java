package com.drugInventory.service;

import com.drugInventory.model.Inventory;
import com.drugInventory.model.Transaction;
import com.drugInventory.model.Drug;
import com.drugInventory.model.User;
import com.drugInventory.exception.ResourceNotFoundException;
import com.drugInventory.repository.InventoryRepository;
import com.drugInventory.repository.DrugRepository;
import com.drugInventory.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Inventory Service - manages real-time stock levels.
 * Handles stock updates after sales, purchases, and transfers.
 * Triggers low-stock alerts.
 */
@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Get all inventory items.
     */
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    /**
     * Get inventory for a specific location.
     */
    public List<Inventory> getInventoryByLocation(Inventory.Location location) {
        return inventoryRepository.findByLocation(location);
    }

    /**
     * Get all low-stock items (quantity <= threshold).
     */
    public List<Inventory> getLowStockAlerts() {
        return inventoryRepository.findLowStockItems();
    }

    /**
     * Update stock at a location (increase or decrease quantity).
     * Also records the transaction for audit trail.
     *
     * @param drugId      ID of the drug
     * @param location    Location to update
     * @param quantity    Positive = add stock, Negative = remove stock
     * @param type        Transaction type for audit log
     * @param user        User performing the action
     */
    @Transactional
    public Inventory updateStock(Long drugId, Inventory.Location location,
                                  int quantity, Transaction.TransactionType type,
                                  User user, String notes) {
        // Find inventory record for this drug at this location
        Inventory inventory = inventoryRepository.findByDrugIdAndLocation(drugId, location)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory record not found for drug " + drugId + " at " + location));

        // Update quantity
        int newQuantity = inventory.getQuantity() + quantity;
        if (newQuantity < 0) {
            throw new IllegalArgumentException(
                    "Insufficient stock. Available: " + inventory.getQuantity() +
                    ", Requested: " + Math.abs(quantity));
        }
        inventory.setQuantity(newQuantity);
        Inventory updated = inventoryRepository.save(inventory);

        // Also update the drug's total quantity
        Drug drug = drugRepository.findById(drugId).orElseThrow();
        drug.setQuantity(drug.getQuantity() + quantity);
        drugRepository.save(drug);

        // Record transaction for audit trail
        Transaction transaction = new Transaction();
        transaction.setDrug(drug);
        transaction.setType(type);
        transaction.setQuantity(Math.abs(quantity));
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setPerformedBy(user);
        transaction.setNotes(notes);
        transactionRepository.save(transaction);

        return updated;
    }
}
