package com.ecommerce.demo.controller;

import com.ecommerce.demo.model.Order;
import com.ecommerce.demo.model.OrderStatus;
import com.ecommerce.demo.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@Tag(name = "Order API", description = "API para gestionar órdenes de compra")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    @Operation(summary = "Creación de una orden", description = "Crea una nueva orden")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la orden inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Order> createOrder(@Parameter(description = "Datos de la orden", required = true)
                                                 @RequestBody Order order,
                                             @Parameter(description = "ID del cliente", required = true)
                                             @RequestParam Long clientId) {
        return ResponseEntity.ok(orderService.createOrder(order, clientId));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Actualizar el estado de una orden", description = "Actualiza el estado de una orden existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado de la orden actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Order> updateOrderStatus(@Parameter(description = "ID de la orden", required = true)
                                                       @PathVariable Long id,
                                                   @Parameter(description = "Nuevo estado de la orden", required = true)
                                                   @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una orden por ID", description = "Recupera los detalles de una orden buscaando por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden encontrada"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Order> getOrderById(@Parameter(description = "ID de la orden", required = true)
                                                  @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
}
