package dev.veecodekub.productservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

import dev.veecodekub.productservice.dto.ProductCreationRequest;
import dev.veecodekub.productservice.dto.ProductResponse;
import dev.veecodekub.productservice.model.Product;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    Product toProduct(ProductCreationRequest request);

    @Mapping(target = "quantity", ignore = true)
    ProductResponse toProductResponse(Product product);

    @Mapping(target = "quantity", source = "quantity")
    ProductResponse toProductResponseWithQuantity(Product product, int quantity);
}
