package dev.veecodekub.orderservice.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import dev.veecodekub.orderservice.dto.CreateOrderRequest.OrderItemRequest;
import dev.veecodekub.orderservice.enumerate.OrderStatus;
import dev.veecodekub.orderservice.enumerate.PaymentMethod;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "orders", indexes = {
        @Index(columnList = "customer_id", name = "idx_customer_id")
})
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal total;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private String paymentCurrency;

    @Version
    private int version;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public static Order create(
            Long customerId,
            List<OrderItemRequest> items,
            PaymentMethod paymentMethod,
            String paymentCurrency) {
        Order order = Order.builder()
                .customerId(customerId)
                .status(OrderStatus.CREATED)
                .paymentMethod(paymentMethod)
                .paymentCurrency(paymentCurrency)
                .build();

        for (OrderItemRequest item : items) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(item.productId())
                    .productName(item.productName())
                    .productPrice(item.productPrice())
                    .quantity(item.quantity())
                    .total(item.productPrice()
                            .multiply(new BigDecimal(item.quantity()))
                            .setScale(2, RoundingMode.HALF_UP))
                    .build();

            order.getItems().add(orderItem);
        }

        order.setTotal(order.getItems().stream()
                .map(OrderItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return order;
    }
}
