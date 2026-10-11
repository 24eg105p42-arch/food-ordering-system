package com.foodorder.dto;

import jakarta.validation.constraints.NotBlank;

public class RestaurantDtos {

    public record RestaurantRequest(
            @NotBlank String name,
            String description,
            @NotBlank String address,
            @NotBlank String city,
            String phone,
            String imageUrl
    ) {}

    public record RestaurantResponse(
            Long id, String name, String description, String address, String city,
            String phone, String imageUrl, boolean openNow, boolean approved
    ) {}
}
