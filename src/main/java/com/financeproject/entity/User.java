package com.financeproject.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity representing a system user.
 * Manages authentication credentials, roles, and profile settings.
 */
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Unique identifier for login. Prevents duplicate registrations.
    @Column(unique = true, nullable = false)
    private String email;
    
    // Stores the BCrypt hash of the password. NEVER stores plain text.
    @Column(nullable = false)
    private String password;
    
    private String name;
    
    // Authorization role (USER, ADMIN, etc.). Defaults to "USER".
    private String role = "USER";
    
    // A random code sent via email for account verification
    @Column(name = "verification_code")
    private String verificationCode; 

    // Flag indicating if the account is active. 
    // False by default until the user verifies their email.
    @Column(name = "enabled")
    private boolean enabled = false; 

    // User-defined monthly spending cap for budget tracking
    private Double monthlyLimit = 0.0; 
}