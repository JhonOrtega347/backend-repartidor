package com.example.deliverytracker.Pedido;

import com.example.deliverytracker.websocket.model.LocationUpdate;
import com.example.deliverytracker.websocket.model.Role;
import com.example.deliverytracker.websocket.model.UbicacionActivaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoSocketController.class);

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UbicacionActivaService ubicacionActivaService;

    // Almacena qué repartidores fueron notificados por pedido
    private final Map<Long, Set<String>> repartidoresNotificados = new ConcurrentHashMap<>();

    public Pedido crearPedido(Pedido pedido) {
        pedido.setEstado("PENDIENTE");
        pedido.setFechaCreacion(LocalDateTime.now());
        Pedido guardado = pedidoRepository.save(pedido);
        log.info("🆔 Pedido guardado con ID: {}", guardado.getId());

        PedidoDto dto = PedidoDto.fromEntity(guardado);

        List<LocationUpdate> disponibles = ubicacionActivaService.obtenerUbicaciones().stream()
                .filter(loc -> loc.getRole() == Role.REPARTIDOR)
                .toList();

        if (disponibles.isEmpty()) {
            log.warn("⚠️ No hay repartidores disponibles para el pedido {}", guardado.getId());
            return guardado;
        }

        for (LocationUpdate repartidor : disponibles) {
            String repartidorId = repartidor.getUserId();
            messagingTemplate.convertAndSendToUser(repartidorId, "/pedidos", dto);
            registrarRepartidorNotificado(dto.getId(), repartidorId);
            log.info("📦 Enviado pedido {} a repartidor {}", dto.getId(), repartidorId);
        }

        log.info("📣 Pedido {} notificado a {} repartidores.", dto.getId(), disponibles.size());
        return guardado;
    }

    public void registrarRepartidorNotificado(Long pedidoId, String repartidorId) {
        repartidoresNotificados
                .computeIfAbsent(pedidoId, k -> ConcurrentHashMap.newKeySet())
                .add(repartidorId);
    }

    public Optional<Pedido> findById(Long id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> obtenerPedidosPendientes() {
        return pedidoRepository.findByEstado("PENDIENTE");
    }

    @Transactional
    public Pedido asignarRepartidor(Long pedidoId, String repartidorId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (!pedido.getEstado().equals("PENDIENTE")) {
            log.warn("🚫 Pedido {} ya no está disponible. Estado actual: {}", pedidoId, pedido.getEstado());
            throw new RuntimeException("Pedido ya fue aceptado o no está disponible");
        }

        pedido.setRepartidorId(repartidorId);
        pedido.setEstado("ACEPTADO");

        repartidoresNotificados.remove(pedidoId); // Limpiar si ya se asignó
        return pedidoRepository.save(pedido);
    }

    public void rechazarPedido(Long pedidoId, String repartidorId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado("RECHAZADO");
        pedidoRepository.save(pedido);
    }
    public Pedido cancelarPedido(Long pedidoId, String clienteId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (!pedido.getClienteId().equals(clienteId)) {
            throw new RuntimeException("Solo el cliente puede cancelar su pedido");
        }

        if (!pedido.getEstado().equals("PENDIENTE")) {
            throw new RuntimeException("Solo se pueden cancelar pedidos PENDIENTES");
        }

        pedido.setEstado("CANCELADO");
        Pedido cancelado = pedidoRepository.save(pedido);

        // 🔥 Agregado: notificar a todos los repartidores que lo recibieron
        Set<String> repartidores = repartidoresNotificados.get(pedidoId);
        if (repartidores != null) {
            for (String repartidorId : repartidores) {
                messagingTemplate.convertAndSendToUser(
                        repartidorId,
                        "/pedido-cancelado",
                        Map.of("pedidoId", pedidoId, "motivo", "Cancelado por el cliente")
                );
            }
            log.info("📢 Notificados {} repartidores sobre cancelación del pedido {}", repartidores.size(), pedidoId);
        } else {
            log.warn("⚠️ No había repartidores registrados para el pedido {}", pedidoId);
        }

        return cancelado;
    }

    public void notificarRepartidoresQuePedidoFueAceptado(Long pedidoId, String repartidorQueAcepto) {
        Set<String> repartidores = repartidoresNotificados.get(pedidoId);
        if (repartidores != null) {
            for (String repartidorId : repartidores) {
                if (!repartidorId.equals(repartidorQueAcepto)) {
                    messagingTemplate.convertAndSendToUser(
                            repartidorId,
                            "/pedido-cancelado",
                            Map.of(
                                    "pedidoId", pedidoId,
                                    "motivo", "Otro repartidor ya aceptó el pedido"
                            )
                    );
                }
            }
            log.info("📢 Notificados {} repartidores (excepto {}) que el pedido {} fue aceptado", repartidores.size() - 1, repartidorQueAcepto, pedidoId);
        } else {
            log.warn("⚠️ No hay repartidores registrados para notificar en el pedido {}", pedidoId);
        }
    }



    public PedidoDto convertirADto(Pedido pedido) {
        return PedidoDto.fromEntity(pedido);
    }
}

