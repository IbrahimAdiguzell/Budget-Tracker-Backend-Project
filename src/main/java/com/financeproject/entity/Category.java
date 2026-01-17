package com.financeproject.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity representing a transaction category (e.g., Groceries, Rent, Transport).
 * Mapped to the 'categories' table in the database.
 */
@Data
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment Primary Key
    @Column(name = "category_id")
    private Long id;
    
    @Column(nullable = false, unique = true) // Category names must be unique
    private String name;
    
    private String icon; // Stores the Emoji or FontAwesome class for UI
}