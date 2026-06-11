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

    // 1. Password Encoder: Wajib ada untuk hash password (equivalent to bcrypt in
    // Go)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Security Filter Chain: Menggantikan konsep "Middleware" di Golang
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // DISABLE CSRF: Karena kita pakai JWT (stateless), CSRF protection tidak diperlukan 
            // dan justru akan memblokir request POST/PUT/PATCH dari Postman/cURL.
            .csrf(AbstractHttpConfigurer::disable)
            
            // STATELESS SESSION: Beri tahu Spring JANGAN membuat HttpSession (JSESSIONID).
            // Setiap request harus membawa JWT di header "Authorization: Bearer <token>".
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // AUTHORIZATION RULES
            .authorizeHttpRequests(auth -> auth
                // Endpoint ini PUBLIC (tidak butuh token)
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/actuator/**").permitAll() // Opsional: untuk health check
                
                // Semua endpoint lain HARUS diautentikasi
                .anyRequest().authenticated()
            );
            
            // NOTE: Di sini kita belum menambahkan JWT Filter kustom. 
            // Untuk project belajar ini, kita akan generate token di /auth/login, 
            // dan client menyimpannya. (Implementasi JWT Filter penuh bisa jadi Phase 5 kalau lu mau).

        return http.build();
 objective
    }
}