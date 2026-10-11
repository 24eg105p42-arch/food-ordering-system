package com.foodorder.controller;

import com.foodorder.dto.AddressDtos.AddressRequest;
import com.foodorder.dto.AddressDtos.AddressResponse;
import com.foodorder.dto.CommonDtos.MessageResponse;
import com.foodorder.entity.User;
import com.foodorder.service.AddressService;
import com.foodorder.service.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public List<AddressResponse> getMyAddresses() {
        return addressService.getAll(currentUserService.getCurrentUser());
    }

    @PostMapping
    public AddressResponse addAddress(@Valid @RequestBody AddressRequest request) {
        return addressService.add(currentUserService.getCurrentUser(), request);
    }

    @DeleteMapping("/{id}")
    public MessageResponse deleteAddress(@PathVariable Long id) {
        addressService.delete(currentUserService.getCurrentUser(), id);
        return new MessageResponse("Address deleted");
    }
}
