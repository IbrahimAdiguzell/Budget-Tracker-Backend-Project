package com.financeproject.entity;

import java.time.LocalDate;
import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity representing a financial transaction (Income or Expense).
 * Maps to the 'transactions' table in the database.
 */
@Data
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Financial amount using BigDecimal for precision
    private BigDecimal amount; 

    private String description;

    // Mapped to 'transaction_date' in DB.
    // NOTE: Used as 'transactionDate' in Thymeleaf templates.
    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    // --- COMPATIBILITY & ALIAS METHODS ---
    // These methods act as a bridge between the database field ('transactionDate')
    // and generic DTOs or frontend inputs that expect simply 'date'.

    /**
     * Alias for getTransactionDate().
     * Allows accessing the date using 'date' property (e.g., in JSON or generic mappers).
     */
    public LocalDate getDate() {
        return transactionDate;
    }

    /**
     * Alias for setTransactionDate().
     * Allows setting the date using 'date' property.
     */
    public void setDate(LocalDate date) {
        this.transactionDate = date;
    }
}