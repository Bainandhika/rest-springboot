// controller/AuthController.java
package com.baghaskara.kafka_redis_demo.controller;

import com.baghaskara.kafka_redis_demo.dto.LoginRequest;
import com.baghaskara.kafka_redis_demo.dto.UserResponse;
import com.baghaskara.kafka_redis_demo.security.JwtService;
import com.baghaskara.kafka_redis_demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;
    // In production, use AuthenticationManager to validate password against DB
    // private final AuthenticationManager authenticationManager;

    public AuthController(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        // 1. Authenticate user (Di production, pakai AuthenticationManager)
        // authenticationManager.authenticate(
        // new UsernamePasswordAuthenticationToken(request.getEmail(),
        // request.getPassword())
        // );

        // 2. Ambil user dari DB dan buat UserDetails untuk JWT
        UserResponse user = userService.getUserByEmail(request.getEmail());
        UserDetails userDetails = User.withUsername(user.email())
                .password("")
                .authorities(List.of())
                .build();

        // 3. Generate JWT
        String jwtToken = jwtService.generateToken(userDetails);

        // 4. TRIGGER BUSINESS LOGIC (Redis/Kafka)
        // Daripada user nge-hit endpoint kedua, kita eksekusi di sini.
        // BEST PRACTICE: Kirim event ke Kafka topic "user-login-events"
        // kafkaTemplate.send("user-login-events", request.getEmail());

        // Untuk sekarang, kita hit Redis langsung (Synchronous)
        userService.incrementLoginCount(request.getEmail());
        userService.addRecentActivity(request.getEmail(), "Logged in via API at " + java.time.LocalDateTime.now());

        // 5. Return Token
        Map<String, String> response = new HashMap<>();
        response.put("token", jwtToken);
        response.put("tokenType", "Bearer");

        return ResponseEntity.ok(response);
    }
}