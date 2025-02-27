package com.ecommerce.demo.service;

import com.ecommerce.demo.model.Order;
import com.ecommerce.demo.model.OrderStatus;

public interface OrderService {
    Order createOrder(Order order, Long clientId);

    Order updateOrderStatus(Long orderId, OrderStatus status);

    Order getOrderById(Long orderId);
}
