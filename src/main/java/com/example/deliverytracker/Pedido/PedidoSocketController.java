package com.example.deliverytracker.Pedido;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class PedidoSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private PedidoService pedidoService;

    private static final Logger logger = LoggerFactory.getLogger(PedidoSocketController.class);

    @MessageMapping("/pedido.aceptado")
    public void handlePedidoAceptado(@Payload AceptarPedidoRequest request) {
        Pedido pedido = pedidoService.asignarRepartidor(
                Long.parseLong(request.getPedidoId()),
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
        pedidoService.rechazarPedido(Long.parseLong(request.getPedidoId()), request.getRepartidorId());
    }

    @MessageMapping("/pedido.nuevo")
    public void handleNuevoPedido(PedidoDto pedidoDto) {
        // Enviar a repartidor específico
        messagingTemplate.convertAndSendToUser(
                pedidoDto.getRepartidorId(),
                "/pedidos",
                pedidoDto
        );
    }
}
