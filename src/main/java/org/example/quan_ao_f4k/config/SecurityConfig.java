package org.example.quan_ao_f4k.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.quan_ao_f4k.service.authentication.CustomUserService;
import org.example.quan_ao_f4k.util.F4KConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;


@Configuration
public class SecurityConfig {

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http, final AuthenticationConfiguration authenticationConfiguration) throws Exception {
        http.authenticationProvider(authenticationProvider());
        http.csrf(csrf -> csrf.disable())
                .authorizeRequests(auth -> {
                    auth.anyRequest().permitAll();
                });

        return http.build();
    }

// @AuthenticationPrincipal UserDetails userDetails for get username in controller
//    @Bean
//    protected SecurityFilterChain securityFilterChain(HttpSecurity http, final AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        http.authenticationProvider(authenticationProvider());
//        http.csrf(csrf -> csrf.disable())
//                .authorizeRequests(auth -> {
//                    auth.requestMatchers("/auth/**",
//                            "/shop/dist/**",
//                            "/shop/script/**",
//                            "/common/**").permitAll();
//                    auth.anyRequest().authenticated();
//                })
//                .formLogin((form) -> form
//                        .permitAll()
//                        .successHandler(new AuthenticationSuccessHandler() {
//                            @Override
//                            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//                                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//                                String username = userDetails.getUsername();
//                                System.out.println("The user " + username + " has logged in.");
//                                boolean hasUserRole = authentication.getAuthorities().stream()
//                                        .anyMatch(r -> r.getAuthority().equals(F4KConstants.ROLE_USER));
//                                boolean hasAdminRole = authentication.getAuthorities().stream()
//                                        .anyMatch(r -> r.getAuthority().equals(F4KConstants.ROLE_ADMIN));
//                                if (hasUserRole) {
//                                    response.sendRedirect("/shop/home");
//                                } else if (hasAdminRole) {
//                                    response.sendRedirect("/admin/admin/products/list");
//                                }
//                            }
//                        })
//                )
//                .logout(logout -> {
//                    logout.logoutUrl("/logout");
//                    logout.logoutSuccessUrl("/");
//                });
//
//        return http.build();
//    }


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomUserService userDetailsService() {
        return new CustomUserService();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}