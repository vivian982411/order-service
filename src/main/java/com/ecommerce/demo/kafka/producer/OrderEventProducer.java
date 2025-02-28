package com.ecommerce.demo.kafka.producer;

import com.ecommerce.demo.model.Order;
import com.ecommerce.demo.model.OrderEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderEventProducer {

    @Value("${kafka.topic.order-events}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @CircuitBreaker(name = "kafkaPublisher", fallbackMethod = "fallbackPublishOrderEvent")
    public void publishOrderEvent(Order order) {
        try {
            log.info("Publishing order event for order ID: {}", order.getId());
            OrderEvent event = new OrderEvent(
                    order.getId(),
                    order.getClient().getId(),
                    order.getClient().getName(),
                    order.getClient().getEmail(),
                    order.getClient().getPhone(),
                    order.getProductId(),
                    order.getQuantity(),
                    order.getStatus()
            );
            String eventJson = objectMapper.writeValueAsString(event);
            log.debug("Order event JSON: {}", eventJson);
            kafkaTemplate.send(topic, eventJson);
            log.info("Order event published successfully for order ID: {}", order.getId());
        } catch (JsonProcessingException e) {
            log.error("Error serializing order event for order ID: {}", order.getId(), e);
            throw new RuntimeException("Error serializing order event", e);
        } catch (Exception e) {
            log.error("Error publishing order event for order ID: {}", order.getId(), e);
            throw new RuntimeException("Error publishing order event", e);
        }
    }

    public void fallbackPublishOrderEvent(Order order, Throwable t) {
        log.error("Fallback: Unable to publish order event for order ID: {}. Error: {}", order.getId(), t.getMessage());
    }
}
