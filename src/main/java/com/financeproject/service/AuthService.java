package com.financeproject.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.financeproject.dto.LoginRequest;
import com.financeproject.dto.RegisterRequest;
import com.financeproject.entity.User;
import com.financeproject.repository.IUserRepository;

import java.util.Random;

/**
 * Service class handling Authentication and Authorization logic.
 * Manages Registration, Login, and Email Verification processes.
 */
@Service
public class AuthService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Handles secure password hashing
    private final AuthenticationManager authenticationManager; // Spring Security Manager
    private final EmailService emailService;
    
    public AuthService(IUserRepository userRepository, 
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }
    
    /**
     * Registers a new user into the system.
     * Generates a verification code and sends it via email.
     */
    public User register(RegisterRequest request) {
        // 1. Check if email is already taken
        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already registered!");
        }
    
        // 2. Initialize new User entity
        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        
        // 3. Encrypt the password before saving
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
    
        // 4. Generate a random 6-digit verification code
        String randomCode = String.valueOf(new Random().nextInt(900000) + 100000);
        newUser.setVerificationCode(randomCode);
        
        // 5. Disable account initially (User must verify email first)
        newUser.setEnabled(false);
        
        // 6. Save to Database
        User savedUser = userRepository.save(newUser);
        
        // 7. Send Verification Email
        try {
            emailService.sendVerificationEmail(request.getEmail(), randomCode);
        } catch (Exception e) {
            // In a real production environment, use a Logger (SLF4J) here instead of sysout.
            System.err.println("Email Service Error: " + e.getMessage());
        }
        
        return savedUser;
    }
    
    /**
     * Authenticates a user using email and password.
     * Uses Spring Security's AuthenticationManager.
     */
    public User login(LoginRequest request) {
        // This line triggers the CustomUserDetailsService.
        // If password is wrong, it automatically throws a 'BadCredentialsException'.
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        // If authentication passed, return the user details
        return userRepository.findByEmail(request.getEmail()).orElseThrow();
    }
    
    /**
     * Verifies the user's account using the code sent via email.
     */
    public void verifyUser(String email, String code) {
        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        
        // Check if the provided code matches the one in DB
        if (user.getVerificationCode() != null && user.getVerificationCode().equals(code)) {
            user.setEnabled(true);           // Activate account
            user.setVerificationCode(null);  // Clear code (One-time use)
            userRepository.save(user);       // Update DB
        } else {
            throw new RuntimeException("Invalid or expired verification code!");
        }
    }
}