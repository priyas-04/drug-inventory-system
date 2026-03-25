package com.drugInventory.controller;

import com.drugInventory.dto.ApiResponse;
import com.drugInventory.model.Supplier;
import com.drugInventory.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Supplier Controller - REST API for supplier management.
 *
 * GET    /api/suppliers           - Get all suppliers
 * GET    /api/suppliers/{id}      - Get supplier by ID
 * POST   /api/suppliers           - Add supplier (Admin only)
 * PUT    /api/suppliers/{id}      - Update supplier (Admin only)
 * DELETE /api/suppliers/{id}      - Delete supplier (Admin only)
 * GET    /api/suppliers/search    - Search by name
 */
@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Supplier>>> getAllSuppliers() {
        return ResponseEntity.ok(ApiResponse.success("Suppliers fetched", supplierService.getAllSuppliers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Supplier>> getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Supplier found", supplierService.getSupplierById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Supplier>> createSupplier(@RequestBody Supplier supplier) {
        Supplier created = supplierService.createSupplier(supplier);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Supplier added successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Supplier>> updateSupplier(@PathVariable Long id,
                                                                @RequestBody Supplier supplier) {
        Supplier updated = supplierService.updateSupplier(id, supplier);
        return ResponseEntity.ok(ApiResponse.success("Supplier updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok(ApiResponse.success("Supplier deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Supplier>>> searchSuppliers(@RequestParam String name) {
        return ResponseEntity.ok(ApiResponse.success("Search results", supplierService.searchSuppliers(name)));
    }
}
