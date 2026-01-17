package com.financeproject.controller;

import com.financeproject.entity.*;
import com.financeproject.repository.*;
import com.financeproject.service.CurrencyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate; // Required for date operations
import java.util.ArrayList;
import java.util.List;

@Controller
public class PageController {

    private final ICategoryRepository categoryRepository;
    private final ITransactionRepository transactionRepository;
    private final IUserRepository userRepository;
    private final IAssetRepository assetRepository;
    private final CurrencyService currencyService;
    private final IRecurringTransactionRepository recurringRepository;

    // Dependency Injection via Constructor (Best Practice)
    public PageController(ICategoryRepository categoryRepository, 
                          ITransactionRepository transactionRepository, 
                          IUserRepository userRepository,
                          IAssetRepository assetRepository,
                          CurrencyService currencyService,
                          IRecurringTransactionRepository recurringRepository) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
        this.currencyService = currencyService;
        this.recurringRepository = recurringRepository;
    }
    
    @GetMapping("/welcome")
    public String landingPage(Principal principal) {
        // If the user is already logged in, redirect to the dashboard immediately
        if (principal != null) {
            return "redirect:/";
        }
        return "landing";
    }

    @GetMapping("/")
    public String homePage(Model model, Principal principal, 
                           @RequestParam(required = false) String keyword,
                           @RequestParam(required = false, defaultValue = "month") String filter) { // ADDED FILTER PARAMETER
        try {
            if (principal == null) return "redirect:/login";

            String email = principal.getName();
            User user = userRepository.findByEmail(email).orElseThrow();

            List<Transaction> transactions = new ArrayList<>();
            
            // --- DATE FILTER LOGIC ---
            LocalDate today = LocalDate.now();
            LocalDate startDate;
            LocalDate endDate = today; // End date is today

            if ("week".equals(filter)) {
                startDate = today.minusDays(7);
            } else if ("month".equals(filter)) {
                startDate = today.withDayOfMonth(1); // First day of the month
                endDate = today.withDayOfMonth(today.lengthOfMonth()); // Last day of the month
            } else if ("year".equals(filter)) {
                startDate = today.withDayOfYear(1); // First day of the year
            } else if ("all".equals(filter)) {
                startDate = LocalDate.of(2000, 1, 1); // Historical data
            } else {
                // Default: This Month
                startDate = today.withDayOfMonth(1);
                endDate = today.withDayOfMonth(today.lengthOfMonth());
            }

            // --- DATA FETCHING (SEARCH or FILTER) ---
            if (keyword != null && !keyword.isEmpty()) {
                // If searching, ignore date filter for now and search by description
                transactions = transactionRepository.findByUserAndDescriptionContainingIgnoreCaseOrderByTransactionDateDesc(user, keyword);
            } else {
                // If no search, fetch data within the selected date range
                transactions = transactionRepository.findByUserAndTransactionDateBetweenOrderByTransactionDateDesc(user, startDate, endDate);
            }

            double totalIncome = 0;
            double totalExpense = 0;

            // --- INCOME/EXPENSE CALCULATION ---
            if (transactions != null) {
                for (Transaction t : transactions) {
                    // Null category protection
                    if (t.getCategory() == null) {
                        Category tempCat = new Category();
                        tempCat.setName("General"); // Changed "Genel" to "General"
                        t.setCategory(tempCat);
                    }
                    
                    double amount = (t.getAmount() != null) ? t.getAmount().doubleValue() : 0.0;
                    
                    if (t.getType() == TransactionType.INCOME) {
                        totalIncome += amount;
                    } else {
                        totalExpense += amount;
                    }
                }
            }
            
            double balance = totalIncome - totalExpense;

            // --- BUDGET PROGRESS BAR (Dynamic based on Income) ---
            int progressPercentage = 0;
            String progressColor = "bg-success"; // Default Green

            // Calculate only if there is income (prevent division by zero)
            if (totalIncome > 0) {
                progressPercentage = (int) ((totalExpense / totalIncome) * 100);
                
                if (progressPercentage >= 100) {
                    progressPercentage = 100; // Prevent overflow
                    progressColor = "bg-danger"; // Red (Over budget!)
                } else if (progressPercentage >= 80) {
                    progressColor = "bg-warning"; // Yellow (Warning, close to limit)
                }
            }

            // --- AI ADVICE (Translated to English) ---
            String aiAdvice = "Your financial status looks stable. 👍";
            if (totalIncome > 0) {
                if (totalExpense > totalIncome) aiAdvice = "⚠️ WARNING: Expenses exceed income!";
                else if (totalExpense > (totalIncome * 0.8)) aiAdvice = "⚠️ CAUTION: You have spent 80% of your income.";
                else aiAdvice = "✅ GREAT: Your budget is healthy.";
            }

            // --- MODEL ATTRIBUTES ---
            model.addAttribute("categoryList", categoryRepository.findAll());
            model.addAttribute("transactions", transactions);
            model.addAttribute("totalIncome", totalIncome);
            model.addAttribute("totalExpense", totalExpense);
            model.addAttribute("balance", balance);
            model.addAttribute("aiAdvice", aiAdvice);
            model.addAttribute("keyword", keyword);
            
            // Progress Bar Data
            model.addAttribute("progressPercentage", progressPercentage);
            model.addAttribute("progressColor", progressColor);
            
            model.addAttribute("currentUserName", user.getName()); 
            model.addAttribute("isAdmin", "ROLE_ADMIN".equals(user.getRole())); 
            
            model.addAttribute("monthlyLimit", user.getMonthlyLimit() != null ? user.getMonthlyLimit() : 0.0);

            // Send active filter to HTML (to highlight the button)
            model.addAttribute("currentFilter", filter);
            
            return "home";
        } catch (Exception e) {
            e.printStackTrace(); 
            return "redirect:/login?error=true";
        }
    }

    @GetMapping("/investments")
    public String investmentPage(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        
        // Fetch Assets
        List<Asset> assets = assetRepository.findByUser(user);
        
        // Current Rates
        double usdRate = currencyService.getUsdToTryRate();
        double goldRate = currencyService.getGoldGramRate();
        double btcUsdRate = currencyService.getBtcToUsd();
        double btcTryRate = btcUsdRate * usdRate; // Convert BTC to TRY

        // --- CALCULATE TOTAL WEALTH ---
        double totalWealth = 0.0;
        
        for (Asset asset : assets) {
            double value = 0;
            switch (asset.getType()) {
                case GOLD:
                    value = asset.getQuantity() * goldRate;
                    break;
                case FOREX: // Assuming USD
                    value = asset.getQuantity() * usdRate;
                    break;
                case CRYPTO: // Assuming Bitcoin
                    value = asset.getQuantity() * btcTryRate;
                    break;
                default:
                    value = 0;
            }
            totalWealth += value;
        }

        model.addAttribute("assets", assets);
        model.addAttribute("usdRate", usdRate);
        model.addAttribute("goldRate", goldRate);
        model.addAttribute("btcRate", btcTryRate); // BTC in TRY
        model.addAttribute("totalWealth", totalWealth); 
        
        model.addAttribute("isAdmin", "ROLE_ADMIN".equals(user.getRole()));
        model.addAttribute("currentUserName", user.getName());
        return "investments";
    }

    @GetMapping("/profile")
    public String profilePage(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("isAdmin", "ROLE_ADMIN".equals(user.getRole()));
        model.addAttribute("currentUserName", user.getName());
        return "profile";
    }

    @GetMapping("/login") public String loginPage() { return "login"; }
    @GetMapping("/register") public String registerPage() { return "register"; }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        
        User admin = userRepository.findByEmail(principal.getName()).orElseThrow();
        
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("currentUserName", admin.getName());
        model.addAttribute("isAdmin", true);
        
        return "admin/dashboard";
    }
    
    // Opens Verification Page
    @GetMapping("/verify")
    public String verifyPage(@RequestParam(required = false) String email, Model model) {
        // Autofill email if present in URL
        if (email != null) {
            model.addAttribute("email", email);
        }
        return "verify";
    }
    
    @GetMapping("/recurring")
    public String recurringPage(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        
        model.addAttribute("tasks", recurringRepository.findByUser(user));
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("currentUserName", user.getName());
        model.addAttribute("isAdmin", "ROLE_ADMIN".equals(user.getRole()));
        
        return "recurring";
    }
}