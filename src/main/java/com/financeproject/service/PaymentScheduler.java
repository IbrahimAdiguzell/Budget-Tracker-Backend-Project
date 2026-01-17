package com.financeproject.service;

import com.financeproject.entity.RecurringTransaction;
import com.financeproject.entity.Transaction;
import com.financeproject.repository.IRecurringTransactionRepository;
import com.financeproject.repository.ITransactionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Scheduled service that handles recurring payments automatically.
 * Runs daily to check if any recurring transaction needs to be processed.
 */
@Service
public class PaymentScheduler {

    private final IRecurringTransactionRepository recurringRepository;
    private final ITransactionRepository transactionRepository;

    public PaymentScheduler(IRecurringTransactionRepository recurringRepository, ITransactionRepository transactionRepository) {
        this.recurringRepository = recurringRepository;
        this.transactionRepository = transactionRepository;
    }

    // Cron Expression: Runs every day at 10:00 AM
    @Scheduled(cron = "0 0 10 * * ?") 
    public void processRecurringPayments() {
        int today = LocalDate.now().getDayOfMonth();
        System.out.println("⏰ Daily Scheduler Triggered! Processing Day: " + today);

        // Fetch tasks scheduled for today
        List<RecurringTransaction> tasks = recurringRepository.findByDayOfMonth(today);

        if (tasks.isEmpty()) {
            System.out.println("ℹ️ No recurring payments found for today.");
            return;
        }

        for (RecurringTransaction task : tasks) {
            Transaction t = new Transaction();
            
            // Add a prefix to distinguish auto-payments
            t.setDescription("Auto-Payment: " + task.getDescription());
            
            // Critical: Convert Double (from Recurring) to BigDecimal (for Transaction)
            // This ensures precision compatibility between entities.
            t.setAmount(BigDecimal.valueOf(task.getAmount())); 

            t.setType(task.getType());
            t.setCategory(task.getCategory());
            t.setUser(task.getUser());
            t.setTransactionDate(LocalDate.now());

            transactionRepository.save(t);
            System.out.println("✅ Processed: " + task.getDescription());
        }
    }
}