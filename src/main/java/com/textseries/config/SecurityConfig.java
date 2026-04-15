package com.textseries.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

	private final JwtFilter jwtFilter;

	public SecurityConfig(JwtFilter jwtFilter) {
		this.jwtFilter = jwtFilter;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.cors(cors -> cors.configurationSource(request -> {
			var corsConfig = new org.springframework.web.cors.CorsConfiguration();
			corsConfig.setAllowedOrigins(java.util.List.of("http://localhost:5173"));
			corsConfig.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
			corsConfig.setAllowedHeaders(java.util.List.of("*"));
			return corsConfig;
		})).csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth

				// 🔓 PUBLIC
				.requestMatchers("/api/auth/**").permitAll()
				.requestMatchers("/api/result/leaderboard").permitAll()
 
				// 🔐 USER
				.requestMatchers(HttpMethod.GET, "/api/tests/active").authenticated()
				.requestMatchers("/api/questions/test/**").authenticated()
				.requestMatchers("/api/result/**").authenticated()
				
				// 🔐 ADMIN
			    .requestMatchers(HttpMethod.POST, "/api/tests").hasRole("ADMIN") 
			    .requestMatchers(HttpMethod.GET, "/api/tests").hasRole("ADMIN") 
				.requestMatchers("/api/questions/**").hasRole("ADMIN")
				.requestMatchers("/api/result/admin/**").hasRole("ADMIN")

				.anyRequest().authenticated()).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.formLogin(form -> form.disable()).httpBasic(basic -> basic.disable());

		return http.build();
	}
}