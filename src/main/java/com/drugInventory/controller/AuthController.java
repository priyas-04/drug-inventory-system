package com.drugInventory.controller;

import com.drugInventory.dto.ApiResponse;
import com.drugInventory.dto.AuthResponse;
import com.drugInventory.dto.LoginRequest;
import com.drugInventory.dto.RegisterRequest;
import com.drugInventory.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller - handles login and registration.
 * These endpoints are PUBLIC (no JWT required) - configured in SecurityConfig.
 *
 * POST /api/auth/login    - Login and receive JWT token
 * POST /api/auth/register - Register a new user account
 */
@RestController
@RequestMapping("/api/auth")

public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Login endpoint.
     * Returns JWT token + user info on success.
     * Throws exception (401) if credentials are wrong.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    /**
     * Register endpoint.
     * Creates a new user and returns a JWT token (auto-login).
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful", response));
    }
}
