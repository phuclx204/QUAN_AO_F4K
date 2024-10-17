package org.example.quan_ao_f4k.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableJpaAuditing
@Configuration
public class ShopF4KWebMvcConfigurer implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("http://localhost:8080")
				.allowedMethods("GET", "POST", "PUT", "PATCH");
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
