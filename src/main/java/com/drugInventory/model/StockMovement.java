package com.drugInventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * StockMovement entity - tracks physical movement of drugs between locations.
 * This captures the supply chain flow: Warehouse → Pharmacy / Hospital.
 * Each movement decrements stock at source and increments at destination.
 */
@Entity
@Table(name = "stock_movements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Drug that was moved
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    // Source location (where the drug came from)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Inventory.Location fromLocation;

    // Destination location (where the drug is going)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Inventory.Location toLocation;

    @Column(nullable = false)
    private Integer quantity; // How many units were moved

    @Column(nullable = false)
    private LocalDateTime movedAt;

    @Column(length = 255)
    private String reason; // Reason for the movement

    // User who initiated the movement
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moved_by")
    private User movedBy;
}
