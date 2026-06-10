package com.baghaskara.kafka_redis_demo.service;

import com.baghaskara.kafka_redis_demo.domain.User;
import com.baghaskara.kafka_redis_demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// @Service: Stereotype annotation yang menandakan class ini berisi Business Logic.
// Secara otomatis terdeteksi oleh Component Scan, sama seperti @Repository atau @Controller.
@Service
public class UserService {

    private final UserRepository userRepository;

    // Constructor Injection (Best Practice)
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // @Transactional: Ensures that if any runtime exception occurs within this
    // method,
    // the database transaction will be rolled back automatically.
    // Crucial for maintaining data integrity.
    @Transactional
    public User createUser(User user) {
        // Di masa depan, di sini kita bisa tambah logic:
        // 1. Cek duplikasi email
        // 2. Hash password
        // 3. Simpan ke DB
        // 4. Kirim event ke Kafka
        // 5. Simpan ke Redis Cache
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        // Note: Di production, lebih baik lempar custom exception seperti
        // UserNotFoundException
        // yang di-handle oleh @ControllerAdvice untuk return 404 yang rapi.
    }
}