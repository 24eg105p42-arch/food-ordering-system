package com.foodorder.dto;

import com.foodorder.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    // Role defaults to CUSTOMER when not supplied, so a plain signup form works.
    public record RegisterRequest(
            @NotBlank(message = "Name is required") String name,
            @NotBlank @Email(message = "A valid email is required") String email,
            @NotBlank @Size(min = 6, message = "Password must be at least 6 characters") String password,
            String phone,
            Role role
    ) {}

    public record LoginRequest(
            @NotBlank @Email(message = "A valid email is required") String email,
            @NotBlank(message = "Password is required") String password
    ) {}

    public record AuthResponse(
            String token,
            Long userId,
            String name,
            String email,
            Role role
    ) {}
}
