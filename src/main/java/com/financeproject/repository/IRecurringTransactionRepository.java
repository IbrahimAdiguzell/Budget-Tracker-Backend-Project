package com.financeproject.repository;

import com.financeproject.entity.RecurringTransaction;
import com.financeproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for managing RecurringTransaction entities.
 */
public interface IRecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {
    
    // Retrieve all recurring transactions for a specific user
    List<RecurringTransaction> findByUser(User user);
    
    /**
     * Finds all recurring transactions scheduled for a specific day of the month.
     * Used by the Scheduler service to process automatic payments.
     * * @param dayOfMonth The day of execution (1-31)
     * @return List of transactions to be processed today
     */
    List<RecurringTransaction> findByDayOfMonth(int dayOfMonth);
}