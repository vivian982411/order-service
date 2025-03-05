package com.ecommerce.demo.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderEvent {
    private Long orderId;
    private Long clientId;
    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private String productId;
    private Integer quantity;
    private OrderStatus status;
    private LocalDateTime eventTimestamp;

    public OrderEvent(Long orderId, Long clientId, String clientName, String clientEmail, String clientPhone,
                      String productId, Integer quantity, OrderStatus status) {
        this.orderId = orderId;
        this.clientId = clientId;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.productId = productId;
        this.quantity = quantity;
        this.status = status;
        this.eventTimestamp = LocalDateTime.now();
    }
}