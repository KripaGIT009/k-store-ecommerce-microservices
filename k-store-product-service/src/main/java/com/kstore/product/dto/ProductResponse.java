package com.kstore.product.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String sku;
    private String imageUrl;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Category information
    private CategoryResponse category;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryResponse {
        private Long id;
        private String name;
        private String description;
    }
}
