package com.financeproject.util;

/**
 * Utility class to assign emojis based on category names.
 * Supports both English and Turkish keywords for auto-detection.
 */
public class CategoryHelper {

    public static String getIconForName(String name) {
        if (name == null) return "📂";
        
        String lowerName = name.toLowerCase();
        
        // 1. GROCERY / MARKET
        if (lowerName.contains("market") || lowerName.contains("grocery") || lowerName.contains("food") || 
            lowerName.contains("gıda") || lowerName.contains("pazar") || lowerName.contains("supermarket")) {
            return "🛒";
        }
        
        // 2. TRANSPORT / ULAŞIM
        if (lowerName.contains("transport") || lowerName.contains("bus") || lowerName.contains("taxi") || 
            lowerName.contains("gas") || lowerName.contains("fuel") || lowerName.contains("uber") ||
            lowerName.contains("ulaşım") || lowerName.contains("benzin") || lowerName.contains("otobüs")) {
            return "🚌";
        }
        
        // 3. HOUSING / EV & FATURA
        if (lowerName.contains("rent") || lowerName.contains("home") || lowerName.contains("bill") || 
            lowerName.contains("electric") || lowerName.contains("water") || lowerName.contains("internet") ||
            lowerName.contains("kira") || lowerName.contains("ev") || lowerName.contains("aidat") || lowerName.contains("fatura")) {
            return "🏠";
        }
        
        // 4. ENTERTAINMENT / EĞLENCE & YEMEK
        if (lowerName.contains("entertainment") || lowerName.contains("cinema") || lowerName.contains("movie") || 
            lowerName.contains("restaurant") || lowerName.contains("cafe") || lowerName.contains("bar") ||
            lowerName.contains("eğlence") || lowerName.contains("sinema") || lowerName.contains("yemek")) {
            return "🎉";
        }
        
        // 5. CLOTHING / GİYİM
        if (lowerName.contains("shopping") || lowerName.contains("cloth") || lowerName.contains("fashion") || 
            lowerName.contains("wear") || lowerName.contains("giyim") || lowerName.contains("kıyafet")) {
            return "👕";
        }
        
        // 6. HEALTH / SAĞLIK
        if (lowerName.contains("health") || lowerName.contains("pharmacy") || lowerName.contains("doctor") || 
            lowerName.contains("hospital") || lowerName.contains("medicine") || 
            lowerName.contains("sağlık") || lowerName.contains("eczane")) {
            return "💊";
        }
        
        // 7. EDUCATION / EĞİTİM
        if (lowerName.contains("education") || lowerName.contains("school") || lowerName.contains("book") || 
            lowerName.contains("course") || lowerName.contains("university") ||
            lowerName.contains("eğitim") || lowerName.contains("okul") || lowerName.contains("kitap")) {
            return "🎓";
        }
        
        // 8. INCOME / GELİR
        if (lowerName.contains("salary") || lowerName.contains("wage") || lowerName.contains("income") || 
            lowerName.contains("bonus") || lowerName.contains("maaş") || lowerName.contains("gelir") || lowerName.contains("prim")) {
            return "💰";
        }
        
        // 9. TECH / TEKNOLOJİ
        if (lowerName.contains("tech") || lowerName.contains("phone") || lowerName.contains("computer") || 
            lowerName.contains("laptop") || lowerName.contains("software") ||
            lowerName.contains("elektronik") || lowerName.contains("telefon") || lowerName.contains("bilgisayar")) {
            return "💻";
        }
        
        // 10. SPORT / SPOR
        if (lowerName.contains("sport") || lowerName.contains("gym") || lowerName.contains("fitness") || 
            lowerName.contains("workout") || lowerName.contains("spor")) {
            return "💪";
        }
        
        // 11. TRAVEL / SEYAHAT
        if (lowerName.contains("travel") || lowerName.contains("holiday") || lowerName.contains("flight") || 
            lowerName.contains("hotel") || lowerName.contains("tatil") || lowerName.contains("seyahat")) {
            return "✈️";
        }
        
        return "📂"; // Default fallback icon
    }
}