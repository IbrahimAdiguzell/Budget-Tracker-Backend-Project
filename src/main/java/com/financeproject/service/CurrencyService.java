package com.financeproject.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

/**
 * Service class responsible for fetching real-time financial data.
 * Integrates with ExchangeRate-API (Fiat) and Binance API (Crypto).
 */
@Service
public class CurrencyService {

    // Best Practice: Reading the API key from application.properties
    // This allows changing the key without modifying the Java code.
    @Value("${finance.api.currency-key}")
    private String apiKey;
    
    // Binance Public API (No authentication required for public ticker data)
    private final String BINANCE_URL = "https://api.binance.com/api/v3/ticker/price?symbol=BTCUSDT";

    // 1. FETCH USD -> TRY RATE
    public Double getUsdToTryRate() {
        // We use the 'apiKey' variable injected from properties
        String url = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/USD";
        // Returns 35.50 as a fallback if the API is down
        return fetchCurrencyRate(url, "TRY", 35.50); 
    }

    // 2. FETCH EUR -> TRY RATE
    public Double getEuroToTryRate() {
        String url = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/EUR";
        // Returns 38.20 as a fallback if the API is down
        return fetchCurrencyRate(url, "TRY", 38.20); 
    }

    // 3. FETCH BITCOIN PRICE (BTC -> USD) FROM BINANCE
    public Double getBtcToUsd() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            // Binance returns JSON format: {"symbol":"BTCUSDT","price":"95000.00"}
            Map<String, Object> response = restTemplate.getForObject(BINANCE_URL, Map.class);
            
            if (response != null && response.containsKey("price")) {
                String priceStr = (String) response.get("price");
                return Double.parseDouble(priceStr);
            }
        } catch (Exception e) {
            System.err.println("Error fetching Bitcoin data: " + e.getMessage());
        }
        // Default fallback price if API fails
        return 98000.0; 
    }

    // 4. GET GOLD GRAM RATE (MOCK IMPLEMENTATION)
    public Double getGoldGramRate() {
        // Free Gold APIs are rare or have strict limits.
        // Returning a static mock value for demonstration purposes.
        return 3100.0; 
    }

    // --- HELPER METHOD (DRY Principle) ---
    private Double fetchCurrencyRate(String url, String targetCurrency, Double fallbackValue) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.containsKey("conversion_rates")) {
                Map<String, Double> rates = (Map<String, Double>) response.get("conversion_rates");
                return rates.getOrDefault(targetCurrency, fallbackValue);
            }
        } catch (Exception e) {
            System.err.println("Currency API Error (" + url + "): " + e.getMessage());
        }
        return fallbackValue;
    }
}