package com.kstore.order.repository;

import com.kstore.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.status = :status")
    Page<Order> findByUserIdAndStatus(@Param("userId") Long userId, 
                                      @Param("status") Order.OrderStatus status, 
                                      Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.userId = :userId")
    Long countOrdersByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.userId = :userId AND o.status = 'DELIVERED'")
    BigDecimal getTotalSpentByUser(@Param("userId") Long userId);

    boolean existsByOrderNumber(String orderNumber);
}
