package com.financeproject.config;

import com.financeproject.entity.Category;
import com.financeproject.repository.ICategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ICategoryRepository categoryRepository;

    public DataSeeder(ICategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // If no categories exist in the database, seed with default ones
        if (categoryRepository.count() == 0) {
            List<String> categories = Arrays.asList(
                "Groceries & Food", "Rent", "Bills", "Transportation", "Entertainment", 
                "Health", "Education", "Clothing", "Electronics", "Holiday & Travel",
                "Sports", "Personal Care", "Gifts", "Investment", "Debt Payment",
                "Fuel", "Home Appliances", "Restaurant", "Cafe", "Other"
            );

            for (String catName : categories) {
                Category category = new Category();
                category.setName(catName);
                categoryRepository.save(category);
            }
            System.out.println("✅ 20 Categories loaded successfully!");
        }
    }
}