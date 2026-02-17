package dev.veecodekub.inventoryservice.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.veecodekub.common.error.BusinessException;
import dev.veecodekub.common.error.inventory.InventoryErrorCode;
import dev.veecodekub.inventoryservice.client.OrderClient;
import dev.veecodekub.inventoryservice.dto.InventoryRequest;
import dev.veecodekub.inventoryservice.dto.OrderSnapshot;
import dev.veecodekub.inventoryservice.model.Inventory;
import dev.veecodekub.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final OrderClient orderClient;
    private final InventoryReservationService reservationService;

    @Transactional
    public void addInventory(InventoryRequest request) {
        log.debug("addInventory-[next]:(adding inventory) request: {}", request);

        if (inventoryRepository.existsByProductId(request.productId())) {
            log.warn("addInventory-[warn]:(product already exists) request: {}", request);
            throw new BusinessException(InventoryErrorCode.PRODUCT_ALREADY_EXISTS);
        }

        Inventory inventory = Inventory.builder()
                .productId(request.productId())
                .availableQty(request.quantity())
                .reservedQty(0)
                .build();

        inventoryRepository.save(inventory);
        log.debug("addInventory-[next]:(inventory added successfully) inventory: {}", inventory);
    }

    @Transactional(readOnly = true)
    public Integer getAvailableQtyByProductId(Long productId) {
        log.debug("getAvailableQtyByProductId-[next]:(fetching inventory) productId: {}", productId);

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> {
                    log.warn("getAvailableQtyByProductId-[warn]:(product not found) productId: {}", productId);
                    throw new BusinessException(InventoryErrorCode.PRODUCT_NOT_FOUND);
                });
        log.debug("getAvailableQtyByProductId-[next]:(inventory fetched successfully) inventory: {}", inventory);

        return inventory.getAvailableQty();
    }

    @Transactional(readOnly = true)
    public Map<Long, Integer> getStockBatch(List<Long> productIds) {
        log.debug("getStockBatch-[next]:(fetching inventory) productIds: {}", productIds);

        Map<Long, Integer> inventoryMap = inventoryRepository.findByProductIdIn(productIds)
                .stream()
                .collect(Collectors.toMap(
                        Inventory::getProductId,
                        Inventory::getAvailableQty));
        log.debug("getStockBatch-[next]:(inventory fetched successfully) inventoryMap: {}", inventoryMap);

        return inventoryMap;
    }

    public void reserveStockForOrder(UUID orderId) {
        log.debug("reserveStockForOrder-[next]:(reserving stock) orderId: {}", orderId);

        OrderSnapshot orderSnapshot = fetchOrderByOrderId(orderId);
        log.debug("reserveStockForOrder-[next]:(order fetched successfully) orderSnapshot: {}", orderSnapshot);

        reservationService.reserveStock(orderSnapshot);
        log.debug("reserveStockForOrder-[next]:(stock reserved successfully) orderId: {}", orderId);
    }

    public void releaseStockForOrder(UUID orderId) {
        log.debug("releaseStockForOrder-[next]:(releasing stock) orderId: {}", orderId);

        OrderSnapshot orderSnapshot = fetchOrderByOrderId(orderId);
        log.debug("releaseStockForOrder-[next]:(order fetched successfully) orderSnapshot: {}", orderSnapshot);

        reservationService.releaseStock(orderSnapshot);
        log.debug("releaseStockForOrder-[next]:(stock released successfully) orderId: {}", orderId);
    }

    public OrderSnapshot fetchOrderByOrderId(UUID orderId) {
        log.debug("fetchOrderByOrderId-[next]:(fetching order) orderId: {}", orderId);

        OrderSnapshot orderSnapshot = orderClient.getOrderByOrderId(orderId);
        log.debug("fetchOrderByOrderId-[next]:(order fetched successfully) orderSnapshot: {}", orderSnapshot);

        return orderSnapshot;
    }
}
