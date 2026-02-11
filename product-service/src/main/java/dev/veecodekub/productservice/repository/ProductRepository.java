package dev.veecodekub.productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.veecodekub.productservice.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
