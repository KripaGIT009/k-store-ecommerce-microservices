package com.kstore.order.dto;

import com.kstore.order.entity.Order;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItemRequest> orderItems;

    @NotBlank(message = "Shipping address is required")
    @Size(max = 500, message = "Shipping address cannot exceed 500 characters")
    private String shippingAddress;

    @NotBlank(message = "Billing address is required")
    @Size(max = 500, message = "Billing address cannot exceed 500 characters")
    private String billingAddress;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemRequest {

        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
        private BigDecimal unitPrice;

        @NotBlank(message = "Product name is required")
        private String productName;

        @NotBlank(message = "Product SKU is required")
        private String productSku;

        private String productImageUrl;
    }
}
