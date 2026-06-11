package com.baghaskara.kafka_redis_demo.dto;

// Kita cuma expose field yang aman dan dibutuhkan oleh frontend/client.
// Field internal DB atau password (kalau ada) nggak akan pernah bocor.
public record UserResponse(
        Long id,
        String email,
        String fullName) {
}