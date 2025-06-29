package com.example.deliverytracker.Pedido;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelarPedidoRequest {
    private String pedidoId;
    private String motivo; // Motivo de la cancelación
    private String repartidorId; // ID del repartidor que cancela el pedido
}
