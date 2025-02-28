package com.ecommerce.demo.controller;

import com.ecommerce.demo.model.Order;
import com.ecommerce.demo.model.OrderStatus;
import com.ecommerce.demo.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest {
    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    void testCreateOrder() throws Exception {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setProductId("prod-123");
        order.setQuantity(2);

        when(orderService.createOrder(any(Order.class), eq(1L))).thenReturn(order);

        // Act & Assert
        mockMvc.perform(post("/orders")
                        .param("clientId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\": \"prod-123\", \"quantity\": 2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.productId").value("prod-123"))
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    void testUpdateOrderStatus() throws Exception {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.SHIPPED);

        when(orderService.updateOrderStatus(eq(1L), eq(OrderStatus.SHIPPED))).thenReturn(order);

        // Act & Assert
        mockMvc.perform(put("/orders/1/status")
                        .param("status", "SHIPPED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }

    @Test
    void testGetOrderById() throws Exception {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setProductId("prod-123");
        order.setQuantity(2);

        when(orderService.getOrderById(1L)).thenReturn(order);

        // Act & Assert
        mockMvc.perform(get("/orders/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.productId").value("prod-123"))
                .andExpect(jsonPath("$.quantity").value(2));
    }

}

