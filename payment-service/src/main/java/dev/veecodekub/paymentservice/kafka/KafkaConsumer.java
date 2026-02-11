package dev.veecodekub.paymentservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import dev.veecodekub.paymentservice.dto.OrderStockReservedEvent;
import dev.veecodekub.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "order-stock-reserved", groupId = "payment-service")
    public void listenOrderStockReserved(OrderStockReservedEvent event) {
        log.debug("listenOrderStockReserved-[next]:(creating payment). event: {}", event);
        paymentService.createPayment(
                event.customerId(),
                event.orderId(),
                event.amount(),
                event.paymentMethod(),
                event.paymentCurrency());
        log.debug("listenOrderStockReserved-[next]:(create payment successfully) event: {}", event);
    }
}