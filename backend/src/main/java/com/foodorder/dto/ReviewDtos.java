package com.foodorder.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ReviewDtos {

    public record ReviewRequest(
            @NotNull Long foodItemId,
            @NotNull Long orderId,
            @Min(1) @Max(5) int rating,
            String comment
    ) {}

    public record ReviewResponse(
            Long id, String foodItemName, int rating, String comment,
            String customerName, LocalDateTime createdAt
    ) {}
}
