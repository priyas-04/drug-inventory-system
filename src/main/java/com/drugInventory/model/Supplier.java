package com.drugInventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Supplier entity - represents companies/vendors that supply drugs.
 * Suppliers are linked to drugs and purchase orders.
 */
@Entity
@Table(name = "suppliers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, length = 100)
    private String email;

    @Column(length = 15)
    private String phone;

    @Column(length = 255)
    private String address;

    // Unique license number required for pharmaceutical suppliers
    @Column(unique = true, length = 50)
    private String licenseNumber;

    @Column(length = 100)
    private String contactPerson;

    @Column(nullable = false)
    private boolean active = true;
}
