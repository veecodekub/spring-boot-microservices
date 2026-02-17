package dev.veecodekub.productservice.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.veecodekub.common.error.BusinessException;
import dev.veecodekub.common.error.product.ProductErrorCode;
import dev.veecodekub.productservice.client.InventoryClient;
import dev.veecodekub.productservice.dto.CreateInventoryRequest;
import dev.veecodekub.productservice.dto.ProductCreationRequest;
import dev.veecodekub.productservice.dto.ProductResponse;
import dev.veecodekub.productservice.mapper.ProductMapper;
import dev.veecodekub.productservice.model.Product;
import dev.veecodekub.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final InventoryClient inventoryClient;

    @Transactional
    public ProductResponse createProduct(ProductCreationRequest request) {
        log.debug("createProduct-[next]:(creating product). request: {}", request);

        Product product = productMapper.toProduct(request);
        productRepository.save(product);
        log.debug("createProduct-[next]:(product created). product: {}", product);

        inventoryClient.addInventory(new CreateInventoryRequest(product.getId(), request.quantity()));
        log.debug("createProduct-[next]:(inventory added). product: {}", product);

        ProductResponse response = productMapper.toProductResponseWithQuantity(product, request.quantity());
        log.debug("createProduct-[next]:(product response). response: {}", response);

        return response;
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.debug("getProductById-[next]:(fetching product). id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("getProductById-[warn]:(product not found). id: {}", id);
                    return new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND);
                });

        int availableQty = inventoryClient.getAvailableQtyByProductId(id);
        log.debug("getProductById-[next]:(inventory found). availableQty: {}", availableQty);

        ProductResponse response = productMapper.toProductResponseWithQuantity(product, availableQty);
        log.debug("getProductById-[next]:(product response). response: {}", response);

        return response;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(Pageable pageable) {
        log.debug("getProducts-[next]:(fetching all products)");

        Page<Product> page = productRepository.findAll(pageable);

        List<Long> productIds = page.getContent()
                .stream()
                .map(Product::getId)
                .toList();

        Map<Long, Integer> stockMap = inventoryClient.getStockBatch(productIds);

        Page<ProductResponse> responses = page.map(product -> productMapper.toProductResponseWithQuantity(product,
                stockMap.getOrDefault(product.getId(), 0)));

        log.debug("getProducts-[next]:(products response). response: {}", responses);

        return responses;
    }
}
