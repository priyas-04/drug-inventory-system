package com.drugInventory.service;

import com.drugInventory.model.Drug;
import com.drugInventory.model.Inventory;
import com.drugInventory.model.Transaction;
import com.drugInventory.repository.DrugRepository;
import com.drugInventory.repository.InventoryRepository;
import com.drugInventory.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Report Service - generates business reports for the dashboard.
 * Provides: stock summary, expired drugs, low stock, sales history.
 */
@Service
public class ReportService {

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Stock Summary Report:
     * Total drugs, total stock, expired count, low-stock count.
     */
    public Map<String, Object> getStockSummary() {
        List<Drug> allDrugs = drugRepository.findAllActiveDrugs();
        List<Drug> expiredDrugs = drugRepository.findByExpiryDateBefore(LocalDate.now());
        List<Inventory> lowStockItems = inventoryRepository.findLowStockItems();

        int totalStock = allDrugs.stream().mapToInt(Drug::getQuantity).sum();

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalDrugs", allDrugs.size());
        summary.put("totalStockUnits", totalStock);
        summary.put("expiredDrugsCount", expiredDrugs.size());
        summary.put("lowStockCount", lowStockItems.size());
        summary.put("generatedAt", LocalDateTime.now().toString());
        return summary;
    }

    /**
     * Expired Drugs Report - lists all medicines past their expiry date.
     */
    public List<Drug> getExpiredDrugsReport() {
        return drugRepository.findByExpiryDateBefore(LocalDate.now());
    }

    /**
     * Near-Expiry Report - drugs expiring within the next 30 days.
     */
    public List<Drug> getNearExpiryReport(int days) {
        LocalDate today = LocalDate.now();
        return drugRepository.findByExpiryDateBetween(today, today.plusDays(days));
    }

    /**
     * Low Stock Report - items below their threshold.
     */
    public List<Inventory> getLowStockReport() {
        return inventoryRepository.findLowStockItems();
    }

    /**
     * Sales History - all SALE transactions.
     */
    public List<Transaction> getSalesHistory() {
        return transactionRepository.findByTypeOrderByTransactionDateDesc(
                Transaction.TransactionType.SALE);
    }

    /**
     * Full Transaction Log - all transactions ordered by date.
     */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
