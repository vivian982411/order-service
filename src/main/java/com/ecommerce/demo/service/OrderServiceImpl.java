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
        // Buscar el cliente por su ID
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> {
                    log.error("Client not found with ID: {}", clientId);
                    return new RuntimeException("Client not found");
                });

        // Asignar el cliente a la orden
        order.setClient(client);

        // Guardar la orden
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getId());

        // Publicar evento de creación de orden
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

        // Actualizar el estado de la orden
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated successfully for ID: {}", orderId);


        // Publicar evento de actualización de orden
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

    // Métodos de fallback para operaciones de base de datos
    public Order fallbackCreateOrder(Order order, Long clientId, Throwable t) {
        log.error("Fallback: Unable to create order for client ID: {}. Error: {}", clientId, t.getMessage());
        // Aquí puedes implementar una lógica alternativa, como guardar la orden en una cola para reintentar más tarde.
        return null;
    }

    public Order fallbackUpdateOrderStatus(Long orderId, OrderStatus status, Throwable t) {
        log.error("Fallback: Unable to update status of order ID: {}. Error: {}", orderId, t.getMessage());
        // Aquí puedes implementar una lógica alternativa, como guardar la actualización en una cola para reintentar más tarde.
        return null;
    }

    public Order fallbackGetOrderById(Long orderId, Throwable t) {
        log.error("Fallback: Unable to fetch order with ID: {}. Error: {}", orderId, t.getMessage());
        // Aquí puedes devolver una respuesta por defecto o lanzar una excepción personalizada.
        return null;
    }
}
