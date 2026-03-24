package com.drugInventory.controller;

import com.drugInventory.dto.ApiResponse;
import com.drugInventory.model.PurchaseOrder;
import com.drugInventory.model.PurchaseOrderItem;
import com.drugInventory.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Purchase Order Controller - REST API for procurement management.
 *
 * GET  /api/orders                   - List all orders
 * GET  /api/orders/{id}              - Get order details
 * POST /api/orders                   - Create new order (Admin only)
 * PUT  /api/orders/{id}/approve      - Approve order (Admin only)
 * PUT  /api/orders/{id}/deliver      - Mark delivered (Admin only)
 * PUT  /api/orders/{id}/cancel       - Cancel order (Admin only)
 * GET  /api/orders/status/{status}   - Filter by status
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PurchaseOrder>>> getAllOrders() {
        return ResponseEntity.ok(ApiResponse.success("Orders fetched", purchaseOrderService.getAllOrders()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PurchaseOrder>> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Order found", purchaseOrderService.getOrderById(id)));
    }

    /*
     * Request body format:
     * {
     *   "supplierId": 1,
     *   "notes": "Urgent restock",
     *   "userId": 1,
     *   "items": [
     *     { "drug": { "id": 1 }, "quantity": 100, "unitPrice": 5.50 }
     *   ]
     * }
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<ApiResponse<PurchaseOrder>> createOrder(@RequestBody Map<String, Object> body) {
        Long supplierId = Long.valueOf(body.get("supplierId").toString());
        Long userId = Long.valueOf(body.get("userId").toString());
        String notes = body.get("notes") != null ? body.get("notes").toString() : "";

        @SuppressWarnings("unchecked")
        List<PurchaseOrderItem> items = (List<PurchaseOrderItem>) body.get("items");

        PurchaseOrder order = purchaseOrderService.createOrder(supplierId, items, userId, notes);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Purchase order created", order));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PurchaseOrder>> approveOrder(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Order approved",
                purchaseOrderService.approveOrder(id)));
    }

    @PutMapping("/{id}/deliver")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PurchaseOrder>> markDelivered(@PathVariable Long id,
                                                                     @RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Order marked as delivered",
                purchaseOrderService.markDelivered(id, userId)));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PurchaseOrder>> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Order cancelled",
                purchaseOrderService.cancelOrder(id)));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<PurchaseOrder>>> getByStatus(
            @PathVariable PurchaseOrder.OrderStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Orders by status",
                purchaseOrderService.getOrdersByStatus(status)));
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<ApiResponse<List<PurchaseOrder>>> getBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(ApiResponse.success("Supplier orders",
                purchaseOrderService.getOrdersBySupplier(supplierId)));
    }
}
