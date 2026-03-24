package com.drugInventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * PurchaseOrder entity - represents an order placed to a supplier.
 * An order goes through status stages: PENDING → APPROVED → DELIVERED.
 * When DELIVERED, inventory is automatically updated.
 */
@Entity
@Table(name = "purchase_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Supplier this order is placed with
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    private LocalDateTime deliveryDate; // Filled when order is delivered

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(length = 255)
    private String notes;

    // User who created this order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    // List of items in this purchase order (one for each drug ordered)
    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderItem> items = new ArrayList<>();

    /**
     * Status lifecycle of a purchase order.
     */
    public enum OrderStatus {
        PENDING,    // Order created, waiting for approval
        APPROVED,   // Order approved, waiting for delivery
        DELIVERED,  // Drugs received, inventory updated
        CANCELLED   // Order cancelled
    }
}
