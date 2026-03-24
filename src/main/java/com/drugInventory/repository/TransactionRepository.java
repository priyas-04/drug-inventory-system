package com.drugInventory.repository;

import com.drugInventory.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Transaction entity - provides sales history and audit trail queries.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Get all transactions for a specific drug
    List<Transaction> findByDrugIdOrderByTransactionDateDesc(Long drugId);

    // Get all transactions of a specific type (e.g., all sales)
    List<Transaction> findByTypeOrderByTransactionDateDesc(Transaction.TransactionType type);

    // Get transactions within a date range (for period reports)
    List<Transaction> findByTransactionDateBetweenOrderByTransactionDateDesc(
            LocalDateTime startDate, LocalDateTime endDate);

    // Get transactions performed by a specific user
    List<Transaction> findByPerformedByIdOrderByTransactionDateDesc(Long userId);
}
