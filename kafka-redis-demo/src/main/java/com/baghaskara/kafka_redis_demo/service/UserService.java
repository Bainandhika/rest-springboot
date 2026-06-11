package com.baghaskara.kafka_redis_demo.service;

import com.baghaskara.kafka_redis_demo.domain.User;
import com.baghaskara.kafka_redis_demo.dto.CreateUserRequest;
import com.baghaskara.kafka_redis_demo.dto.UserResponse;
import com.baghaskara.kafka_redis_demo.exception.ResourceNotFoundException;
import com.baghaskara.kafka_redis_demo.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public UserService(UserRepository userRepository, RedisTemplate<String, Object> redisTemplate,
            KafkaTemplate<String, String> kafkaTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        // 1. Map DTO to Entity
        User user = new User();
        user.setEmail(request.email());
        user.setFullName(request.fullName());

        User savedUser = userRepository.save(user);

        // KAFKA PRODUCER LOGIC
        // Kirim event asinkron ke topic 'user-events'.
        // Parameter: (topicName, key, value). Key digunakan untuk partitioning.
        String eventMessage = "New user registered: " + savedUser.getFullName() + " (" + savedUser.getEmail() + ")";
        kafkaTemplate.send("user-events", savedUser.getEmail(), eventMessage);

        return mapToUserResponse(savedUser);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapToUserResponse(user);
    }

    // ==========================================
    // REDIS INTEGRATION STARTS HERE
    // ==========================================

    /**
     * 1. SOLVING CONCURRENCY: Atomic Increment
     * Di Golang, kalau ini in-memory, lu pakai sync.Mutex.
     * Tapi kalau aplikasinya di-scale (multiple pods/instances), sync.Mutex gagal.
     * Redis opsForValue().increment() dijamin ATOMIC oleh Redis server,
     * sehingga aman dari Race Condition meski diakses 1000 request/detik secara
     * bersamaan.
     */
    public Long incrementLoginCount(String email) {
        String key = "user:" + email + ":login_count";
        // increment(key, delta)
        Long count = redisTemplate.opsForValue().increment(key, 1);

        // Opsional: Set TTL (Time To Live) agar key tidak selamanya di memory
        // Equivalent to EXPIRE command in Redis CLI
        redisTemplate.expire(key, 24, TimeUnit.HOURS);

        return count;
    }

    /**
     * 2. LIST DATA STRUCTURE: Storing Collections
     * Menyimpan riwayat aktivitas. Mirip slice di Go, tapi dikelola oleh Redis.
     * rightPush = LPUSH/RPUSH (menambah ke ujung list).
     * range = LRANGE (mengambil sebagian atau seluruh elemen list).
     */
    public void addRecentActivity(String email, String activity) {
        String key = "user:" + email + ":activities";
        // Push activity to the right of the list
        redisTemplate.opsForList().rightPush(key, activity);

        // Trim the list to keep only the last 10 activities (Equivalent to LTRIM)
        // Ini penting untuk mencegah memory leak di Redis!
        redisTemplate.opsForList().trim(key, -10, -1);
    }

    public List<Object> getRecentActivities(String email) {
        String key = "user:" + email + ":activities";
        // Get all elements from index 0 to -1 (all elements)
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 3. HASH DATA STRUCTURE: Storing Objects
     * Hash di Redis mirip dengan Map[String]Interface{} di Go atau struct.
     * Sangat efisien untuk menyimpan objek dengan banyak field yang mungkin
     * di-update sebagian.
     */
    public void cacheUserProfile(String email, UserResponse user) {
        String key = "user:" + email + ":profile";

        // putAll equivalent to HMSET
        redisTemplate.opsForHash().putAll(key, Map.of(
                "id", user.id(),
                "fullName", user.fullName(),
                "email", user.email()));

        redisTemplate.expire(key, 1, TimeUnit.HOURS);
    }

    public Map<Object, Object> getUserProfileCache(String email) {
        String key = "user:" + email + ":profile";
        // entries equivalent to HGETALL
        return redisTemplate.opsForHash().entries(key);
    }

    private UserResponse mapToUserResponse(User user) {
        // Mapping manual dari Entity ke DTO. Di production, bisa pakai library seperti
        // MapStruct.
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName());
    }
}