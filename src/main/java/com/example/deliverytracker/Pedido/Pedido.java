package com.example.deliverytracker.Pedido;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Datos del cliente
    private String clienteId;
    private String repartidorId;

    // Datos del local
    private String local;              // Nombre del restaurante/local
    private double latitudLocal;       // Latitud del local
    private double longitudLocal;      // Longitud del local

    // Datos del destino
    private String destination;        // Dirección legible del cliente
    private double latitudDestino;     // Latitud del cliente
    private double longitudDestino;    // Longitud del cliente

    // Detalles del pedido
    private double price;              // Cambiado de 'total' a 'price'
    private String descripcion;
    private String estado;             // PENDIENTE, ACEPTADO, etc.
    private LocalDateTime fechaCreacion;
}