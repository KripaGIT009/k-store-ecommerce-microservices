package com.kstore.product.repository;

import com.kstore.product.entity.Category;
import com.kstore.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String name, String description, Pageable pageable);

    Page<Product> findByCategory(Category category, Pageable pageable);

    Page<Product> findByActiveTrue(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0")
    Page<Product> findInStockProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(@Param("minPrice") java.math.BigDecimal minPrice, 
                                   @Param("maxPrice") java.math.BigDecimal maxPrice, 
                                   Pageable pageable);

    boolean existsBySku(String sku);
    
    boolean existsBySkuAndIdNot(String sku, Long id);
}
