package com.foodorder.controller;

import com.foodorder.dto.ReviewDtos.ReviewRequest;
import com.foodorder.dto.ReviewDtos.ReviewResponse;
import com.foodorder.service.CurrentUserService;
import com.foodorder.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final CurrentUserService currentUserService;

    @PostMapping
    public ReviewResponse addReview(@Valid @RequestBody ReviewRequest request) {
        return reviewService.addReview(currentUserService.getCurrentUser(), request);
    }
}
