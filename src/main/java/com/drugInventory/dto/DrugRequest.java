package com.drugInventory.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating or updating a Drug.
 * Sent as request body in POST /api/drugs and PUT /api/drugs/{id}
 */
@Data
public class DrugRequest {

    @NotBlank(message = "Drug name is required")
    @Size(max = 100, message = "Drug name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Batch number is required")
    private String batchNumber;

    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;

    @NotNull(message = "Manufacture date is required")
    private LocalDate manufactureDate;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    private String category;

    private String manufacturer;

    private String description;

    @Min(value = 1, message = "Low stock threshold must be at least 1")
    private Integer lowStockThreshold = 10;

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;
}
