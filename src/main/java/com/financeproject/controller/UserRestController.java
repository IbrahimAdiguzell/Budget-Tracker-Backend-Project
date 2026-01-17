package com.financeproject.controller;

import com.financeproject.entity.User;
import com.financeproject.repository.IUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

    private final IUserRepository userRepository;

    public UserRestController(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // UPDATE PROFILE INFO (Name + Budget Limit)
    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> updates, Principal principal) {
        try {
            User user = userRepository.findByEmail(principal.getName()).orElseThrow();

            if (updates.containsKey("name")) {
                user.setName((String) updates.get("name"));
            }
            
            if (updates.containsKey("monthlyLimit")) {
                // Numbers from JSON can be Integer or Double, safer to convert to String first then parse
                String limitStr = String.valueOf(updates.get("monthlyLimit"));
                user.setMonthlyLimit(Double.parseDouble(limitStr));
            }

            userRepository.save(user);
            return ResponseEntity.ok("Profile updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}