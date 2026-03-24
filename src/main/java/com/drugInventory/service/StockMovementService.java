package com.drugInventory.service;

import com.drugInventory.exception.ResourceNotFoundException;
import com.drugInventory.model.*;
import com.drugInventory.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * StockMovement Service - handles drug transfers between locations.
 * Supply chain flow example: WAREHOUSE → PHARMACY
 * Decrements stock at source, increments at destination, records transaction.
 */
@Service
public class StockMovementService {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all stock movements.
     */
    public List<StockMovement> getAllMovements() {
        return stockMovementRepository.findAll();
    }

    /**
     * Get movements for a specific drug.
     */
    public List<StockMovement> getMovementsByDrug(Long drugId) {
        return stockMovementRepository.findByDrugIdOrderByMovedAtDesc(drugId);
    }

    /**
     * Transfer drug stock from one location to another.
     * Validates source has enough stock before transferring.
     */
    @Transactional
    public StockMovement transferStock(Long drugId,
                                       Inventory.Location fromLocation,
                                       Inventory.Location toLocation,
                                       int quantity,
                                       String reason,
                                       Long userId) {
        // Validate source inventory
        Inventory sourceInventory = inventoryRepository
                .findByDrugIdAndLocation(drugId, fromLocation)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for drug " + drugId + " at " + fromLocation));

        if (sourceInventory.getQuantity() < quantity) {
            throw new IllegalArgumentException(
                    "Insufficient stock at " + fromLocation +
                    ". Available: " + sourceInventory.getQuantity() + ", Requested: " + quantity);
        }

        Drug drug = drugRepository.findById(drugId)
                .orElseThrow(() -> new ResourceNotFoundException("Drug", "id", drugId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Deduct from source
        sourceInventory.setQuantity(sourceInventory.getQuantity() - quantity);
        inventoryRepository.save(sourceInventory);

        // Add to destination (create record if it doesn't exist)
        Inventory destInventory = inventoryRepository
                .findByDrugIdAndLocation(drugId, toLocation)
                .orElseGet(() -> {
                    Inventory inv = new Inventory();
                    inv.setDrug(drug);
                    inv.setLocation(toLocation);
                    inv.setQuantity(0);
                    inv.setLowStockThreshold(drug.getLowStockThreshold());
                    return inv;
                });
        destInventory.setQuantity(destInventory.getQuantity() + quantity);
        inventoryRepository.save(destInventory);

        // Log movement record
        StockMovement movement = new StockMovement();
        movement.setDrug(drug);
        movement.setFromLocation(fromLocation);
        movement.setToLocation(toLocation);
        movement.setQuantity(quantity);
        movement.setMovedAt(LocalDateTime.now());
        movement.setReason(reason);
        movement.setMovedBy(user);
        StockMovement saved = stockMovementRepository.save(movement);

        // Log TRANSFER transaction for audit trail
        Transaction transaction = new Transaction();
        transaction.setDrug(drug);
        transaction.setType(Transaction.TransactionType.TRANSFER);
        transaction.setQuantity(quantity);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setPerformedBy(user);
        transaction.setNotes("Transfer from " + fromLocation + " to " + toLocation + ". " + reason);
        transaction.setReferenceId(saved.getId());
        transactionRepository.save(transaction);

        return saved;
    }
}
