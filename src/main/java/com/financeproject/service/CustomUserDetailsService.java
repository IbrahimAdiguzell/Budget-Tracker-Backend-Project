package com.financeproject.service;

import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.financeproject.entity.User;
import com.financeproject.repository.IUserRepository;

/**
 * Custom service to bridge the application's database with Spring Security.
 * Loads user-specific data during the authentication process.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	private final IUserRepository userRepository;
	
	public CustomUserDetailsService(IUserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
    /**
     * Locates the user based on the username (email in this case).
     * This method is called automatically by Spring Security during login.
     */
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// 1. Retrieve the user from the database
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
		
		// 2. Convert the custom 'User' entity to Spring Security's 'UserDetails' object.
        // We use the full constructor to handle the account status (enabled/disabled).
		return new org.springframework.security.core.userdetails.User(
				user.getEmail(),
				user.getPassword(),
				user.isEnabled(), // Checks if the email is verified (True = Active, False = Inactive)
				true, // accountNonExpired: true means the account never expires
				true, // credentialsNonExpired: true means the password never expires
				true, // accountNonLocked: true means the account is not banned/locked
				Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) // Assign default authority
		);
	}
}