package com.baghaskara.kafka_redis_demo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// @Entity: Marks this class as a JPA Entity (mapped to a DB table).
// @Table: Explicitly names the table. If omitted, it defaults to class name (User).
@Entity
@Table(name = "users")
@Data // Lombok: Auto-generates getters, setters, toString, equals, hashCode
@NoArgsConstructor // JPA requires a no-args constructor
@AllArgsConstructor // Lombok: Generates constructor with all fields
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "full_name", nullable = false)
    private String fullName;
}