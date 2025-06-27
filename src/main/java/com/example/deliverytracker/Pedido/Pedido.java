package com.example.deliverytracker.Pedido;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Pedido {
    @Id
    private String id; // ID único del pedido (puede ser String o Long si es numérico)
    private String clienteId; // ID del cliente que hizo el pedido
    private String repartidorId; // ID del repartidor asignado (null al principio)
    private double latitudDestino; // Latitud del cliente
    private double longitudDestino; // Longitud del cliente
    private String direccionDestino; // Dirección legible del cliente
    private String estado; // Pendiente, Asignado, EnCamino, Entregado, Cancelado
    private String descripcion; // Detalles del pedido (ej. "Pizza pepperoni + Coca Cola")
    private double total; // Precio total del pedido
    private LocalDateTime fechaCreacion;
}