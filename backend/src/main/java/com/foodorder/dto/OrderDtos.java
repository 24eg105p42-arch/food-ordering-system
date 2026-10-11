package com.foodorder.dto;

import com.foodorder.enums.OrderStatus;
import com.foodorder.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDtos {

    public record PlaceOrderRequest(@NotNull Long addressId, @NotNull PaymentMethod paymentMethod) {}

    public record OrderStatusUpdateRequest(@NotNull OrderStatus status) {}

    public record OrderItemResponse(String itemName, BigDecimal unitPrice, int quantity) {}

    public record OrderResponse(
            Long id, String restaurantName, String deliveryAddress, BigDecimal totalAmount,
            OrderStatus status, List<OrderItemResponse> items, LocalDateTime createdAt
    ) {}
}
