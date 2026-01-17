package com.financeproject.controller;

import com.financeproject.entity.*;
import com.financeproject.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/recurring")
public class RecurringRestController {

    private final IRecurringTransactionRepository recurringRepository;
    private final IUserRepository userRepository;
    private final ICategoryRepository categoryRepository;

    public RecurringRestController(IRecurringTransactionRepository recurringRepository, IUserRepository userRepository, ICategoryRepository categoryRepository) {
        this.recurringRepository = recurringRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    // 1. ADD RECURRING TRANSACTION
    @PostMapping
    public ResponseEntity<?> addRecurring(@RequestBody RecurringTransaction task, @RequestParam Long categoryId, Principal principal) {
        try {
            User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
            Category cat = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
            
            task.setUser(user);
            task.setCategory(cat);
            
            recurringRepository.save(task);
            return ResponseEntity.ok("Recurring transaction created successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // 2. DELETE RECURRING TRANSACTION
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecurring(@PathVariable Long id) {
        try {
            recurringRepository.deleteById(id);
            return ResponseEntity.ok("Recurring transaction deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: Could not delete transaction.");
        }
    }
}