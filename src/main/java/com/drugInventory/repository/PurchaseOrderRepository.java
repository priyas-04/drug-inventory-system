package com.drugInventory.repository;

import com.drugInventory.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for PurchaseOrder entity.
 */
@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    // Find all orders for a specific supplier
    List<PurchaseOrder> findBySupplierIdOrderByOrderDateDesc(Long supplierId);

    // Find orders by status (e.g., all PENDING orders)
    List<PurchaseOrder> findByStatusOrderByOrderDateDesc(PurchaseOrder.OrderStatus status);

    // Find orders created by a specific user
    List<PurchaseOrder> findByCreatedByIdOrderByOrderDateDesc(Long userId);
}
