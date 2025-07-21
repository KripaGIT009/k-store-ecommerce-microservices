package com.kstore.order.service.impl;

import com.kstore.common.exception.BusinessException;
import com.kstore.common.exception.ResourceNotFoundException;
import com.kstore.order.dto.OrderRequest;
import com.kstore.order.dto.OrderResponse;
import com.kstore.order.entity.Order;
import com.kstore.order.entity.OrderItem;
import com.kstore.order.repository.OrderRepository;
import com.kstore.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        // Generate unique order number
        String orderNumber = generateOrderNumber();
        
        // Calculate total amount
        BigDecimal totalAmount = request.getOrderItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create order
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .userId(request.getUserId())
                .status(Order.OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .shippingAddress(request.getShippingAddress())
                .billingAddress(request.getBillingAddress())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(Order.PaymentStatus.PENDING)
                .notes(request.getNotes())
                .build();

        // Create order items
        List<OrderItem> orderItems = request.getOrderItems().stream()
                .map(itemRequest -> {
                    BigDecimal itemTotal = itemRequest.getUnitPrice()
                            .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
                    
                    return OrderItem.builder()
                            .order(order)
                            .productId(itemRequest.getProductId())
                            .productName(itemRequest.getProductName())
                            .productSku(itemRequest.getProductSku())
                            .quantity(itemRequest.getQuantity())
                            .unitPrice(itemRequest.getUnitPrice())
                            .totalPrice(itemTotal)
                            .productImageUrl(itemRequest.getProductImageUrl())
                            .build();
                })
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return mapToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with order number: " + orderNumber));
        return mapToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public OrderResponse updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // Business logic for status transitions
        validateStatusTransition(order.getStatus(), status);

        order.setStatus(status);
        
        // Set timestamps based on status
        switch (status) {
            case SHIPPED:
                order.setShippedAt(LocalDateTime.now());
                break;
            case DELIVERED:
                order.setDeliveredAt(LocalDateTime.now());
                break;
        }

        Order updatedOrder = orderRepository.save(order);
        return mapToResponse(updatedOrder);
    }

    @Override
    public OrderResponse updatePaymentStatus(Long id, Order.PaymentStatus paymentStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        order.setPaymentStatus(paymentStatus);
        
        // If payment is successful, move order to confirmed status
        if (paymentStatus == Order.PaymentStatus.PAID && order.getStatus() == Order.OrderStatus.PENDING) {
            order.setStatus(Order.OrderStatus.CONFIRMED);
        }

        Order updatedOrder = orderRepository.save(order);
        return mapToResponse(updatedOrder);
    }

    @Override
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // Can only cancel orders that are not yet shipped
        if (order.getStatus() == Order.OrderStatus.SHIPPED || 
            order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new BusinessException("Cannot cancel order that has been shipped or delivered");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByStatus(Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrdersByStatus(Long userId, Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByUserIdAndStatus(userId, status, pageable)
                .map(this::mapToResponse);
    }

    private String generateOrderNumber() {
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "ORD-" + uuid;
    }

    private void validateStatusTransition(Order.OrderStatus currentStatus, Order.OrderStatus newStatus) {
        // Define valid status transitions
        switch (currentStatus) {
            case PENDING:
                if (newStatus != Order.OrderStatus.CONFIRMED && newStatus != Order.OrderStatus.CANCELLED) {
                    throw new BusinessException("Invalid status transition from PENDING to " + newStatus);
                }
                break;
            case CONFIRMED:
                if (newStatus != Order.OrderStatus.PROCESSING && newStatus != Order.OrderStatus.CANCELLED) {
                    throw new BusinessException("Invalid status transition from CONFIRMED to " + newStatus);
                }
                break;
            case PROCESSING:
                if (newStatus != Order.OrderStatus.SHIPPED && newStatus != Order.OrderStatus.CANCELLED) {
                    throw new BusinessException("Invalid status transition from PROCESSING to " + newStatus);
                }
                break;
            case SHIPPED:
                if (newStatus != Order.OrderStatus.DELIVERED) {
                    throw new BusinessException("Invalid status transition from SHIPPED to " + newStatus);
                }
                break;
            case DELIVERED:
                if (newStatus != Order.OrderStatus.REFUNDED) {
                    throw new BusinessException("Invalid status transition from DELIVERED to " + newStatus);
                }
                break;
            default:
                throw new BusinessException("Cannot transition from status: " + currentStatus);
        }
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderResponse.OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> OrderResponse.OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .productSku(item.getProductSku())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .productImageUrl(item.getProductImageUrl())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .billingAddress(order.getBillingAddress())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .orderItems(itemResponses)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .shippedAt(order.getShippedAt())
                .deliveredAt(order.getDeliveredAt())
                .notes(order.getNotes())
                .build();
    }
}
