package dev.veecodekub.orderservice.event;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderStockReserved(UUID orderId, BigDecimal amount) {

}
