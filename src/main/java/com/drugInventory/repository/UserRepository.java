package com.drugInventory.repository;

import com.drugInventory.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity.
 * JpaRepository provides CRUD operations automatically.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by username (for login authentication)
    Optional<User> findByUsername(String username);

    // Find user by email
    Optional<User> findByEmail(String email);

    // Check if username already exists (for registration validation)
    boolean existsByUsername(String username);

    // Check if email already exists
    boolean existsByEmail(String email);
}
