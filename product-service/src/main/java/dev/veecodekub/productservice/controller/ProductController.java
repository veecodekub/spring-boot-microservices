package dev.veecodekub.productservice.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.veecodekub.productservice.dto.ProductCreationRequest;
import dev.veecodekub.productservice.dto.ProductResponse;
import dev.veecodekub.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ProductResponse createProduct(@Valid @RequestBody ProductCreationRequest request) {
        return productService.createProduct(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('user')")
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('user')")
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }
}
