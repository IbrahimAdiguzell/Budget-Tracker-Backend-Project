package com.financeproject.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity representing recurring transactions (e.g., Monthly Rent, Netflix Subscription).
 * These records are used by the Scheduler to automatically generate transactions.
 */
@Data
@Entity
@Table(name = "recurring_transactions")
public class RecurringTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description; // e.g., Rent, Gym Membership
    private Double amount;      // Transaction amount
    private int dayOfMonth;     // The day of the month execution (e.g., 1 for the 1st of every month)

    @Enumerated(EnumType.STRING)
    private TransactionType type; // INCOME or EXPENSE

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}