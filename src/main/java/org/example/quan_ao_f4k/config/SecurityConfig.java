package org.example.quan_ao_f4k.config;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.quan_ao_f4k.repository.authentication.UserRepository;
import org.example.quan_ao_f4k.service.authentication.JwtAuthenticationFilter;
import org.example.quan_ao_f4k.util.F4KConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
	private static final String[] PUBLIC_ENDPOINTS = {
			"/shop/**",
			"/common/**",
			"/auth/**",
			"/admin/**"
	};

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final AuthenticationProvider authenticationProvider;
	@Value("${api.prefix}")
	private String api;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers( PUBLIC_ENDPOINTS).permitAll()
						.requestMatchers( String.format("%s/admin/**", api)).permitAll()
						.requestMatchers( HttpMethod.GET,"/dashboard/**").hasRole("ADMIN")
						.requestMatchers( String.format("%s/shop/**", api)).hasRole("USER")
						.anyRequest().authenticated()
				)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.formLogin((form) -> form
						.permitAll()
						.successHandler(new AuthenticationSuccessHandler() {
							@Override
							public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
								UserDetails userDetails = (UserDetails) authentication.getPrincipal();
								String username = userDetails.getUsername();
								System.out.println("The user " + username + " has logged in.");
								boolean hasUserRole = authentication.getAuthorities().stream()
										.anyMatch(r -> r.getAuthority().equals(F4KConstants.ROLE_USER));
								boolean hasAdminRole = authentication.getAuthorities().stream()
										.anyMatch(r -> r.getAuthority().equals(F4KConstants.ROLE_ADMIN));
								if (hasUserRole) {
									response.sendRedirect("/api/v1/shop/home");
								} else if (hasAdminRole) {
									response.sendRedirect("/dashboard/admin/");
								}
							}
						})
				)
//                .logout(logout -> logout
//                        .logoutUrl("/auth/logout")
//                        .permitAll())
				.build();
	}

}