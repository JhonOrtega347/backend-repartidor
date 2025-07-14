package com.example.deliverytracker.Pedido;

import com.example.deliverytracker.websocket.model.LocationUpdate;
import com.example.deliverytracker.websocket.model.Role;
import com.example.deliverytracker.websocket.model.UbicacionActivaService;
import com.example.deliverytracker.maps.RouteInfo;
import com.example.deliverytracker.maps.GoogleMapsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
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

    @Autowired
    private GoogleMapsService googleMapsService;

    @MessageMapping("/pedido.aceptado")
    public void handlePedidoAceptado(@Payload AceptarPedidoRequest request) {
        log.info("🎯 Iniciando procesamiento de aceptación de pedido {} por repartidor {}", 
                request.getPedidoId(), request.getRepartidorId());
        
        try {
            Pedido pedido = pedidoService.asignarRepartidor(
                    Long.parseLong(request.getPedidoId()),
                    request.getRepartidorId()
            );

            log.info("✅ Pedido {} asignado exitosamente al repartidor {}", 
                    request.getPedidoId(), request.getRepartidorId());

            // ✅ Confirmar al repartidor que el pedido fue aceptado exitosamente
            messagingTemplate.convertAndSendToUser(
                    request.getRepartidorId(),
                    "/pedido-respuesta",
                    Map.of("success", true, "pedidoId", request.getPedidoId())
            );
            log.info("✅ Confirmación de éxito enviada al repartidor {}", request.getRepartidorId());

            messagingTemplate.convertAndSendToUser(
                    pedido.getClienteId(),
                    "/queue/estado-pedido",
                    Map.of("estado", "ACEPTADO", "repartidorId", request.getRepartidorId())
            );
            log.info("✅ Notificación enviada al cliente {}", pedido.getClienteId());

            // Notificar a otros repartidores que el pedido ya fue aceptado
            pedidoService.notificarRepartidoresQuePedidoFueAceptado(
                    pedido.getId(),
                    request.getRepartidorId()
            );
            log.info("✅ Notificaciones enviadas a otros repartidores");

        } catch (RuntimeException e) {
            log.warn("🚫 Error al aceptar pedido {} por repartidor {}: {}", 
                    request.getPedidoId(), request.getRepartidorId(), e.getMessage());
            
            // Notificar al repartidor que el pedido ya no está disponible
            // SIN lanzar excepción que pueda causar desconexión
            try {
                log.info("📤 Enviando mensaje de pedido no disponible a repartidor {}", request.getRepartidorId());
                
                // ✅ Enviar respuesta de error al repartidor
                messagingTemplate.convertAndSendToUser(
                        request.getRepartidorId(),
                        "/pedido-respuesta",
                        Map.of("success", false, "message", e.getMessage(), "pedidoId", request.getPedidoId())
                );
                log.info("✅ Respuesta de error enviada a repartidor {}", request.getRepartidorId());
                
                messagingTemplate.convertAndSendToUser(
                        request.getRepartidorId(),
                        "/queue/pedido-no-disponible",
                        Map.of("pedidoId", request.getPedidoId(), "motivo", e.getMessage())
                );
                log.info("✅ Mensaje de pedido no disponible enviado a repartidor {}", request.getRepartidorId());
            } catch (Exception messagingError) {
                log.error("❌ Error enviando mensaje de pedido no disponible: {}", messagingError.getMessage());
                log.error("❌ Stack trace:", messagingError);
            }
        } catch (Exception e) {
            log.error("❌ Error inesperado al procesar aceptación de pedido: {}", e.getMessage());
            log.error("❌ Stack trace:", e);
            
            // Enviar mensaje de error genérico
            try {
                log.info("📤 Enviando mensaje de error genérico a repartidor {}", request.getRepartidorId());
                
                // ✅ Enviar respuesta de error al repartidor
                messagingTemplate.convertAndSendToUser(
                        request.getRepartidorId(),
                        "/pedido-respuesta",
                        Map.of("success", false, "message", "Error interno del servidor", "pedidoId", request.getPedidoId())
                );
                log.info("✅ Respuesta de error genérico enviada a repartidor {}", request.getRepartidorId());
                
                messagingTemplate.convertAndSendToUser(
                        request.getRepartidorId(),
                        "/queue/pedido-no-disponible",
                        Map.of("pedidoId", request.getPedidoId(), "motivo", "Error interno del servidor")
                );
                log.info("✅ Mensaje de error genérico enviado a repartidor {}", request.getRepartidorId());
            } catch (Exception messagingError) {
                log.error("❌ Error enviando mensaje de error: {}", messagingError.getMessage());
                log.error("❌ Stack trace:", messagingError);
            }
        }
        
        log.info("🏁 Finalizado procesamiento de aceptación de pedido {} por repartidor {}", 
                request.getPedidoId(), request.getRepartidorId());
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

        // Calcular distancia y tiempo real si no están presentes
        if (pedidoDto.getDistanciaReal() == null || pedidoDto.getTiempoEstimado() == null) {
            try {
                RouteInfo routeInfo = googleMapsService.calculateRouteInfo(
                    pedidoDto.getLatitudLocal(), pedidoDto.getLongitudLocal(),
                    pedidoDto.getLatitudDestino(), pedidoDto.getLongitudDestino()
                );
                pedidoDto.setDistanciaReal(routeInfo.getDistanceKm());
                pedidoDto.setTiempoEstimado(routeInfo.getEstimatedMinutes());
                log.info("🗺️ Ruta calculada para pedido {}: {} km, {} min", 
                    pedidoDto.getId(), routeInfo.getDistanceKm(), routeInfo.getEstimatedMinutes());
            } catch (Exception e) {
                log.warn("⚠️ Error calculando ruta para pedido {}: {}", pedidoDto.getId(), e.getMessage());
            }
        }

        List<LocationUpdate> repartidoresDisponibles = ubicacionActivaService.obtenerUbicaciones().stream()
                .filter(loc -> loc.getRole() == Role.REPARTIDOR)
                .toList();

        if (repartidoresDisponibles.isEmpty()) {
            log.warn("⚠️ No hay repartidores disponibles para el pedido.");
            return;
        }

        for (LocationUpdate repartidor : repartidoresDisponibles) {
            String repartidorId = repartidor.getUserId();
            log.info("📦 Enviando pedido {} a repartidor {}", pedidoDto.getId(), repartidorId);

            messagingTemplate.convertAndSendToUser(
                    repartidorId,
                    "/pedidos",
                    pedidoDto
            );

            // ✅ Registra a todos los notificados
            pedidoService.registrarRepartidorNotificado(pedidoDto.getId(), repartidorId);
        }

        log.info("📣 Pedido {} notificado a {} repartidores.", pedidoDto.getId(), repartidoresDisponibles.size());
    }


}