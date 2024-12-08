package org.example.quan_ao_f4k.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.quan_ao_f4k.util.F4KConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {
            "/static/**",
            "/common/**",
            "/verify_account/**",
            "/admin/plugins/**",
            "/vnPay/**"
    };

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private RememberMeServices rememberMeServices;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                                .requestMatchers("/authentication/**").permitAll()
//                                .requestMatchers("/**").permitAll()
                                .requestMatchers("/admin/**").hasAuthority(F4KConstants.ROLE_ADMIN)
                                .requestMatchers("/shop/**").hasAnyAuthority(F4KConstants.ROLE_USER,F4KConstants.ROLE_ADMIN)
                                .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/authentication/login").permitAll()
                        .loginProcessingUrl("/authentication/login")
                        .failureUrl("/authentication/login?error=true")
                        .defaultSuccessUrl("/shop/home", true)
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
                                boolean hasStaffRole = authentication.getAuthorities().stream()
                                        .anyMatch(r -> r.getAuthority().equals(F4KConstants.ROLE_STAFF));
                                if (hasUserRole){
                                    response.sendRedirect("/shop/home");
                                }else if (hasAdminRole || hasStaffRole){
                                    response.sendRedirect("/admin/products");
                                } else {
                                    response.sendRedirect("/error/401");
                                }
                            }
                        })
                )
                .authenticationProvider(authenticationProvider)
                .logout(logout -> logout
                        .logoutUrl("/authentication/logout")
                        .invalidateHttpSession(true)
                        .invalidateHttpSession(true)
                        .logoutSuccessUrl("/authentication/login")
                )
                .rememberMe((remember) -> remember
                        .rememberMeServices(rememberMeServices)
                );
        ;
        return httpSecurity.build();
    }
}
