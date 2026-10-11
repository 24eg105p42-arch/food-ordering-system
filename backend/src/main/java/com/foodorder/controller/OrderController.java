package com.foodorder.controller;

import com.foodorder.dto.OrderDtos.OrderResponse;
import com.foodorder.dto.OrderDtos.PlaceOrderRequest;
import com.foodorder.service.CurrentUserService;
import com.foodorder.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CurrentUserService currentUserService;

    @PostMapping
    public OrderResponse placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        return orderService.placeOrder(currentUserService.getCurrentUser(), request);
    }

    @GetMapping
    public List<OrderResponse> getHistory() {
        return orderService.getCustomerHistory(currentUserService.getCurrentUser());
    }

    @GetMapping("/{id}")
    public OrderResponse trackOrder(@PathVariable Long id) {
        return orderService.getCustomerOrder(currentUserService.getCurrentUser(), id);
    }

    @PutMapping("/{id}/cancel")
    public OrderResponse cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(currentUserService.getCurrentUser(), id);
    }
}
