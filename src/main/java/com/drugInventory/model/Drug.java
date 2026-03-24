package com.drugInventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Drug entity - represents a medicine in the system.
 * Each drug has a unique batch number and is linked to a supplier.
 * The system tracks quantity, expiry, and price per drug batch.
 */
@Entity
@Table(name = "drugs",
       uniqueConstraints = @UniqueConstraint(columnNames = "batchNumber")) // Prevent duplicate batches
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Drug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // Drug name (e.g., Amoxicillin 500mg)

    @Column(nullable = false, unique = true, length = 50)
    private String batchNumber; // Unique batch/lot number

    @Column(nullable = false)
    private LocalDate expiryDate; // Expiry date - checked before dispensing

    @Column(nullable = false)
    private LocalDate manufactureDate;

    @Column(nullable = false)
    private Integer quantity; // Total quantity in stock

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // Price per unit

    @Column(length = 100)
    private String category; // e.g., Antibiotic, Painkiller, Vitamin

    @Column(length = 100)
    private String manufacturer;

    @Column(length = 255)
    private String description;

    // Low stock threshold - alert is triggered when quantity falls below this
    @Column(nullable = false)
    private Integer lowStockThreshold = 10;

    // Link to the supplier who provides this drug
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(nullable = false)
    private boolean active = true;
}
