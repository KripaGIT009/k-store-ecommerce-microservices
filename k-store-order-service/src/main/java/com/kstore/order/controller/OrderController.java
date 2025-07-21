package com.kstore.order.controller;

import com.kstore.common.dto.ApiResponse;
import com.kstore.order.dto.OrderRequest;
import com.kstore.order.dto.OrderResponse;
import com.kstore.order.entity.Order;
import com.kstore.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(order, "Order created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByOrderNumber(@PathVariable String orderNumber) {
        OrderResponse order = orderService.getOrderByOrderNumber(orderNumber);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getOrdersByUserId(
            @PathVariable Long userId,
            Pageable pageable) {
        Page<OrderResponse> orders = orderService.getOrdersByUserId(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrders(Pageable pageable) {
        Page<OrderResponse> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Order.OrderStatus status) {
        OrderResponse order = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(order, "Order status updated successfully"));
    }

    @PutMapping("/{id}/payment-status")
    public ResponseEntity<ApiResponse<OrderResponse>> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam Order.PaymentStatus paymentStatus) {
        OrderResponse order = orderService.updatePaymentStatus(id, paymentStatus);
        return ResponseEntity.ok(ApiResponse.success(order, "Payment status updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Order cancelled successfully"));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getOrdersByStatus(
            @PathVariable Order.OrderStatus status,
            Pageable pageable) {
        Page<OrderResponse> orders = orderService.getOrdersByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getUserOrdersByStatus(
            @PathVariable Long userId,
            @PathVariable Order.OrderStatus status,
            Pageable pageable) {
        Page<OrderResponse> orders = orderService.getUserOrdersByStatus(userId, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
}
