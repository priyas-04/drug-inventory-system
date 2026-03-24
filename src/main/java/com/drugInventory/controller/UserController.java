package com.drugInventory.controller;

import com.drugInventory.dto.ApiResponse;
import com.drugInventory.model.User;
import com.drugInventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User Controller - Admin-only user management.
 *
 * GET    /api/users         - List all users (Admin)
 * GET    /api/users/{id}    - Get user (Admin)
 * PUT    /api/users/{id}    - Update user (Admin)
 * DELETE /api/users/{id}    - Soft-delete user (Admin)
 * PUT    /api/users/{id}/toggle - Toggle user active status
 */
@RestController
@RequestMapping("/api/users")

@PreAuthorize("hasRole('ADMIN')") // All endpoints in this controller require ADMIN role
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success("Users fetched", userService.getAllUsers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("User found", userService.getUserById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id,
                                                        @RequestBody User userData) {
        User updated = userService.updateUser(id, userData);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully"));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<User>> toggleActive(@PathVariable Long id) {
        User updated = userService.toggleActive(id);
        String status = updated.isActive() ? "activated" : "deactivated";
        return ResponseEntity.ok(ApiResponse.success("User " + status + " successfully", updated));
    }
}
