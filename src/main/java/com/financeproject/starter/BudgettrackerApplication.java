package com.financeproject.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Budget Tracker application.
 * Initializes the Spring Boot context, component scanning, and background scheduling tasks.
 */
@SpringBootApplication
@EnableScheduling // Enables the PaymentScheduler to run background tasks (Cron Jobs)
// Explicit scanning is required because this class is located in the '.starter' sub-package
@ComponentScan(basePackages = "com.financeproject")
@EnableJpaRepositories(basePackages = "com.financeproject.repository")
@EntityScan(basePackages = "com.financeproject.entity")
public class BudgettrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BudgettrackerApplication.class, args);
	}
	
	/**
     * Global Bean definition for RestTemplate.
     * Used for making HTTP requests to external APIs (e.g., Currency Exchange, Binance).
     */
	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}