package com.example.deliverytracker.Pedido;

import com.example.deliverytracker.websocket.model.LocationUpdate;
import com.example.deliverytracker.websocket.model.Role;
import com.example.deliverytracker.websocket.model.UbicacionActivaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class PedidoSocketController {

    private static final Logger log = LoggerFactory.getLogger(PedidoSocketController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UbicacionActivaService ubicacionActivaService;

    @MessageMapping("/pedido.aceptado")
    public void handlePedidoAceptado(@Payload AceptarPedidoRequest request) {
        try {
            Pedido pedido = pedidoService.asignarRepartidor(
                    Long.parseLong(request.getPedidoId()),
                    request.getRepartidorId()
            );

            messagingTemplate.convertAndSendToUser(
                    pedido.getClienteId(),
                    "/queue/estado-pedido",
                    Map.of("estado", "ACEPTADO", "repartidorId", request.getRepartidorId())
            );
        } catch (RuntimeException e) {
            // Notificar al repartidor que el pedido ya no está disponible
            messagingTemplate.convertAndSendToUser(
                    request.getRepartidorId(),
                    "/queue/pedido-no-disponible",
                    Map.of("pedidoId", request.getPedidoId(), "motivo", e.getMessage())
            );
        }
    }


    @MessageMapping("/pedido.rechazado")
    public void handlePedidoRechazado(@Payload RechazarPedidoRequest request) {
        pedidoService.rechazarPedido(Long.parseLong(request.getPedidoId()), request.getRepartidorId());
    }
    @MessageMapping("/pedido.cancelado")
    public void handlePedidoCancelado(@Payload CancelarPedidoRequest request) {
        pedidoService.cancelarPedido(
                Long.parseLong(request.getPedidoId()),
                request.getMotivo()
        );

        // Notificar a repartidor
        messagingTemplate.convertAndSendToUser(
                request.getRepartidorId(),
                "/pedido-cancelado",
                Map.of(
                        "pedidoId", request.getPedidoId(),
                        "motivo", request.getMotivo()
                )
        );

        // Notificar al cliente
        Pedido pedido = pedidoService.findById(Long.parseLong(request.getPedidoId()))
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        messagingTemplate.convertAndSendToUser(
                pedido.getClienteId(),
                "/pedido-cancelado",
                Map.of(
                        "pedidoId", request.getPedidoId(),
                        "motivo", request.getMotivo()
                )
        );
    }
    @MessageMapping("/pedido.nuevo")
    public void handleNuevoPedido(PedidoDto pedidoDto) {
        log.info("📥 Pedido recibido en handleNuevoPedido: {}", pedidoDto);
        Optional<LocationUpdate> repartidorDisponible = ubicacionActivaService.obtenerUbicaciones().stream()
                .filter(loc -> loc.getRole() == Role.REPARTIDOR)
                .findAny(); // o aplica lógica de cercanía

        if (repartidorDisponible.isPresent()) {
            String repartidorId = repartidorDisponible.get().getUserId();
            pedidoDto.setRepartidorId(repartidorId);

            messagingTemplate.convertAndSendToUser(
                    repartidorId,
                    "/pedidos",
                    pedidoDto
            );
            pedidoService.registrarRepartidorNotificado(pedidoDto.getId(), repartidorId);
            log.info("✅ Pedido asignado automáticamente a: {}", repartidorId);
        } else {
            log.warn("⚠️ No hay repartidores disponibles para el pedido.");
        }
    }
}