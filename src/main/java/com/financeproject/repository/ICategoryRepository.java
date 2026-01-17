package com.financeproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.financeproject.entity.Category;

/**
 * Repository interface for managing Category entities.
 * Inherits standard CRUD operations (save, findAll, delete) from JpaRepository.
 */
public interface ICategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds a specific category by its name.
     * Useful for preventing duplicate categories or linking transactions.
     * * @param name The name of the category
     * @return The Category entity
     */
    Category findByName(String name);
}