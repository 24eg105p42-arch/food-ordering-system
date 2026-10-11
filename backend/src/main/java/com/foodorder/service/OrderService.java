package com.foodorder.service;

import com.foodorder.dto.OrderDtos.OrderItemResponse;
import com.foodorder.dto.OrderDtos.OrderResponse;
import com.foodorder.dto.OrderDtos.PlaceOrderRequest;
import com.foodorder.entity.*;
import com.foodorder.enums.OrderStatus;
import com.foodorder.enums.PaymentStatus;
import com.foodorder.exception.BadRequestException;
import com.foodorder.exception.ResourceNotFoundException;
import com.foodorder.repository.AddressRepository;
import com.foodorder.repository.OrderRepository;
import com.foodorder.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final AddressRepository addressRepository;
    private final CartService cartService;

    // Legal next steps for each order status, enforced so the UI can't skip a step
    // (e.g. jumping straight from PLACED to DELIVERED).
    private static final Set<OrderStatus> CANCELLABLE_BY_CUSTOMER = Set.of(OrderStatus.PLACED);

    @Transactional
    public OrderResponse placeOrder(User customer, PlaceOrderRequest request) {
        Cart cart = cartService.getOrCreateCart(customer);
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Your cart is empty");
        }

        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        if (!address.getUser().getId().equals(customer.getId())) {
            throw new BadRequestException("This address does not belong to you");
        }

        Restaurant restaurant = cart.getRestaurant();
        String addressSnapshot = String.join(", ",
                address.getLine1(),
                address.getLine2() == null ? "" : address.getLine2(),
                address.getCity(), address.getState(), address.getPincode());

        BigDecimal total = cart.getItems().stream()
                .map(ci -> ci.getFoodItem().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .user(customer)
                .restaurant(restaurant)
                .deliveryAddress(addressSnapshot)
                .totalAmount(total)
                .status(OrderStatus.PLACED)
                .build();

        cart.getItems().forEach(ci -> order.getItems().add(
                OrderItem.builder()
                        .order(order)
                        .foodItem(ci.getFoodItem())
                        .itemName(ci.getFoodItem().getName())
                        .unitPrice(ci.getFoodItem().getPrice())
                        .quantity(ci.getQuantity())
                        .build()));

        Order saved = orderRepository.save(order);

        Payment payment = Payment.builder()
                .order(saved)
                .method(request.paymentMethod())
                .amount(total)
                // Cash on delivery stays PENDING until the courier collects it;
                // everything else is treated as paid immediately in this version.
                .status(request.paymentMethod().name().equals("COD") ? PaymentStatus.PENDING : PaymentStatus.SUCCESS)
                .build();
        paymentRepository.save(payment);

        cartService.clearCart(customer);
        return toResponse(saved);
    }

    public List<OrderResponse> getCustomerHistory(User customer) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(customer.getId())
                .stream().map(this::toResponse).toList();
    }

    public OrderResponse getCustomerOrder(User customer, Long orderId) {
        Order order = findOrThrow(orderId);
        if (!order.getUser().getId().equals(customer.getId())) {
            throw new ResourceNotFoundException("Order not found");
        }
        return toResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(User customer, Long orderId) {
        Order order = findOrThrow(orderId);
        if (!order.getUser().getId().equals(customer.getId())) {
            throw new ResourceNotFoundException("Order not found");
        }
        if (!CANCELLABLE_BY_CUSTOMER.contains(order.getStatus())) {
            throw new BadRequestException("This order can no longer be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        return toResponse(orderRepository.save(order));
    }

    // ---- Restaurant admin side ----
    public List<OrderResponse> getIncomingOrders(Restaurant restaurant) {
        return orderRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurant.getId())
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public OrderResponse updateStatus(Restaurant restaurant, Long orderId, OrderStatus newStatus) {
        Order order = findOrThrow(orderId);
        if (!order.getRestaurant().getId().equals(restaurant.getId())) {
            throw new ResourceNotFoundException("Order not found for your restaurant");
        }
        validateTransition(order.getStatus(), newStatus);
        order.setStatus(newStatus);
        return toResponse(orderRepository.save(order));
    }

    private void validateTransition(OrderStatus current, OrderStatus next) {
        boolean valid = switch (current) {
            case PLACED -> next == OrderStatus.ACCEPTED || next == OrderStatus.REJECTED;
            case ACCEPTED -> next == OrderStatus.PREPARING;
            case PREPARING -> next == OrderStatus.OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY -> next == OrderStatus.DELIVERED;
            default -> false;
        };
        if (!valid) {
            throw new BadRequestException("Cannot move order from " + current + " to " + next);
        }
    }

    // ---- Super admin side ----
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(this::toResponse).toList();
    }

    public Order findOrThrow(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(i -> new OrderItemResponse(i.getItemName(), i.getUnitPrice(), i.getQuantity()))
                .toList();
        return new OrderResponse(order.getId(), order.getRestaurant().getName(), order.getDeliveryAddress(),
                order.getTotalAmount(), order.getStatus(), items, order.getCreatedAt());
    }
}
