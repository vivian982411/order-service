package com.ecommerce.demo.kafka.producer;

import com.ecommerce.demo.model.Client;
import com.ecommerce.demo.model.Order;
import com.ecommerce.demo.model.OrderStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderEventProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderEventProducer orderEventProducer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPublishOrderEvent() throws JsonProcessingException {
        // Arrange
        Client client = new Client();
        client.setId(1L);
        client.setName("John Doe");
        client.setEmail("test@test.com");
        client.setPhone("1111111111");

        Order order = new Order();
        order.setId(1L);
        order.setProductId("prod-123");
        order.setQuantity(2);
        order.setClient(client);
        order.setStatus(OrderStatus.PROCESSING);

        ReflectionTestUtils.setField(orderEventProducer, "topic", "order-events");
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"order\": {\"id\": 1,\"clientId\":1,\"clientName\":\"John Doe\",\"clientEmail\":\"test@test.com\",\"clientPhone\":\"1111111111\",\"productId\": \"prod-123\",\"quantity\": 2,\"client\": {\"id\": 1,\"name\": \"John Doe\",\"email\": \"test@test.com\",\"phone\": \"1111111111\"},\"status\": \"PROCESSING\"}}");

        // Act
        orderEventProducer.publishOrderEvent(order);

        // Assert
        verify(kafkaTemplate, times(1)).send(eq("order-events"), anyString());
    }

    @Test
    void testPublishOrderEvent_JsonProcessingException() throws JsonProcessingException {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setProductId("prod-123");
        order.setQuantity(2);
        order.setStatus(OrderStatus.PROCESSING);

        when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> orderEventProducer.publishOrderEvent(order));
    }
}



