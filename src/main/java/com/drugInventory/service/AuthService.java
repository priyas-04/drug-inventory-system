package com.drugInventory.service;

import com.drugInventory.dto.AuthResponse;
import com.drugInventory.dto.LoginRequest;
import com.drugInventory.dto.RegisterRequest;
import com.drugInventory.exception.DuplicateEntryException;
import com.drugInventory.model.User;
import com.drugInventory.repository.UserRepository;
import com.drugInventory.security.JwtUtil;
import com.drugInventory.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication Service - handles login and user registration.
 * Uses Spring Security's AuthenticationManager to validate credentials.
 * Returns a JWT token on successful login.
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Authenticates user and returns a JWT token.
     * @throws org.springframework.security.core.AuthenticationException if credentials are invalid
     */
    public AuthResponse login(LoginRequest request) {
        // Attempt authentication - throws exception if credentials are wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        // Load user details and fetch the User entity
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        // Generate JWT token with user's role embedded
        String token = jwtUtil.generateToken(userDetails, user.getRole().name());

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getRole().name(),
                user.getFullName(),
                user.getEmail(),
                "Login successful"
        );
    }

    /**
     * Registers a new user in the system.
     * Checks for duplicate username and email before saving.
     */
    public AuthResponse register(RegisterRequest request) {
        // Check for duplicate username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateEntryException("Username '" + request.getUsername() + "' is already taken");
        }

        // Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEntryException("Email '" + request.getEmail() + "' is already registered");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Hash password
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setActive(true);

        userRepository.save(user);

        // Auto-login: generate token for the new user
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails, user.getRole().name());

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getRole().name(),
                user.getFullName(),
                user.getEmail(),
                "Registration successful"
        );
    }
}
