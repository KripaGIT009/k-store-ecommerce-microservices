package com.kstore.product.service;

import com.kstore.product.dto.ProductRequest;
import com.kstore.product.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    
    Page<ProductResponse> getAllProducts(Pageable pageable);
    
    ProductResponse getProductById(Long id);
    
    ProductResponse createProduct(ProductRequest request);
    
    ProductResponse updateProduct(Long id, ProductRequest request);
    
    void deleteProduct(Long id);
    
    Page<ProductResponse> searchProducts(String keyword, Pageable pageable);
    
    Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable);
}
