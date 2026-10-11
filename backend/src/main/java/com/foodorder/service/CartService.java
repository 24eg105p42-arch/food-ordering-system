package com.foodorder.service;

import com.foodorder.dto.CartDtos.AddToCartRequest;
import com.foodorder.dto.CartDtos.CartItemResponse;
import com.foodorder.dto.CartDtos.CartResponse;
import com.foodorder.entity.Cart;
import com.foodorder.entity.CartItem;
import com.foodorder.entity.FoodItem;
import com.foodorder.entity.User;
import com.foodorder.exception.BadRequestException;
import com.foodorder.exception.ResourceNotFoundException;
import com.foodorder.repository.CartItemRepository;
import com.foodorder.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * A cart belongs to one restaurant at a time, matching the real-world rule
 * that you cannot checkout a single order split across two kitchens.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final FoodItemService foodItemService;

    @Transactional
    public CartResponse addItem(User customer, AddToCartRequest request) {
        Cart cart = getOrCreateCart(customer);
        FoodItem foodItem = foodItemService.findOrThrow(request.foodItemId());

        if (!foodItem.isAvailable()) {
            throw new BadRequestException("This item is currently unavailable");
        }

        if (cart.getRestaurant() != null && !cart.getRestaurant().getId().equals(foodItem.getRestaurant().getId())) {
            throw new BadRequestException(
                    "Your cart has items from another restaurant. Clear the cart first or finish that order.");
        }
        cart.setRestaurant(foodItem.getRestaurant());

        CartItem existing = cartItemRepository.findByCartIdAndFoodItemId(cart.getId(), foodItem.getId()).orElse(null);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.quantity());
            cartItemRepository.save(existing);
        } else {
            CartItem item = CartItem.builder().cart(cart).foodItem(foodItem).quantity(request.quantity()).build();
            cart.getItems().add(item);
            cartItemRepository.save(item);
        }

        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse updateItemQuantity(User customer, Long cartItemId, int quantity) {
        Cart cart = getOrCreateCart(customer);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(User customer, Long cartItemId) {
        Cart cart = getOrCreateCart(customer);
        cart.getItems().removeIf(i -> i.getId().equals(cartItemId));
        cartItemRepository.deleteById(cartItemId);

        if (cart.getItems().isEmpty()) {
            cart.setRestaurant(null);
        }
        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse clearCart(User customer) {
        Cart cart = getOrCreateCart(customer);
        cart.getItems().clear();
        cart.setRestaurant(null);
        cartRepository.save(cart);
        return toResponse(cart);
    }

    public CartResponse getCart(User customer) {
        return toResponse(getOrCreateCart(customer));
    }

    public Cart getOrCreateCart(User customer) {
        return cartRepository.findByUserId(customer.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().user(customer).build()));
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(i -> new CartItemResponse(
                        i.getId(), i.getFoodItem().getId(), i.getFoodItem().getName(),
                        i.getFoodItem().getPrice(), i.getQuantity(),
                        i.getFoodItem().getPrice().multiply(BigDecimal.valueOf(i.getQuantity()))))
                .toList();

        BigDecimal total = itemResponses.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(
                cart.getId(),
                cart.getRestaurant() != null ? cart.getRestaurant().getId() : null,
                cart.getRestaurant() != null ? cart.getRestaurant().getName() : null,
                itemResponses,
                total
        );
    }
}
