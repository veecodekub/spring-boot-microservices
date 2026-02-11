package dev.veecodekub.orderservice.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.veecodekub.orderservice.dto.CreateOrderRequest;
import dev.veecodekub.orderservice.dto.OrderResponse;
import dev.veecodekub.orderservice.dto.OrderResponse.OrderItemResponse;
import dev.veecodekub.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponse placeOrder(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CreateOrderRequest request) {
        return orderService.placeOrder(userId, request);
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrderById(
            @PathVariable UUID orderId) {
        return orderService.getOrderById(orderId);
    }

    @GetMapping("/customer/{orderId}")
    public OrderResponse getOrderByOrderIdAndCustomerId(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable UUID orderId) {
        return orderService.getOrderByOrderIdAndCustomerId(orderId, userId);
    }

    @GetMapping("/order-items/{orderId}")
    public List<OrderItemResponse> getOrderItemsByOrderId(
            @PathVariable UUID orderId) {
        return orderService.getOrderItemsByOrderId(orderId);
    }
}
