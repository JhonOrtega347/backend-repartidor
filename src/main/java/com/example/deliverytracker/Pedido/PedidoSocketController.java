package com.example.deliverytracker.Pedido;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;
@Controller
public class PedidoSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private PedidoService pedidoService;

    @MessageMapping("/pedido.aceptado")
    public void handlePedidoAceptado(@Payload AceptarPedidoRequest request) {
        Pedido pedido = pedidoService.asignarRepartidor(
                request.getPedidoId(),
                request.getRepartidorId()
        );

        messagingTemplate.convertAndSendToUser(
                pedido.getClienteId(),
                "/queue/estado-pedido",
                Map.of("estado", "ACEPTADO", "repartidorId", request.getRepartidorId())
        );
    }

    @MessageMapping("/pedido.rechazado")
    public void handlePedidoRechazado(@Payload RechazarPedidoRequest request) {
        pedidoService.rechazarPedido(request.getPedidoId(), request.getRepartidorId());
    }

    @MessageMapping("/pedido.nuevo")
    public void notificarNuevoPedido(@Payload String pedidoId) {
        pedidoService.findById(pedidoId).ifPresent(pedido -> {
            PedidoDto dto = pedidoService.convertirADto(pedido);
            messagingTemplate.convertAndSend("/topic/pedidos.nuevos", dto);
        });
    }
}
