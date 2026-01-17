package com.financeproject.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.financeproject.entity.User;

/**
 * Repository interface for managing User entities.
 * Critical for Authentication and Authorization processes.
 */
public interface IUserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique email address.
     * Used mainly by the CustomUserDetailsService for login verification.
     * * @param email The email to search for
     * @return Optional<User> container (present if found, empty if not)
     */
    Optional<User> findByEmail(String email); 
}