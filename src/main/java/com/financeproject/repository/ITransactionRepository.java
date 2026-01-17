package com.financeproject.repository;

import com.financeproject.entity.Transaction;
import com.financeproject.entity.User;
import com.financeproject.dto.DTOCategoryTotal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for managing Transaction entities.
 * Includes custom JPQL queries for analytics and advanced filtering methods.
 */
public interface ITransactionRepository extends JpaRepository<Transaction, Long> {
    
    // 1. Standard Listing
    // Retrieve all transactions for a user, sorted by date (Newest first)
    List<Transaction> findByUserOrderByTransactionDateDesc(User user);

    // 2. Search Functionality
    // Finds transactions where the description contains the keyword (Case Insensitive)
    List<Transaction> findByUserAndDescriptionContainingIgnoreCaseOrderByTransactionDateDesc(User user, String keyword);

    // 3. Date Range Filter
    // Retrieves transactions within a specific start and end date (e.g., Weekly or Monthly view)
    List<Transaction> findByUserAndTransactionDateBetweenOrderByTransactionDateDesc(User user, LocalDate startDate, LocalDate endDate);

    // 4. Chart Data Aggregation (Custom JPQL)
    // Calculates total expenses grouped by category to populate the dashboard chart.
    @Query("SELECT new com.financeproject.dto.DTOCategoryTotal(t.category.name, SUM(t.amount)) " +
           "FROM Transaction t WHERE t.user = :user AND t.type = 'EXPENSE' " +
           "GROUP BY t.category.name")
    List<DTOCategoryTotal> getCategoryTotalAmount(@Param("user") User user);
}