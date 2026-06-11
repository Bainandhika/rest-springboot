package com.baghaskara.kafka_redis_demo.controller;

import com.baghaskara.kafka_redis_demo.dto.CreateUserRequest;
import com.baghaskara.kafka_redis_demo.dto.UserResponse;
import com.baghaskara.kafka_redis_demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse savedUser = userService.createUser(request);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PostMapping("/{email}/login")
    public ResponseEntity<String> simulateLogin(@PathVariable String email) {
        // 1. Increment counter (Atomic & Concurrency Safe)
        Long count = userService.incrementLoginCount(email);

        // 2. Add to activity list
        userService.addRecentActivity(email, "Logged in at " + java.time.LocalDateTime.now());

        return ResponseEntity.ok("Login successful. Total logins: " + count);
    }

    @GetMapping("/{email}/activities")
    public ResponseEntity<List<Object>> getActivities(@PathVariable String email) {
        return ResponseEntity.ok(userService.getRecentActivities(email));
    }

    @PostMapping("/{email}/cache-profile")
    public ResponseEntity<String> cacheProfile(@PathVariable String email) {
        // Ambil dari DB dulu, baru simpan ke Redis Hash
        UserResponse user = userService.getUserByEmail(email);
        userService.cacheUserProfile(email, user);
        return ResponseEntity.ok("Profile cached in Redis Hash");
    }

    @GetMapping("/{email}/cache-profile")
    public ResponseEntity<Map<Object, Object>> getCachedProfile(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserProfileCache(email));
    }
}