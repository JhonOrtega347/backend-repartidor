package com.example.deliverytracker.Pedido;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDto {
    private Long id;
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

    // Nuevos campos para distancia y tiempo real
    private Double distanciaReal;
    private Integer tiempoEstimado;

    // Método de conversión
    public static PedidoDto fromEntity(Pedido pedido) {
        return new PedidoDto(
                pedido.getId(),
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
                pedido.getFechaCreacion(),
                null, // distanciaReal se calculará después
                null  // tiempoEstimado se calculará después
        );
    }
}
