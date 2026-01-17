package com.financeproject.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DTOCategoryTotal {
    private String name;       // Category Name (e.g., Grocery)
    private Double amount;     // Total Amount (e.g., 500.0)

    // SCENARIO 1: If the database returns the result as Double
    public DTOCategoryTotal(String name, Double amount) {
        this.name = name;
        this.amount = amount != null ? amount : 0.0;
    }

    // SCENARIO 2: If the database returns the result as BigDecimal
    // (We convert it to Double for compatibility with the charting library)
    public DTOCategoryTotal(String name, BigDecimal amount) {
        this.name = name;
        this.amount = amount != null ? amount.doubleValue() : 0.0;
    }
}