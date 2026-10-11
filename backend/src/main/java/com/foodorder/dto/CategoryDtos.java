package com.foodorder.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoryDtos {

    public record CategoryRequest(@NotBlank String name, String imageUrl) {}

    public record CategoryResponse(Long id, String name, String imageUrl) {}
}
