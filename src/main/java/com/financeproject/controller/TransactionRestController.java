package com.financeproject.controller;

import com.financeproject.dto.DTOCategoryTotal;
import com.financeproject.dto.TransactionRequest;
import com.financeproject.entity.*;
import com.financeproject.repository.*;
import com.financeproject.service.ExcelService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionRestController {

    private final ITransactionRepository transactionRepository;
    private final IUserRepository userRepository;
    private final ICategoryRepository categoryRepository;
    private final ExcelService excelService; 

    public TransactionRestController(ITransactionRepository transactionRepository, 
                                     IUserRepository userRepository, 
                                     ICategoryRepository categoryRepository,
                                     ExcelService excelService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.excelService = excelService;
    }

    // 1. CREATE NEW TRANSACTION
    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody TransactionRequest request, Principal principal) {
        try {
            User user = userRepository.findByEmail(principal.getName()).orElseThrow();
            
            Transaction transaction = new Transaction();
            transaction.setAmount(request.getAmount());
            transaction.setDescription(request.getDescription());
            transaction.setType(TransactionType.valueOf(request.getType()));
            
            // Set date to current date (Compatible with Repository)
            transaction.setTransactionDate(LocalDate.now()); 
            
            transaction.setUser(user);

            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found!"));
            transaction.setCategory(category);

            transactionRepository.save(transaction);
            
            return ResponseEntity.ok("Transaction created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // 2. DELETE TRANSACTION
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
        try {
            transactionRepository.deleteById(id);
            return ResponseEntity.ok("Transaction deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Deletion failed: " + e.getMessage());
        }
    }

    // 3. UPDATE TRANSACTION
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable Long id, @RequestBody TransactionRequest request) {
        try {
            Transaction transaction = transactionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Transaction not found"));

            transaction.setAmount(request.getAmount());
            transaction.setDescription(request.getDescription());
            transaction.setType(TransactionType.valueOf(request.getType()));
            
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            transaction.setCategory(category);
            
            transactionRepository.save(transaction);
            return ResponseEntity.ok("Transaction updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Update failed: " + e.getMessage());
        }
    }

    // 4. CHART DATA (For Chart.js)
    @GetMapping("/chart-data")
    public List<DTOCategoryTotal> getChartData(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        // Calling custom query from Repository
        return transactionRepository.getCategoryTotalAmount(user);
    }

    // 5. EXPORT TO EXCEL
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportToExcel(Principal principal) throws IOException {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        
        // Generate file via ExcelService
        ByteArrayInputStream in = excelService.exportUserTransactions(user);

        HttpHeaders headers = new HttpHeaders();
        // Filename changed to English
        headers.add("Content-Disposition", "attachment; filename=transactions.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}