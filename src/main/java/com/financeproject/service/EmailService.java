package com.financeproject.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service responsible for sending email notifications.
 * Uses Spring's JavaMailSender interface.
 */
@Service
public class EmailService {
	
	private final JavaMailSender mailSender;

    // We read the sender email directly from application.properties
    // This prevents hardcoding your personal email in the Java class.
    @Value("${spring.mail.username}")
    private String fromEmail;

    // Dependency Injection via Constructor (Best Practice)
	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendVerificationEmail(String toEmail, String code) {
		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setFrom(fromEmail);
		message.setTo(toEmail);
		message.setSubject("BudgetTracker - Account Verification");
		
		// Professional English content
		String emailBody = "Hello,\n\n" +
		                   "Your account verification code is: " + code + "\n\n" +
		                   "Please enter this code to activate your account.\n\n" +
		                   "Best Regards,\n" +
		                   "BudgetTracker Team";
		
		message.setText(emailBody);
	
		mailSender.send(message);
		System.out.println("Verification email sent to: " + toEmail);
	}
}