package com.org.payment_processing_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_tbl")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paypalTransactionId;

    private String status;  // CREATED, APPROVED, EXECUTED, FAILED

    private String payerId;

    private Double amount;

    private String currency;

    private String failureReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
