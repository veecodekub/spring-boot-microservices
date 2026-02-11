package dev.veecodekub.orderservice.event;

import java.util.UUID;

public record OrderPaid(UUID orderId) {

}
