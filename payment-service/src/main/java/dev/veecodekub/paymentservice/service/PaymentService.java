package dev.veecodekub.paymentservice.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.veecodekub.common.error.BusinessException;
import dev.veecodekub.common.error.payment.PaymentErrorCode;
import dev.veecodekub.paymentservice.dto.PaymentResponse;
import dev.veecodekub.paymentservice.enumerate.PaymentMethod;
import dev.veecodekub.paymentservice.enumerate.PaymentStatus;
import dev.veecodekub.paymentservice.mapper.PaymentMapper;
import dev.veecodekub.paymentservice.model.Payment;
import dev.veecodekub.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public PaymentResponse createPayment(
            Long payerId,
            UUID orderId,
            BigDecimal amount,
            PaymentMethod method,
            String currency) {
        log.debug(
                "createPayment-[next]:(creating payment). payerId: {}, orderId: {}, amount: {}, method: {}, currency: {}",
                payerId, orderId, amount, method, currency);

        if (paymentRepository.existsByOrderId(orderId)) {
            log.warn("createPayment-[warn]:(payment already exists). orderId: {}", orderId);
            throw new BusinessException(PaymentErrorCode.PAYMENT_ALREADY_EXISTS);
        }

        Payment payment = Payment.builder()
                .payerId(payerId)
                .orderId(orderId)
                .amount(amount)
                .currency(currency)
                .method(method)
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);
        log.debug("createPayment-[next]:(payment saved). payment: {}", payment);

        kafkaTemplate.send("order-payment-created", orderId);
        log.debug("createPayment-[next]:(sent order-payment-created event). orderId: {}", orderId);

        PaymentResponse response = paymentMapper.toPaymentResponse(payment);
        log.debug("createPayment-[next]:(payment created). response: {}", response);

        return response;
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(UUID orderId) {
        log.debug("getPaymentByOrderId-[next]:(fetching payment). orderId: {}", orderId);

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> {
                    log.warn("getPaymentByOrderId-[next]:(payment not found). orderId: {}", orderId);
                    throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND);
                });

        PaymentResponse response = paymentMapper.toPaymentResponse(payment);
        log.debug("getPaymentByOrderId-[next]:(payment found). response: {}", response);

        return response;
    }

    @Transactional
    public void updatePaymentStatus(UUID orderId, PaymentStatus status) {
        log.debug("updatePaymentStatus-[next]:(updating payment status). orderId: {}, status: {}", orderId, status);

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> {
                    log.warn("updatePaymentStatus-[next]:(payment not found). orderId: {}", orderId);
                    throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND);
                });

        if (payment.getStatus() != PaymentStatus.PENDING) {
            log.warn("updatePaymentStatus-[next]:(payment not in pending state). payment: {}", payment);
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_PENDING);
        }

        payment.setStatus(status);
        paymentRepository.save(payment);
        log.debug("updatePaymentStatus-[next]:(payment updated). payment: {}", payment);

        switch (status) {
            case SUCCESS:
                log.debug("updatePaymentStatus-[next]:(payment status updated to success). payment: {}", payment);
                kafkaTemplate.send("order-payment-succeeded", orderId);
                break;
            case FAILED:
                log.debug("updatePaymentStatus-[next]:(payment status updated to failed). payment: {}", payment);
                kafkaTemplate.send("order-payment-failed", orderId);
                break;
            case REFUNDED:
                log.debug("updatePaymentStatus-[next]:(payment status updated to refunded). payment: {}", payment);
                kafkaTemplate.send("order-payment-refunded", orderId);
                break;
            default:
                break;
        }
    }
}
