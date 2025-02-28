package com.ecommerce.demo.service;

import com.ecommerce.demo.kafka.producer.OrderEventProducer;
import com.ecommerce.demo.model.Client;
import com.ecommerce.demo.model.Order;
import com.ecommerce.demo.model.OrderStatus;
import com.ecommerce.demo.repository.ClientRepository;
import com.ecommerce.demo.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private OrderEventProducer orderEventProducer;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder() {
        // Arrange
        Client client = new Client();
        client.setId(1L);
        client.setName("John Doe");
        client.setEmail("john.doe@example.com");
        client.setPhone("+1234567890");

        Order order = new Order();
        order.setProductId("prod-123");
        order.setQuantity(2);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        Order createdOrder = orderService.createOrder(order, 1L);

        // Assert
        assertNotNull(createdOrder);
        assertEquals("prod-123", createdOrder.getProductId());
        assertEquals(2, createdOrder.getQuantity());
        assertEquals(client, createdOrder.getClient());
        verify(orderEventProducer, times(1)).publishOrderEvent(createdOrder);
    }

    @Test
    void testCreateOrder_ClientNotFound() {
        // Arrange
        Order order = new Order();
        order.setProductId("prod-123");
        order.setQuantity(2);

        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> orderService.createOrder(order, 1L));
    }

    @Test
    void testUpdateOrderStatus() {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PROCESSING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        Order updatedOrder = orderService.updateOrderStatus(1L, OrderStatus.SHIPPED);

        // Assert
        assertNotNull(updatedOrder);
        assertEquals(OrderStatus.SHIPPED, updatedOrder.getStatus());
        verify(orderEventProducer, times(1)).publishOrderEvent(updatedOrder);
    }

    @Test
    void testUpdateOrderStatus_OrderNotFound() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> orderService.updateOrderStatus(1L, OrderStatus.SHIPPED));
    }

    @Test
    void testGetOrderById() {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setProductId("prod-123");
        order.setQuantity(2);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        Order foundOrder = orderService.getOrderById(1L);

        // Assert
        assertNotNull(foundOrder);
        assertEquals(1L, foundOrder.getId());
        assertEquals("prod-123", foundOrder.getProductId());
        assertEquals(2, foundOrder.getQuantity());
    }

    @Test
    void testGetOrderById_OrderNotFound() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> orderService.getOrderById(1L));
    }

}

