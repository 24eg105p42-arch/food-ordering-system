package com.foodorder.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class FoodItemDtos {

    public record FoodItemRequest(
            @NotBlank String name,
            String description,
            @NotNull @DecimalMin(value = "0.0", inclusive = true, message = "Price cannot be negative") BigDecimal price,
            String imageUrl,
            boolean veg,
            @NotNull Long categoryId
    ) {}

    public record FoodItemAvailabilityRequest(@NotNull Boolean available) {}

    public record FoodItemResponse(
            Long id, String name, String description, BigDecimal price, String imageUrl,
            boolean veg, boolean available, String categoryName,
            Long restaurantId, String restaurantName
    ) {}
}
