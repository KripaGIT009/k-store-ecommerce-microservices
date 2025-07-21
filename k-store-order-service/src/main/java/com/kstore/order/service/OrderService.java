package com.kstore.order.service;

import com.kstore.order.dto.OrderRequest;
import com.kstore.order.dto.OrderResponse;
import com.kstore.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse createOrder(OrderRequest request);

    OrderResponse getOrderById(Long id);

    OrderResponse getOrderByOrderNumber(String orderNumber);

    Page<OrderResponse> getOrdersByUserId(Long userId, Pageable pageable);

    Page<OrderResponse> getAllOrders(Pageable pageable);

    OrderResponse updateOrderStatus(Long id, Order.OrderStatus status);

    OrderResponse updatePaymentStatus(Long id, Order.PaymentStatus paymentStatus);

    void cancelOrder(Long id);

    Page<OrderResponse> getOrdersByStatus(Order.OrderStatus status, Pageable pageable);

    Page<OrderResponse> getUserOrdersByStatus(Long userId, Order.OrderStatus status, Pageable pageable);
}
