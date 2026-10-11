package com.foodorder.dto;

import jakarta.validation.constraints.NotBlank;

public class AddressDtos {

    public record AddressRequest(
            String label,
            @NotBlank String line1,
            String line2,
            @NotBlank String city,
            @NotBlank String state,
            @NotBlank String pincode,
            boolean isDefault
    ) {}

    public record AddressResponse(
            Long id, String label, String line1, String line2,
            String city, String state, String pincode, boolean isDefault
    ) {}
}
