package com.drugInventory.controller;

import com.drugInventory.dto.ApiResponse;
import com.drugInventory.model.Inventory;
import com.drugInventory.model.StockMovement;
import com.drugInventory.service.StockMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Stock Movement Controller - tracks supply chain transfers between locations.
 *
 * GET  /api/movements               - All movements
 * POST /api/movements               - Transfer drugs between locations
 * GET  /api/movements/drug/{drugId} - Movements for a specific drug
 */
@RestController
@RequestMapping("/api/movements")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class StockMovementController {

    @Autowired
    private StockMovementService stockMovementService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<StockMovement>>> getAllMovements() {
        return ResponseEntity.ok(ApiResponse.success("Movements fetched",
                stockMovementService.getAllMovements()));
    }

    @GetMapping("/drug/{drugId}")
    public ResponseEntity<ApiResponse<List<StockMovement>>> getMovementsByDrug(
            @PathVariable Long drugId) {
        return ResponseEntity.ok(ApiResponse.success("Drug movements",
                stockMovementService.getMovementsByDrug(drugId)));
    }

    /*
     * Request body:
     * {
     *   "drugId": 1,
     *   "fromLocation": "WAREHOUSE",
     *   "toLocation": "PHARMACY",
     *   "quantity": 50,
     *   "reason": "Weekly restock to pharmacy",
     *   "userId": 1
     * }
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<ApiResponse<StockMovement>> transferStock(
            @RequestBody Map<String, Object> body) {
        Long drugId = Long.valueOf(body.get("drugId").toString());
        Inventory.Location from = Inventory.Location.valueOf(body.get("fromLocation").toString());
        Inventory.Location to = Inventory.Location.valueOf(body.get("toLocation").toString());
        int qty = Integer.parseInt(body.get("quantity").toString());
        String reason = body.getOrDefault("reason", "").toString();
        Long userId = Long.valueOf(body.get("userId").toString());

        StockMovement movement = stockMovementService.transferStock(drugId, from, to, qty, reason, userId);
        return ResponseEntity.ok(ApiResponse.success("Stock transferred successfully", movement));
    }
}
