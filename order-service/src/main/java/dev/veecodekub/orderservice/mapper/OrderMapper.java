package dev.veecodekub.orderservice.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

import dev.veecodekub.orderservice.dto.OrderResponse;
import dev.veecodekub.orderservice.dto.OrderResponse.OrderItemResponse;
import dev.veecodekub.orderservice.model.Order;
import dev.veecodekub.orderservice.model.OrderItem;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {

    @Mapping(target = "customerId", ignore = true)
    OrderResponse toOrderResponse(Order order);

    OrderResponse toOrderResponseWithCustomerId(Order order);

    List<OrderItemResponse> toOrderItemResponse(List<OrderItem> orderItems);
}
