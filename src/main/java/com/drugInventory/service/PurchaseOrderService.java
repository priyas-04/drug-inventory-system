package com.drugInventory.service;

import com.drugInventory.exception.ResourceNotFoundException;
import com.drugInventory.model.*;
import com.drugInventory.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PurchaseOrder Service - manages procurement from suppliers.
 * When an order is marked DELIVERED, inventory is automatically updated.
 *
 * Order lifecycle: PENDING → APPROVED → DELIVERED (or CANCELLED)
 */
@Service
public class PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all purchase orders.
     */
    public List<PurchaseOrder> getAllOrders() {
        return purchaseOrderRepository.findAll();
    }

    /**
     * Get a specific purchase order by ID.
     */
    public PurchaseOrder getOrderById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));
    }

    /**
     * Create a new purchase order with items.
     */
    @Transactional
    public PurchaseOrder createOrder(Long supplierId, List<PurchaseOrderItem> items, Long userId, String notes) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", supplierId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        PurchaseOrder order = new PurchaseOrder();
        order.setSupplier(supplier);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(PurchaseOrder.OrderStatus.PENDING);
        order.setNotes(notes);
        order.setCreatedBy(user);

        // Attach each item to this order and resolve the drug entity
        for (PurchaseOrderItem item : items) {
            Drug drug = drugRepository.findById(item.getDrug().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Drug", "id", item.getDrug().getId()));
            item.setDrug(drug);
            item.setPurchaseOrder(order);
        }
        order.setItems(items);

        return purchaseOrderRepository.save(order);
    }

    /**
     * Approve a PENDING order (moves it to APPROVED status).
     */
    @Transactional
    public PurchaseOrder approveOrder(Long orderId) {
        PurchaseOrder order = getOrderById(orderId);
        if (order.getStatus() != PurchaseOrder.OrderStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING orders can be approved");
        }
        order.setStatus(PurchaseOrder.OrderStatus.APPROVED);
        return purchaseOrderRepository.save(order);
    }

    /**
     * Mark an APPROVED order as DELIVERED.
     * This automatically updates inventory and logs transactions.
     */
    @Transactional
    public PurchaseOrder markDelivered(Long orderId, Long userId) {
        PurchaseOrder order = getOrderById(orderId);
        if (order.getStatus() != PurchaseOrder.OrderStatus.APPROVED) {
            throw new IllegalArgumentException("Only APPROVED orders can be marked as delivered");
        }

        User user = userRepository.findById(userId).orElseThrow();

        // Update inventory for each item in the order
        for (PurchaseOrderItem item : order.getItems()) {
            Drug drug = item.getDrug();

            // Find or create inventory record at WAREHOUSE
            Inventory inventory = inventoryRepository
                    .findByDrugIdAndLocation(drug.getId(), Inventory.Location.WAREHOUSE)
                    .orElseGet(() -> {
                        Inventory newInv = new Inventory();
                        newInv.setDrug(drug);
                        newInv.setLocation(Inventory.Location.WAREHOUSE);
                        newInv.setQuantity(0);
                        newInv.setLowStockThreshold(drug.getLowStockThreshold());
                        return newInv;
                    });

            // Add delivered quantity
            inventory.setQuantity(inventory.getQuantity() + item.getQuantity());
            inventoryRepository.save(inventory);

            // Update drug total quantity
            drug.setQuantity(drug.getQuantity() + item.getQuantity());
            drugRepository.save(drug);

            // Record PURCHASE transaction for audit trail
            Transaction transaction = new Transaction();
            transaction.setDrug(drug);
            transaction.setType(Transaction.TransactionType.PURCHASE);
            transaction.setQuantity(item.getQuantity());
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setPerformedBy(user);
            transaction.setNotes("Purchase Order #" + orderId + " delivered");
            transaction.setReferenceId(orderId);
            transactionRepository.save(transaction);
        }

        order.setStatus(PurchaseOrder.OrderStatus.DELIVERED);
        order.setDeliveryDate(LocalDateTime.now());
        return purchaseOrderRepository.save(order);
    }

    /**
     * Cancel a PENDING order.
     */
    @Transactional
    public PurchaseOrder cancelOrder(Long orderId) {
        PurchaseOrder order = getOrderById(orderId);
        if (order.getStatus() == PurchaseOrder.OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Delivered orders cannot be cancelled");
        }
        order.setStatus(PurchaseOrder.OrderStatus.CANCELLED);
        return purchaseOrderRepository.save(order);
    }

    /**
     * Get all orders for a specific supplier.
     */
    public List<PurchaseOrder> getOrdersBySupplier(Long supplierId) {
        return purchaseOrderRepository.findBySupplierIdOrderByOrderDateDesc(supplierId);
    }

    /**
     * Get orders filtered by status.
     */
    public List<PurchaseOrder> getOrdersByStatus(PurchaseOrder.OrderStatus status) {
        return purchaseOrderRepository.findByStatusOrderByOrderDateDesc(status);
    }
}
