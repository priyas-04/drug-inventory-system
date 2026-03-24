package com.drugInventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Inventory entity - tracks stock levels per drug per location.
 * A drug may be stocked at multiple locations (WAREHOUSE, PHARMACY, HOSPITAL).
 * This is the "real-time" view of stock at each physical location.
 */
@Entity
@Table(name = "inventory",
       uniqueConstraints = @UniqueConstraint(columnNames = {"drug_id", "location"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to the drug this inventory record tracks
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    // Physical location where this drug stock is held
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Location location;

    @Column(nullable = false)
    private Integer quantity; // Current stock quantity at this location

    // Alert triggered when quantity drops below this threshold
    @Column(nullable = false)
    private Integer lowStockThreshold = 10;

    /**
     * Defines possible locations in the supply chain.
     */
    public enum Location {
        WAREHOUSE,  // Central storage
        PHARMACY,   // Retail pharmacy
        HOSPITAL    // Hospital dispensary
    }
}
