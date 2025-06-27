package com.example.deliverytracker.Pedido;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AceptarPedidoRequest {
    private String pedidoId;
    private String repartidorId;
}
