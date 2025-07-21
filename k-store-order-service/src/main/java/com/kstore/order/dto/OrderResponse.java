package com.kstore.order.dto;

import com.kstore.order.entity.Order;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private String orderNumber;
    private Long userId;
    private Order.OrderStatus status;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String billingAddress;
    private String paymentMethod;
    private Order.PaymentStatus paymentStatus;
    private List<OrderItemResponse> orderItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private String notes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String productSku;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private String productImageUrl;
    }
}
