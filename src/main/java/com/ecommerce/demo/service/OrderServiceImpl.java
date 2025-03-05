package com.ecommerce.demo.service;

import com.ecommerce.demo.kafka.producer.OrderEventProducer;
import com.ecommerce.demo.model.Client;
import com.ecommerce.demo.model.Order;
import com.ecommerce.demo.model.OrderStatus;
import com.ecommerce.demo.repository.ClientRepository;
import com.ecommerce.demo.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OrderEventProducer orderEventProducer;

    @CircuitBreaker(name = "databaseOperations", fallbackMethod = "fallbackCreateOrder")
    @Override
    public Order createOrder(Order order, Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> {
                    log.error("Client not found with ID: {}", clientId);
                    return new RuntimeException("Client not found");
                });
        order.setClient(client);
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getId());
        orderEventProducer.publishOrderEvent(savedOrder);

        return savedOrder;
    }

    @CircuitBreaker(name = "databaseOperations", fallbackMethod = "fallbackUpdateOrderStatus")
    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        log.info("Updating status of order ID: {} to {}", orderId, status);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found with ID: {}", orderId);
                    return new RuntimeException("Order not found");
                });
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated successfully for ID: {}", orderId);
        orderEventProducer.publishOrderEvent(updatedOrder);

        return updatedOrder;
    }

    @CircuitBreaker(name = "databaseOperations", fallbackMethod = "fallbackGetOrderById")
    @Override
    public Order getOrderById(Long orderId) {
        log.info("Fetching order with ID: {}", orderId);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found with ID: {}", orderId);
                    return new RuntimeException("Orden no encontrada");
                });
    }

    public Order fallbackCreateOrder(Order order, Long clientId, Throwable t) {
        log.error("Fallback: Unable to create order for client ID: {}. Error: {}", clientId, t.getMessage());
        return null;
    }

    public Order fallbackUpdateOrderStatus(Long orderId, OrderStatus status, Throwable t) {
        log.error("Fallback: Unable to update status of order ID: {}. Error: {}", orderId, t.getMessage());
        return null;
    }

    public Order fallbackGetOrderById(Long orderId, Throwable t) {
        log.error("Fallback: Unable to fetch order with ID: {}. Error: {}", orderId, t.getMessage());
        return null;
    }
}
