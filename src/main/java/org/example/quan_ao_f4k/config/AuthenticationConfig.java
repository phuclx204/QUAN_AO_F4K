package org.example.quan_ao_f4k.config;

import lombok.extern.slf4j.Slf4j;
import org.example.quan_ao_f4k.model.authentication.Role;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.repository.authentication.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Slf4j
public class AuthenticationConfig {
	private final UserRepository userRepository;

	public AuthenticationConfig(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

//	@Bean
//	ApplicationRunner applicationRunner(){
//		return args -> {
//			if(userRepository.findByUsername("admin").isEmpty()) {
//				User user = User.builder()
//						.username("admin")
//						.password(passwordEncoder().encode("admin"))
//						.role(Role.builder().name("ADMIN").status(1).build())
//						.build();
//				userRepository.save(user);
//				log.warn("account default has been created with username: admin,password: admin");
//			}
//		};
//	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return username ->  userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
	}

	@Bean
	public AuthenticationManager authenticationManager() {
		return new ProviderManager(authenticationProvider());
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService());
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}
}
