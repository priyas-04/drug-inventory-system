package com.drugInventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User entity - represents a system user.
 * Each user has a role that controls what they can access:
 *   - ADMIN: full access
 *   - PHARMACIST: drug and inventory management
 *   - SUPPLIER: view orders, manage their own data
 */
@Entity
@Table(name = "users")
@Data                  // Lombok: generates getters, setters, toString, equals, hashCode
@NoArgsConstructor     // Lombok: generates no-args constructor
@AllArgsConstructor    // Lombok: generates all-args constructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password; // stored as bcrypt hash

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 100)
    private String fullName;

    @Column(length = 15)
    private String phone;

    // Enum stored as string in DB (e.g., "ADMIN", "PHARMACIST", "SUPPLIER")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private boolean active = true;

    /**
     * Enum defining all possible user roles in the system.
     */
    public enum Role {
        ADMIN,       // Full system access
        PHARMACIST,  // Drug and inventory management
        SUPPLIER     // Purchase orders and supply management
    }
}
