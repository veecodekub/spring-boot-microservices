package dev.veecodekub.paymentservice.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.veecodekub.paymentservice.dto.PaymentResponse;
import dev.veecodekub.paymentservice.enumerate.PaymentStatus;
import dev.veecodekub.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/order/{orderId}")
    public PaymentResponse getPaymentByOrderId(@PathVariable UUID orderId) {
        return paymentService.getPaymentByOrderId(orderId);
    }

    @PostMapping("/order/{orderId}/pay")
    public void payOrder(@PathVariable UUID orderId) {
        paymentService.updatePaymentStatus(orderId, PaymentStatus.SUCCESS);
    }

    @PostMapping("/order/{orderId}/cancel")
    public void cancelOrder(@PathVariable UUID orderId) {
        paymentService.updatePaymentStatus(orderId, PaymentStatus.FAILED);
    }

    @PostMapping("/order/{orderId}/refund")
    public void refundOrder(@PathVariable UUID orderId) {
        paymentService.updatePaymentStatus(orderId, PaymentStatus.REFUNDED);
    }
}
