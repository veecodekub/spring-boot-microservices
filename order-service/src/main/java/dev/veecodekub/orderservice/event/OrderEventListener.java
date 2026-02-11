package dev.veecodekub.orderservice.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import dev.veecodekub.orderservice.client.InventoryClient;
import dev.veecodekub.orderservice.enumerate.OrderStatus;
import dev.veecodekub.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final InventoryClient inventoryClient;
    private final OrderRepository orderRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreated(OrderCreated event) {
        log.info("Order created: {}", event);
        try {
            inventoryClient.reserve(event.orderId());
        } catch (Exception e) {
            log.error("Failed to reserve inventory for order: {}", event.orderId(), e);
            orderRepository.updateOrderStatus(event.orderId(), OrderStatus.CANCELLED);
        }
    }
}
