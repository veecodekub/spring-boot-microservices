package dev.veecodekub.orderservice.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.veecodekub.orderservice.enumerate.OrderStatus;
import dev.veecodekub.orderservice.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByIdAndCustomerId(UUID orderId, Long customerId);

    @Modifying
    @Query("UPDATE Order o SET o.status = :status WHERE o.id = :orderId")
    void updateOrderStatus(@Param("orderId") UUID orderId, @Param("status") OrderStatus status);

    Page<Order> findByCustomerId(Long customerId, Pageable pageable);
}
