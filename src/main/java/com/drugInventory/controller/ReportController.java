package com.drugInventory.controller;

import com.drugInventory.dto.ApiResponse;
import com.drugInventory.model.Drug;
import com.drugInventory.model.Inventory;
import com.drugInventory.model.Transaction;
import com.drugInventory.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Report Controller - provides business intelligence reports.
 *
 * GET /api/reports/summary        - Overall stock summary (counts, totals)
 * GET /api/reports/expired        - List of all expired drugs
 * GET /api/reports/expiring-soon  - Drugs expiring in next N days
 * GET /api/reports/low-stock      - All low-stock inventory items
 * GET /api/reports/sales          - Sales history (SALE transactions)
 * GET /api/reports/transactions   - Full transaction log
 */
@RestController
@RequestMapping("/api/reports")

@PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')") // Only authenticated staff can see reports
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStockSummary() {
        return ResponseEntity.ok(ApiResponse.success("Stock summary", reportService.getStockSummary()));
    }

    @GetMapping("/expired")
    public ResponseEntity<ApiResponse<List<Drug>>> getExpiredDrugs() {
        return ResponseEntity.ok(ApiResponse.success("Expired drugs report", reportService.getExpiredDrugsReport()));
    }

    @GetMapping("/expiring-soon")
    public ResponseEntity<ApiResponse<List<Drug>>> getNearExpiry(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(ApiResponse.success("Near-expiry report", reportService.getNearExpiryReport(days)));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<Inventory>>> getLowStock() {
        return ResponseEntity.ok(ApiResponse.success("Low stock report", reportService.getLowStockReport()));
    }

    @GetMapping("/sales")
    public ResponseEntity<ApiResponse<List<Transaction>>> getSalesHistory() {
        return ResponseEntity.ok(ApiResponse.success("Sales history", reportService.getSalesHistory()));
    }

    @GetMapping("/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Transaction>>> getAllTransactions() {
        return ResponseEntity.ok(ApiResponse.success("All transactions", reportService.getAllTransactions()));
    }
}
