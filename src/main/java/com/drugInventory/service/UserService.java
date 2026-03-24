package com.drugInventory.service;

import com.drugInventory.exception.DuplicateEntryException;
import com.drugInventory.exception.ResourceNotFoundException;
import com.drugInventory.model.User;
import com.drugInventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User Service - Admin-level user management.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Transactional
    public User updateUser(Long id, User userData) {
        User user = getUserById(id);
        // Only update non-null field values
        if (userData.getFullName() != null) user.setFullName(userData.getFullName());
        if (userData.getEmail() != null) {
            if (!user.getEmail().equals(userData.getEmail()) &&
                    userRepository.existsByEmail(userData.getEmail())) {
                throw new DuplicateEntryException("Email already in use");
            }
            user.setEmail(userData.getEmail());
        }
        if (userData.getPhone() != null) user.setPhone(userData.getPhone());
        if (userData.getRole() != null) user.setRole(userData.getRole());
        if (userData.getPassword() != null && !userData.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userData.getPassword()));
        }
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        user.setActive(false); // Soft delete
        userRepository.save(user);
    }

    @Transactional
    public User toggleActive(Long id) {
        User user = getUserById(id);
        user.setActive(!user.isActive());
        return userRepository.save(user);
    }
}
