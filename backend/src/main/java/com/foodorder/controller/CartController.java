package com.foodorder.controller;

import com.foodorder.dto.CartDtos.AddToCartRequest;
import com.foodorder.dto.CartDtos.CartResponse;
import com.foodorder.dto.CartDtos.UpdateCartItemRequest;
import com.foodorder.service.CartService;
import com.foodorder.service.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public CartResponse getCart() {
        return cartService.getCart(currentUserService.getCurrentUser());
    }

    @PostMapping("/items")
    public CartResponse addItem(@Valid @RequestBody AddToCartRequest request) {
        return cartService.addItem(currentUserService.getCurrentUser(), request);
    }

    @PutMapping("/items/{cartItemId}")
    public CartResponse updateItem(@PathVariable Long cartItemId, @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateItemQuantity(currentUserService.getCurrentUser(), cartItemId, request.quantity());
    }

    @DeleteMapping("/items/{cartItemId}")
    public CartResponse removeItem(@PathVariable Long cartItemId) {
        return cartService.removeItem(currentUserService.getCurrentUser(), cartItemId);
    }

    @DeleteMapping
    public CartResponse clearCart() {
        return cartService.clearCart(currentUserService.getCurrentUser());
    }
}
