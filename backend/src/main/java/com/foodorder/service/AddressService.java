package com.foodorder.service;

import com.foodorder.dto.AddressDtos.AddressRequest;
import com.foodorder.dto.AddressDtos.AddressResponse;
import com.foodorder.entity.Address;
import com.foodorder.entity.User;
import com.foodorder.exception.ResourceNotFoundException;
import com.foodorder.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public List<AddressResponse> getAll(User customer) {
        return addressRepository.findByUserId(customer.getId()).stream().map(this::toResponse).toList();
    }

    public AddressResponse add(User customer, AddressRequest request) {
        Address address = Address.builder()
                .user(customer)
                .label(request.label())
                .line1(request.line1())
                .line2(request.line2())
                .city(request.city())
                .state(request.state())
                .pincode(request.pincode())
                .isDefault(request.isDefault())
                .build();
        return toResponse(addressRepository.save(address));
    }

    public void delete(User customer, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        if (!address.getUser().getId().equals(customer.getId())) {
            throw new ResourceNotFoundException("Address not found");
        }
        addressRepository.delete(address);
    }

    private AddressResponse toResponse(Address a) {
        return new AddressResponse(a.getId(), a.getLabel(), a.getLine1(), a.getLine2(),
                a.getCity(), a.getState(), a.getPincode(), a.isDefault());
    }
}
