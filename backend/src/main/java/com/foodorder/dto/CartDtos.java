package com.foodorder.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class CartDtos {

    public record AddToCartRequest(@NotNull Long foodItemId, @Min(1) int quantity) {}

    public record UpdateCartItemRequest(@Min(1) int quantity) {}

    public record CartItemResponse(
            Long cartItemId, Long foodItemId, String name,
            BigDecimal price, int quantity, BigDecimal subtotal
    ) {}

    public record CartResponse(
            Long cartId, Long restaurantId, String restaurantName,
            List<CartItemResponse> items, BigDecimal total
    ) {}
}
