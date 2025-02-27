package com.ecommerce.demo.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderEvent {
    private Long orderId;           // ID de la orden
    private Long clientId;          // ID del cliente
    private String clientName;      // Nombre del cliente
    private String clientEmail;     // Correo electrónico del cliente
    private String clientPhone;     // Teléfono del cliente
    private String productId;       // ID del producto
    private Integer quantity;       // Cantidad del producto
    private OrderStatus status;     // Estado de la orden
    private LocalDateTime eventTimestamp; // Fecha y hora del evento

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