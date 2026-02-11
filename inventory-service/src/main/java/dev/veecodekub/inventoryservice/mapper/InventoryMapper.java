package dev.veecodekub.inventoryservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

import dev.veecodekub.inventoryservice.dto.InventoryRequest;
import dev.veecodekub.inventoryservice.dto.InventoryResponse;
import dev.veecodekub.inventoryservice.model.Inventory;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InventoryMapper {

    @Mapping(target = "id", ignore = true)
    Inventory toInventory(InventoryRequest request);

    InventoryResponse toInventoryResponse(Inventory inventory);
}
