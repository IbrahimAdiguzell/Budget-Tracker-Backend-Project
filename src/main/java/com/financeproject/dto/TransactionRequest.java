package com.financeproject.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) for creating or updating financial transactions.
 * Uses BigDecimal for high precision in monetary values.
 */
@Data
public class TransactionRequest {
    
    private BigDecimal amount; 
    private String description;
    private Long categoryId;
    private String type; // Enum value as String (INCOME / EXPENSE)
}