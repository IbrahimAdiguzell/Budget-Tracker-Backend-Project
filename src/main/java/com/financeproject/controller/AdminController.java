package com.financeproject.controller;

import com.financeproject.entity.Category;
import com.financeproject.entity.User;
import com.financeproject.repository.ICategoryRepository;
import com.financeproject.repository.IUserRepository;
import com.financeproject.util.CategoryHelper; // Imported utility class for icons

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
@RequestMapping("/admin") // All endpoints start with /admin
public class AdminController {

    private final ICategoryRepository categoryRepository;
    private final IUserRepository userRepository;

    public AdminController(ICategoryRepository categoryRepository, IUserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        
        String email = principal.getName();
        User admin = userRepository.findByEmail(email).orElseThrow();

        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        
        // Data required for Navbar and welcome message
        model.addAttribute("currentUserName", admin.getName());
        model.addAttribute("isAdmin", true);
        
        return "admin/dashboard"; 
    }

    @PostMapping("/categories/add")
    public String addCategory(@RequestParam String name) {
        Category category = new Category();
        category.setName(name);

        // --- AUTOMATIC ICON ASSIGNMENT ---
        // Even if the admin only enters a name, the Helper class assigns a suitable icon.
        String icon = CategoryHelper.getIconForName(name);
        category.setIcon(icon);
        // ---------------------------------

        categoryRepository.save(category);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        // Note: Deletion might fail if there are transactions linked to this category.
        // Future improvement: Implement try-catch or cascade delete.
        try {
            categoryRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println("Category could not be deleted (Data might be linked): " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}