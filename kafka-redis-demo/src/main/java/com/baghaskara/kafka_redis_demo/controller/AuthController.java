package com.baghaskara.kafka_redis_demo.controller;

import com.baghaskara.kafka_redis_demo.security.JwtService;
import com.baghaskara.kafka_redis_demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String email) {
        // 1. Cek apakah user ada di DB (Simulasi validasi user)
        // Di production, lu akan validasi password pakai passwordEncoder.matches(rawPassword, encodedPassword)
        userService.getUserByEmail(email); 

        // 2. Buat objek UserDetails (Interface standar Spring Security)
        // Kita pakai implementasi bawaan Spring untuk simplifikasi
        UserDetails userDetails = User.builder()
                .username(email)
                .password("") // Password tidak dipakai untuk validasi di flow JWT generation ini
                .authorities("ROLE_USER") // Role/Permission
                .build();

        // 3. Generate JWT Token
        String token = jwtService.generateToken(userDetails);

        // 4. Return token ke client
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("tokenType", "Bearer");
        
        return ResponseEntity.ok(response);
    }
}