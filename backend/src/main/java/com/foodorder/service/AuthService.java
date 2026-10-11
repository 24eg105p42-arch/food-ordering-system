package com.foodorder.service;

import com.foodorder.dto.AuthDtos.AuthResponse;
import com.foodorder.dto.AuthDtos.LoginRequest;
import com.foodorder.dto.AuthDtos.RegisterRequest;
import com.foodorder.entity.User;
import com.foodorder.enums.Role;
import com.foodorder.exception.BadRequestException;
import com.foodorder.repository.UserRepository;
import com.foodorder.security.CustomUserDetailsService;
import com.foodorder.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("An account with this email already exists");
        }

        // Public signup can only create CUSTOMER or RESTAURANT_ADMIN accounts.
        // SUPER_ADMIN accounts should be created directly in the database.
        Role role = request.role() == null ? Role.CUSTOMER : request.role();
        if (role == Role.SUPER_ADMIN) {
            throw new BadRequestException("Super admin accounts cannot be self-registered");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .role(role)
                .active(true)
                .build();

        User saved = userRepository.save(user);
        String token = generateTokenFor(saved);

        return new AuthResponse(token, saved.getId(), saved.getName(), saved.getEmail(), saved.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        String token = generateTokenFor(user);
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    private String generateTokenFor(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        return jwtUtil.generateToken(userDetails, Map.of("role", user.getRole().name(), "userId", user.getId()));
    }
}
