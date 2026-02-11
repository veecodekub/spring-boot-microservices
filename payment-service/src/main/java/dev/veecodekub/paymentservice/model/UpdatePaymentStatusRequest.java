package dev.veecodekub.paymentservice.model;

import dev.veecodekub.paymentservice.enumerate.PaymentStatus;

public record UpdatePaymentStatusRequest(
        PaymentStatus status) {

}
