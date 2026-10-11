package com.foodorder.service;

import com.foodorder.dto.ReviewDtos.ReviewRequest;
import com.foodorder.dto.ReviewDtos.ReviewResponse;
import com.foodorder.entity.FoodItem;
import com.foodorder.entity.Order;
import com.foodorder.entity.Review;
import com.foodorder.entity.User;
import com.foodorder.enums.OrderStatus;
import com.foodorder.exception.BadRequestException;
import com.foodorder.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final FoodItemService foodItemService;
    private final OrderService orderService;

    public ReviewResponse addReview(User customer, ReviewRequest request) {
        Order order = orderService.findOrThrow(request.orderId());
        if (!order.getUser().getId().equals(customer.getId())) {
            throw new BadRequestException("You can only review your own orders");
        }
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new BadRequestException("You can only review items from a delivered order");
        }
        boolean alreadyReviewed = reviewRepository.existsByUserIdAndFoodItemIdAndOrderId(
                customer.getId(), request.foodItemId(), request.orderId());
        if (alreadyReviewed) {
            throw new BadRequestException("You already reviewed this item for this order");
        }

        FoodItem foodItem = foodItemService.findOrThrow(request.foodItemId());

        Review review = Review.builder()
                .user(customer)
                .foodItem(foodItem)
                .order(order)
                .rating(request.rating())
                .comment(request.comment())
                .build();

        return toResponse(reviewRepository.save(review));
    }

    public List<ReviewResponse> getForFoodItem(Long foodItemId) {
        return reviewRepository.findByFoodItemId(foodItemId).stream().map(this::toResponse).toList();
    }

    private ReviewResponse toResponse(Review r) {
        return new ReviewResponse(r.getId(), r.getFoodItem().getName(), r.getRating(),
                r.getComment(), r.getUser().getName(), r.getCreatedAt());
    }
}
