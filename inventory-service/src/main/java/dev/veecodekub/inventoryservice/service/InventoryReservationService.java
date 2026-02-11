package dev.veecodekub.inventoryservice.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.veecodekub.common.error.BusinessException;
import dev.veecodekub.common.error.inventory.InventoryErrorCode;
import dev.veecodekub.inventoryservice.dto.OrderSnapshot;
import dev.veecodekub.inventoryservice.dto.OrderSnapshot.OrderItemSnapshot;
import dev.veecodekub.inventoryservice.dto.OrderStockReservedEvent;
import dev.veecodekub.inventoryservice.model.Inventory;
import dev.veecodekub.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryReservationService {

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public void reserveStock(OrderSnapshot orderSnapshot) {
        log.debug("reserveStock-[next]:(reserve stock). orderSnapshot: {}", orderSnapshot);

        List<Long> productIds = orderSnapshot.items().stream()
                .map(OrderItemSnapshot::productId)
                .toList();
        log.debug("reserveStock-[next]:(productIds from orderItems). productIds: {}", productIds);

        List<Inventory> inventories = inventoryRepository.findAllForUpdate(productIds);
        log.debug("reserveStock-[next]:(fetch inventories). inventories: {}", inventories);

        if (inventories.size() != productIds.size()) {
            log.warn("reserveStock-[warn]:(inventory size not match). inventoriesSize: {}, productIdsSize: {}",
                    inventories.size(), productIds.size());
            throw new BusinessException(InventoryErrorCode.PRODUCT_NOT_FOUND);
        }
        log.debug("reserveStock-[next]:(inventories size). inventoriesSize: {}", inventories.size());

        Map<Long, Inventory> inventoryMap = inventories.stream()
                .collect(Collectors.toMap(Inventory::getProductId, Function.identity()));
        log.debug("reserveStock-[next]:(inventory map). inventoryMap: {}", inventoryMap);

        for (OrderItemSnapshot item : orderSnapshot.items()) {
            Inventory inv = inventoryMap.get(item.productId());
            if (inv == null) {
                log.warn("reserveStock-[warn]:(product not found). productId: {}", item.productId());
                throw new BusinessException(InventoryErrorCode.PRODUCT_NOT_FOUND);
            }
            if (inv.getAvailableQty() < item.quantity()) {
                log.warn("reserveStock-[warn]:(product out of stock). productId: {}", item.productId());
                throw new BusinessException(InventoryErrorCode.OUT_OF_STOCK);
            }
        }
        log.debug("reserveStock-[next]:(check stock). inventories: {}", inventories);

        for (OrderItemSnapshot item : orderSnapshot.items()) {
            Inventory inv = inventoryMap.get(item.productId());
            if (inv == null) {
                log.warn("reserveStock-[warn]:(product not found). productId: {}", item.productId());
                throw new BusinessException(InventoryErrorCode.PRODUCT_NOT_FOUND);
            }
            inv.decrease(item.quantity());
        }
        log.debug("reserveStock-[next]:(decrease stock). inventories: {}", inventories);

        OrderStockReservedEvent event = new OrderStockReservedEvent(
                orderSnapshot.id(),
                orderSnapshot.customerId(),
                orderSnapshot.total(),
                orderSnapshot.paymentMethod(),
                orderSnapshot.paymentCurrency());
        kafkaTemplate.send("order-stock-reserved", event);
        log.debug("reserveStock-[next]:(publish event). orderSnapshot: {}", orderSnapshot);
    }

    @Transactional
    public void releaseStock(OrderSnapshot orderSnapshot) {
        log.debug("releaseStock-[next]:(release stock). orderSnapshot: {}", orderSnapshot);

        List<Long> productIds = orderSnapshot.items().stream()
                .map(OrderItemSnapshot::productId)
                .toList();
        log.debug("releaseStock-[next]:(productIds from orderItems). productIds: {}", productIds);

        List<Inventory> inventories = inventoryRepository.findAllForUpdate(productIds);
        log.debug("releaseStock-[next]:(fetch inventories). inventories: {}", inventories);
        if (inventories.size() != productIds.size()) {
            log.warn("releaseStock-[warn]:(product not found). productIds: {}", productIds);
            throw new BusinessException(InventoryErrorCode.PRODUCT_NOT_FOUND);
        }
        log.debug("releaseStock-[next]:(check inventories size). inventoriesSize: {}", inventories.size());

        Map<Long, Inventory> inventoryMap = inventories.stream()
                .collect(Collectors.toMap(Inventory::getProductId, Function.identity()));
        log.debug("releaseStock-[next]:(inventory map). inventoryMap: {}", inventoryMap);

        for (OrderItemSnapshot item : orderSnapshot.items()) {
            Inventory inv = inventoryMap.get(item.productId());
            if (inv == null) {
                log.warn("releaseStock-[warn]:(product not found). productId: {}", item.productId());
                throw new BusinessException(InventoryErrorCode.PRODUCT_NOT_FOUND);
            }
            inv.increase(item.quantity());
        }
        log.debug("releaseStock-[next]:(stock released for order). orderSnapshot: {}", orderSnapshot);
    }
}
