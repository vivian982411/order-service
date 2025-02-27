package com.ecommerce.demo.model;


public enum OrderStatus {
    PROCESSING,    // La orden está en proceso
    SHIPPED,       // La orden ha sido enviada
    DELIVERED,     // La orden ha sido entregada
    CANCELLED      // La orden ha sido cancelada
}