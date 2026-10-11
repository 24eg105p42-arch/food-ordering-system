package com.foodorder.dto;

import java.math.BigDecimal;

public class AdminDtos {

    public record RevenueStatsResponse(
            long totalOrders,
            BigDecimal totalRevenue,
            long totalUsers,
            long totalRestaurants,
            long deliveredOrders,
            long pendingOrders
    ) {}

    public record UserSummaryResponse(Long id, String name, String email, String role, boolean active) {}
}
