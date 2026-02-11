package dev.veecodekub.orderservice.service;

import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.veecodekub.common.error.BusinessException;
import dev.veecodekub.common.error.order.OrderErrorCode;
import dev.veecodekub.orderservice.dto.CreateOrderRequest;
import dev.veecodekub.orderservice.dto.OrderResponse;
import dev.veecodekub.orderservice.dto.OrderResponse.OrderItemResponse;
import dev.veecodekub.orderservice.enumerate.OrderStatus;
import dev.veecodekub.orderservice.event.OrderCreated;
import dev.veecodekub.orderservice.mapper.OrderMapper;
import dev.veecodekub.orderservice.model.Order;
import dev.veecodekub.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public OrderResponse placeOrder(Long customerId, CreateOrderRequest request) {
        log.debug("placeOrder-[next]:(placing order). customerId: {}, request: {}", customerId, request);

        Order order = Order.create(
                customerId,
                request.items(),
                request.paymentMethod(),
                request.paymentCurrency());

        orderRepository.save(order);
        log.debug("placeOrder-[next]:(order saved). order: {}", order);

        publisher.publishEvent(new OrderCreated(order.getId()));
        log.debug("placeOrder-[next]:(order created event published). orderId: {}", order.getId());

        UUID orderId = order.getId();
        log.debug("placeOrder-[next]:(returning order id). orderId: {}", orderId);

        OrderResponse response = orderMapper.toOrderResponse(order);
        log.debug("placeOrder-[next]:(returning order response). response: {}", response);

        return response;
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID orderId) {
        log.debug("getOrderById-[next]:(fetching order). orderId: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("getOrderById-[next]:(order not found). orderId: {}", orderId);
                    return new BusinessException(OrderErrorCode.ORDER_NOT_FOUND);
                });

        OrderResponse response = orderMapper.toOrderResponseWithCustomerId(order);
        log.debug("getOrderByOrderId-[next]:(order response). response: {}", response);

        return response;
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderIdAndCustomerId(UUID orderId, Long customerId) {
        log.debug("getOrderByOrderIdAndCustomerId-[next]:(fetching order). orderId: {}, customerId: {}",
                orderId, customerId);

        Order order = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> {
                    log.warn(
                            "getOrderByOrderIdAndCustomerId-[next]:(order not found). orderId: {}, customerId: {}",
                            customerId, orderId);
                    return new BusinessException(OrderErrorCode.ORDER_NOT_FOUND);
                });

        OrderResponse response = orderMapper.toOrderResponse(order);
        log.debug("getOrderByOrderIdAndCustomerId-[next]:(order response). response: {}", response);

        return response;
    }

    @Transactional(readOnly = true)
    public List<OrderItemResponse> getOrderItemsByOrderId(UUID orderId) {
        log.debug("getOrderItemsByOrderId-[next]:(fetching order). orderId: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("getOrderItemsByOrderId-[next]:(order not found). orderId: {}", orderId);
                    return new BusinessException(OrderErrorCode.ORDER_NOT_FOUND);
                });

        List<OrderItemResponse> responses = orderMapper.toOrderItemResponse(order.getItems());
        log.debug("getOrderItemsByOrderId-[next]:(order item responses). responses: {}", responses);

        return responses;
    }

    @Transactional
    public void updateOrderStatus(UUID orderId, OrderStatus status) {
        log.debug("updateOrderStatus-[next]:(updating order status). orderId: {}, status: {}", orderId,
                status);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("updateOrderStatus-[next]:(order not found). orderId: {}", orderId);
                    return new BusinessException(OrderErrorCode.ORDER_NOT_FOUND);
                });

        order.setStatus(status);
        orderRepository.save(order);
        log.debug("updateOrderStatus-[next]:(order saved). order: {}", order);

    }
}
