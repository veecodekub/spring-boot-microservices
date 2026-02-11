package dev.veecodekub.paymentservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

import dev.veecodekub.paymentservice.dto.PaymentResponse;
import dev.veecodekub.paymentservice.model.Payment;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentMapper {

    PaymentResponse toPaymentResponse(Payment payment);
}
