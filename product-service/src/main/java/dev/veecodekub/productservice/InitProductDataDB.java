package dev.veecodekub.productservice;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import dev.veecodekub.productservice.dto.ProductCreationRequest;
import dev.veecodekub.productservice.service.ProductService;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class InitProductDataDB {

    @Bean
    ApplicationRunner runner(ProductService service) {
        return args -> {
            if (service.getProducts(Pageable.unpaged()).isEmpty()) {
                IntStream.rangeClosed(1, 10)
                        .forEach(i -> service.createProduct(new ProductCreationRequest(
                                "Product " + i,
                                "Description for product " + i,
                                BigDecimal.valueOf(100.0 * i),
                                100)));
                log.info("Successfully initialized 10 products in the database.");
            }
        };
    }
}
