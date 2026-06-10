package com.baghaskara.kafka_redis_demo.controller;

import com.baghaskara.kafka_redis_demo.domain.User;
import com.baghaskara.kafka_redis_demo.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController: Combines @Controller and @ResponseBody. 
// Ensures return objects are serialized to JSON (like c.JSON() in Gin).
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;

    // BEST PRACTICE: Constructor Injection.
    // Avoid @Autowired on fields. This makes the class immutable, 
    // thread-safe, and easy to unit test (just like passing dependencies in Go struct init).
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        // @Valid triggers Jakarta Validation (e.g., @NotNull, @Email) defined in the domain.
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}