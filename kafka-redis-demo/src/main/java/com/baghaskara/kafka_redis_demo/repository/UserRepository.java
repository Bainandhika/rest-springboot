package com.baghaskara.kafka_redis_demo.repository;

import com.baghaskara.kafka_redis_demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// @Repository: Stereotype annotation indicating this is a Data Access Object (DAO).
// JpaRepository<EntityType, PrimaryKeyType>: Provides built-in CRUD methods.
// This is the Java equivalent of GORM's auto-generated repository methods, 
// but achieved via runtime proxy generation.
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Spring Data JPA magic: Method name translates to SQL automatically.
    // Equivalent to: db.Where("email = ?", email).First(&user) in GORM.
    Optional<User> findByEmail(String email);
}