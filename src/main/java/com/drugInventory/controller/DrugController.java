package com.drugInventory.controller;

import com.drugInventory.dto.ApiResponse;
import com.drugInventory.dto.DrugRequest;
import com.drugInventory.model.Drug;
import com.drugInventory.service.DrugService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Drug Controller - REST API for drug management.
 *
 * GET    /api/drugs             - Get all drugs
 * GET    /api/drugs/{id}        - Get drug by ID
 * POST   /api/drugs             - Add new drug (Admin, Pharmacist)
 * PUT    /api/drugs/{id}        - Update drug (Admin, Pharmacist)
 * DELETE /api/drugs/{id}        - Delete drug (Admin only)
 * GET    /api/drugs/search?name=... - Search by name
 * GET    /api/drugs/expired        - Get expired drugs
 * GET    /api/drugs/expiring-soon  - Near expiry (next 30 days)
 * GET    /api/drugs/low-stock      - Low stock drugs
 */
@RestController
@RequestMapping("/api/drugs")

public class DrugController {

    @Autowired
    private DrugService drugService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Drug>>> getAllDrugs() {
        return ResponseEntity.ok(ApiResponse.success("Drugs fetched", drugService.getAllDrugs()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Drug>> getDrugById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Drug found", drugService.getDrugById(id)));
    }

    // Only Admin and Pharmacist can add drugs
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<ApiResponse<Drug>> createDrug(@Valid @RequestBody DrugRequest request) {
        Drug drug = drugService.createDrug(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Drug added successfully", drug));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<ApiResponse<Drug>> updateDrug(@PathVariable Long id,
                                                        @Valid @RequestBody DrugRequest request) {
        Drug updated = drugService.updateDrug(id, request);
        return ResponseEntity.ok(ApiResponse.success("Drug updated successfully", updated));
    }

    // Only Admins can delete drugs
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDrug(@PathVariable Long id) {
        drugService.deleteDrug(id);
        return ResponseEntity.ok(ApiResponse.success("Drug deleted successfully"));
    }

    // Search drugs by name
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Drug>>> searchDrugs(@RequestParam String name) {
        return ResponseEntity.ok(ApiResponse.success("Search results", drugService.searchDrugs(name)));
    }

    // Get all expired drugs
    @GetMapping("/expired")
    public ResponseEntity<ApiResponse<List<Drug>>> getExpiredDrugs() {
        return ResponseEntity.ok(ApiResponse.success("Expired drugs", drugService.getExpiredDrugs()));
    }

    // Get drugs expiring in next 30 days (default) or custom days
    @GetMapping("/expiring-soon")
    public ResponseEntity<ApiResponse<List<Drug>>> getExpiringSoon(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(ApiResponse.success("Expiring soon", drugService.getDrugsExpiringSoon(days)));
    }

    // Get low-stock drugs
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<Drug>>> getLowStockDrugs() {
        return ResponseEntity.ok(ApiResponse.success("Low stock drugs", drugService.getLowStockDrugs()));
    }

    // Filter by category
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<Drug>>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(ApiResponse.success("Drugs in category", drugService.getDrugsByCategory(category)));
    }
}
