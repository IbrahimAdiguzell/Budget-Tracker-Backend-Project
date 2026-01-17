package com.financeproject.controller;

import java.security.Principal;
import java.util.Map;
import java.util.Collections; // For list operations

// --- NEW IMPORTS (REQUIRED FOR SESSION MANAGEMENT) ---
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
// --------------------------------------------------

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.financeproject.dto.RegisterRequest;
import com.financeproject.entity.User;
import com.financeproject.repository.IUserRepository;
import com.financeproject.service.AuthService;

@Controller
public class AuthController {

    private final IUserRepository userRepository;
    private final AuthService authService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthController(AuthService authService, IUserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    // 1. REGISTER (Returns JSON)
    @PostMapping("/api/auth/register")
    @ResponseBody
    public ResponseEntity<?> register(@RequestBody RegisterRequest request){
        try {
            User createdUser = authService.register(request);
            return ResponseEntity.ok("User registered successfully! ID: " + createdUser.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. LOGIN (UPDATED SECTION) 🛠️
    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody com.financeproject.dto.LoginRequest request, HttpServletRequest httpServletRequest){
        try {
            // A) Validate password via service
            User user = authService.login(request);

            // B) If password is correct, Manually Initialize Session (Spring Security)
            // 1. Create authentication token
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.getEmail(), 
                    null, 
                    Collections.emptyList() // Role list is empty for now, sufficient for login
            );

            // 2. Create Security Context
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            // 3. Save this Context to Session
            HttpSession session = httpServletRequest.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

            return ResponseEntity.ok("Login successful! Welcome " + user.getName());
            
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid email or password!");
        }
    }

    // 3. ACCOUNT VERIFICATION (Redirects to HTML)
    @PostMapping("/auth/verify")
    public String verifyAccount(@RequestParam String email, @RequestParam String code) {
        try {
            authService.verifyUser(email, code);
            return "redirect:/login?verified=true"; 
        } catch (Exception e) {
            return "redirect:/verify?error=true&email=" + email;
        }
    }

    // 4. CHANGE PASSWORD (Returns JSON)
    @PostMapping("/api/auth/change-password")
    @ResponseBody
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest request, Principal principal) {
        try {
            if (principal == null) return ResponseEntity.status(401).body("Session not found!");
            
            User user = userRepository.findByEmail(principal.getName()).orElseThrow();
            
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
            
            return ResponseEntity.ok(Map.of("message", "Password updated successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // DTO Class
    public static class PasswordChangeRequest {
        private String newPassword;
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}