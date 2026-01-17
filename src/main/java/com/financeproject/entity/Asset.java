package com.financeproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Entity representing a financial asset (Investment).
 * Stores information about user investments like Gold, Forex, or Crypto.
 */
@Data
@Entity
@Table(name = "assets")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g., Gold, USD, Bitcoin
    
    // Quantity owned (e.g., 10.5 grams or 100 units)
    private Double quantity; 

    // Average purchase price (Optional, used for Profit/Loss calculation)
    private BigDecimal buyPrice; 

    // Asset category (GOLD, FOREX, CRYPTO)
    @Enumerated(EnumType.STRING)
    private AssetType type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}