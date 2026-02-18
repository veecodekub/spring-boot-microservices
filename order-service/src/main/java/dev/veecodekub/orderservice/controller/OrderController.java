package dev.veecodekub.orderservice.controller;

import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.veecodekub.orderservice.annotation.CurrentUserId;
import dev.veecodekub.orderservice.dto.CreateOrderRequest;
import dev.veecodekub.orderservice.dto.OrderResponse;
import dev.veecodekub.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public OrderResponse placeOrder(
            @CurrentUserId Long userId,
            @RequestBody CreateOrderRequest request) {
        return orderService.placeOrder(userId, request);
    }

    @GetMapping("/customer/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public OrderResponse getOrderByOrderIdAndCustomerId(
            @CurrentUserId Long userId,
            @PathVariable UUID orderId) {
        return orderService.getOrderByOrderIdAndCustomerId(orderId, userId);
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrderById(
            @PathVariable UUID orderId) {
        return orderService.getOrderById(orderId);
    }
}
