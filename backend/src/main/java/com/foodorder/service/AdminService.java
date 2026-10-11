package com.foodorder.service;

import com.foodorder.dto.AdminDtos.RevenueStatsResponse;
import com.foodorder.dto.AdminDtos.UserSummaryResponse;
import com.foodorder.entity.User;
import com.foodorder.enums.OrderStatus;
import com.foodorder.exception.ResourceNotFoundException;
import com.foodorder.repository.OrderRepository;
import com.foodorder.repository.RestaurantRepository;
import com.foodorder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;

    public List<UserSummaryResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserSummaryResponse(u.getId(), u.getName(), u.getEmail(), u.getRole().name(), u.isActive()))
                .toList();
    }

    public UserSummaryResponse setUserActive(Long userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(active);
        User saved = userRepository.save(user);
        return new UserSummaryResponse(saved.getId(), saved.getName(), saved.getEmail(),
                saved.getRole().name(), saved.isActive());
    }

    public RevenueStatsResponse getStats() {
        BigDecimal totalRevenue = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(o -> o.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RevenueStatsResponse(
                orderRepository.count(),
                totalRevenue,
                userRepository.count(),
                restaurantRepository.count(),
                orderRepository.countByStatus(OrderStatus.DELIVERED),
                orderRepository.countByStatus(OrderStatus.PLACED)
        );
    }
}
