package com.org.payment_processing_system.repo;

import com.org.payment_processing_system.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByPaypalTransactionId(String paypalTransactionId);
}
