package com.baghaskara.kafka_redis_demo.controller;

import com.baghaskara.kafka_redis_demo.domain.User;
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

    // Controller sekarang cuma bergantung pada Service, TIDAK LANGSUNG ke
    // Repository.
    // Ini membuat Controller tetap "bersih" dari detail implementasi database.
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User savedUser = userService.createUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
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
        User user = userService.getUserByEmail(email);
        userService.cacheUserProfile(email, user);
        return ResponseEntity.ok("Profile cached in Redis Hash");
    }

    @GetMapping("/{email}/cache-profile")
    public ResponseEntity<Map<Object, Object>> getCachedProfile(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserProfileCache(email));
    }
}