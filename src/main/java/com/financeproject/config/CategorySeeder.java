package com.financeproject.config;

import com.financeproject.entity.Category;
import com.financeproject.repository.ICategoryRepository;
import com.financeproject.util.CategoryHelper; // Imported our helper class
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategorySeeder implements CommandLineRunner {

    private final ICategoryRepository categoryRepository;

    public CategorySeeder(ICategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Category> categories = categoryRepository.findAll();

        for (Category cat : categories) {
            // If the icon is missing, assign one using the Helper class
            if (cat.getIcon() == null || cat.getIcon().isEmpty()) {
                cat.setIcon(CategoryHelper.getIconForName(cat.getName()));
                categoryRepository.save(cat);
            }
        }
        
        // Create default categories if none exist
        if (categories.isEmpty()) {
            createCategory("Gıda & Market");
            createCategory("Ulaşım");
            createCategory("Kira & Fatura");
            createCategory("Eğlence");
            createCategory("Giyim");
            createCategory("Sağlık");
            createCategory("Eğitim");
            createCategory("Maaş");
            createCategory("Elektronik");
        }
    }

    private void createCategory(String name) {
        Category c = new Category();
        c.setName(name);
        // Automatically assign icon based on name
        c.setIcon(CategoryHelper.getIconForName(name));
        categoryRepository.save(c);
    }
}