package com.financeproject.repository;

import com.financeproject.entity.Asset;
import com.financeproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for managing Asset entities.
 * Handles database operations related to user investments/assets.
 */
public interface IAssetRepository extends JpaRepository<Asset, Long> {
    
    // Retrieve all assets owned by a specific user
    List<Asset> findByUser(User user);
}