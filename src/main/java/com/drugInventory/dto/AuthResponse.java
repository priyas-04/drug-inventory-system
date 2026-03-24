package com.drugInventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication response.
 * Returned to client after successful login.
 * Contains the JWT token and user information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String token;          // JWT token to be sent in future requests
    private String username;
    private String role;           // User role (ADMIN, PHARMACIST, SUPPLIER)
    private String fullName;
    private String email;
    private String message;        // e.g., "Login successful"
}
