package com.baghaskara.kafka_redis_demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// 'record' adalah class immutable yang otomatis generate getter, constructor, dll.
// Sangat cocok untuk DTO karena datanya nggak akan berubah setelah di-instantiate.
public record CreateUserRequest(
        @NotBlank(message = "Email is required") @Email(message = "Email format is invalid") String email,

        @NotBlank(message = "Full name is required") String fullName) {
}