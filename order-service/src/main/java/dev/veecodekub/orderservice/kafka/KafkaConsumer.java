package dev.veecodekub.orderservice.kafka;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import dev.veecodekub.orderservice.client.InventoryClient;
import dev.veecodekub.orderservice.enumerate.OrderStatus;
import dev.veecodekub.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final OrderService orderService;
    private final InventoryClient inventoryClient;

    @KafkaListener(topics = "order-payment-created", groupId = "order-service")
    public void listenOrderPaymentCreated(UUID orderId) {
        log.debug("listenOrderPaymentCreated-[next]:(order payment created). event: {}", orderId);
        orderService.updateOrderStatus(orderId, OrderStatus.PAYMENT_PENDING);
        log.debug("listenOrderPaymentCreated-[next]:(order payment created successfully) event: {}", orderId);
    }

    @KafkaListener(topics = "order-payment-succeeded", groupId = "order-service")
    public void listenOrderPaymentSucceeded(UUID orderId) {
        log.debug("listenOrderPaymentSucceeded-[next]:(order payment succeeded). event: {}", orderId);
        orderService.updateOrderStatus(orderId, OrderStatus.PAID);
        log.debug("listenOrderPaymentSucceeded-[next]:(order payment succeeded successfully) event: {}", orderId);
    }

    @KafkaListener(topics = "order-payment-failed", groupId = "order-service")
    public void listenOrderPaymentFailed(UUID orderId) {
        log.debug("listenOrderPaymentFailed-[next]:(order payment failed). event: {}", orderId);
        inventoryClient.release(orderId);
        orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);
        log.debug("listenOrderPaymentFailed-[next]:(order payment failed successfully) event: {}", orderId);
    }

    @KafkaListener(topics = "order-payment-refunded", groupId = "order-service")
    public void listenOrderPaymentRefunded(UUID orderId) {
        log.debug("listenOrderPaymentRefunded-[next]:(order payment refunded). event: {}", orderId);
        inventoryClient.release(orderId);
        orderService.updateOrderStatus(orderId, OrderStatus.REFUNDED);
        log.debug("listenOrderPaymentRefunded-[next]:(order payment refunded successfully) event: {}", orderId);
    }
}