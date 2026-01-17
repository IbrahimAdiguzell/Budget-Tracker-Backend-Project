package com.financeproject.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity representing a sub-category for more granular expense tracking.
 * Used to define specific merchants or brands under a main category.
 */
@Entity
@Data
@Table(name = "sub_categories")
public class SubCategory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String name; // e.g., Netflix, Starbucks, Gym
	
	@ManyToOne
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
}