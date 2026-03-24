package com.drugInventory.controller;

import com.drugInventory.dto.ApiResponse;
import com.drugInventory.model.Inventory;
import com.drugInventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Inventory Controller - REST API for real-time inventory tracking.
 *
 * GET /api/inventory              - All inventory records
 * GET /api/inventory/alerts       - Low-stock alerts
 * GET /api/inventory/location/:loc - By location (WAREHOUSE, PHARMACY, HOSPITAL)
 */
@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Inventory>>> getAllInventory() {
        return ResponseEntity.ok(ApiResponse.success("Inventory fetched", inventoryService.getAllInventory()));
    }

    @GetMapping("/alerts")
    public ResponseEntity<ApiResponse<List<Inventory>>> getLowStockAlerts() {
        List<Inventory> alerts = inventoryService.getLowStockAlerts();
        return ResponseEntity.ok(ApiResponse.success(
                alerts.size() + " low-stock alert(s) found", alerts));
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<ApiResponse<List<Inventory>>> getByLocation(
            @PathVariable Inventory.Location location) {
        return ResponseEntity.ok(ApiResponse.success(
                "Inventory at " + location, inventoryService.getInventoryByLocation(location)));
    }
}
