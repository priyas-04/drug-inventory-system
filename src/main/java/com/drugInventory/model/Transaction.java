package com.drugInventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Transaction entity - audit log of every drug movement.
 * Each sale, purchase, or transfer creates a transaction record.
 * Provides a complete audit trail for reporting and compliance.
 */
@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The drug involved in this transaction
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    // Type of transaction
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Column(nullable = false)
    private Integer quantity; // How many units were involved

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    @Column(length = 255)
    private String notes; // Optional notes or reason

    // The user who performed this transaction
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User performedBy;

    // Reference to linked purchase order if applicable
    private Long referenceId;

    /**
     * Types of transactions logged in the system.
     */
    public enum TransactionType {
        PURCHASE,   // Drug purchased from supplier
        SALE,       // Drug sold to patient / dispensed
        TRANSFER,   // Drug moved from one location to another
        ADJUSTMENT, // Manual stock adjustment (loss, damage, etc.)
        RETURN      // Drug returned to supplier
    }
}
