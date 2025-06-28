package com.example.deliverytracker.Pedido;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDto {
    private String clienteId;
    private String repartidorId;

    // Datos del local
    private String local;
    private double latitudLocal;
    private double longitudLocal;

    // Datos del destino
    private String destination;
    private double latitudDestino;
    private double longitudDestino;

    private double price;
    private String descripcion;
    private String estado;
    private LocalDateTime fechaCreacion;

    // Método de conversión
    public static PedidoDto fromEntity(Pedido pedido) {
        return new PedidoDto(
                pedido.getClienteId(),
                pedido.getRepartidorId(),
                pedido.getLocal(),
                pedido.getLatitudLocal(),
                pedido.getLongitudLocal(),
                pedido.getDestination(),
                pedido.getLatitudDestino(),
                pedido.getLongitudDestino(),
                pedido.getPrice(),
                pedido.getDescripcion(),
                pedido.getEstado(),
                pedido.getFechaCreacion()
        );
    }
}
