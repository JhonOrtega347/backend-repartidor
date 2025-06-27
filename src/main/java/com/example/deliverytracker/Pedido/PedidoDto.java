package com.example.deliverytracker.Pedido;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDto {
    private String id;
    private String clienteId;
    private String repartidorId;
    private double latitudDestino;
    private double longitudDestino;
    private String direccionDestino;
    private String estado;
    private String descripcion;
    private double total;
    private LocalDateTime fechaCreacion;
    private double distanciaRepartidor; // Opcional: útil para calcular distancia en el frontend

    // Método estático para conversión desde Entidad
    public static PedidoDto fromEntity(Pedido pedido) {
        return new PedidoDto(
                pedido.getId(),
                pedido.getClienteId(),
                pedido.getRepartidorId(),
                pedido.getLatitudDestino(),
                pedido.getLongitudDestino(),
                pedido.getDireccionDestino(),
                pedido.getEstado(),
                pedido.getDescripcion(),
                pedido.getTotal(),
                pedido.getFechaCreacion(),
                0.0 // Inicializar distancia (puede calcularse después)
        );
    }
}
