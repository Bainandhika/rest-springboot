package com.baghaskara.kafka_redis_demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. Password Encoder: Untuk hash password (equivalent to bcrypt di Go)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Security Filter Chain: Menggantikan konsep "Middleware" di Golang
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // DISABLE CSRF: Karena kita pakai JWT (stateless), CSRF protection tidak
                // diperlukan
                // dan justru akan memblokir request POST/PUT/PATCH dari Postman/cURL.
                .csrf(AbstractHttpConfigurer::disable)

                // STATELESS SESSION: Beri tahu Spring JANGAN membuat HttpSession.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // AUTHORIZATION RULES
                .authorizeHttpRequests(auth -> auth
                        // Endpoint ini PUBLIC (tidak butuh token)
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        // Semua endpoint lain HARUS diautentikasi
                        .anyRequest().authenticated());

        // Build dan return SecurityFilterChain
        return http.build();
    }
}