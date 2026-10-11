package com.foodorder.service;

import com.foodorder.dto.RestaurantDtos.RestaurantRequest;
import com.foodorder.dto.RestaurantDtos.RestaurantResponse;
import com.foodorder.entity.Restaurant;
import com.foodorder.entity.User;
import com.foodorder.exception.BadRequestException;
import com.foodorder.exception.ResourceNotFoundException;
import com.foodorder.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public List<RestaurantResponse> getApprovedRestaurants() {
        return restaurantRepository.findByApprovedTrue().stream().map(this::toResponse).toList();
    }

    public RestaurantResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public Restaurant findOrThrow(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
    }

    // Each restaurant admin owns exactly one restaurant in this version of the app.
    public RestaurantResponse createForOwner(User owner, RestaurantRequest request) {
        if (restaurantRepository.findFirstByOwnerId(owner.getId()).isPresent()) {
            throw new BadRequestException("You already have a restaurant profile. Use update instead.");
        }
        Restaurant restaurant = Restaurant.builder()
                .owner(owner)
                .name(request.name())
                .description(request.description())
                .address(request.address())
                .city(request.city())
                .phone(request.phone())
                .imageUrl(request.imageUrl())
                .openNow(true)
                .approved(false) // a Super Admin must approve before it is publicly visible
                .build();
        return toResponse(restaurantRepository.save(restaurant));
    }

    public RestaurantResponse updateOwnRestaurant(User owner, RestaurantRequest request) {
        Restaurant restaurant = getOwnRestaurantOrThrow(owner);
        restaurant.setName(request.name());
        restaurant.setDescription(request.description());
        restaurant.setAddress(request.address());
        restaurant.setCity(request.city());
        restaurant.setPhone(request.phone());
        restaurant.setImageUrl(request.imageUrl());
        return toResponse(restaurantRepository.save(restaurant));
    }

    public RestaurantResponse setOpenStatus(User owner, boolean open) {
        Restaurant restaurant = getOwnRestaurantOrThrow(owner);
        restaurant.setOpenNow(open);
        return toResponse(restaurantRepository.save(restaurant));
    }

    public RestaurantResponse getOwnRestaurantResponse(User owner) {
        return toResponse(getOwnRestaurantOrThrow(owner));
    }

    public Restaurant getOwnRestaurantOrThrow(User owner) {
        return restaurantRepository.findFirstByOwnerId(owner.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "You have not created a restaurant profile yet"));
    }

    // ---- Super Admin ----
    public List<RestaurantResponse> getAllForAdmin() {
        return restaurantRepository.findAll().stream().map(this::toResponse).toList();
    }

    public RestaurantResponse approve(Long id) {
        Restaurant restaurant = findOrThrow(id);
        restaurant.setApproved(true);
        return toResponse(restaurantRepository.save(restaurant));
    }

    private RestaurantResponse toResponse(Restaurant r) {
        return new RestaurantResponse(r.getId(), r.getName(), r.getDescription(), r.getAddress(),
                r.getCity(), r.getPhone(), r.getImageUrl(), r.isOpenNow(), r.isApproved());
    }
}
