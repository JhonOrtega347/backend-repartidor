package com.example.deliverytracker.Pedido;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Pedido crearPedido(Pedido pedido) {
        pedido.setEstado("PENDIENTE");
        pedido.setFechaCreacion(LocalDateTime.now());
        Pedido nuevoPedido = pedidoRepository.save(pedido);

        messagingTemplate.convertAndSend("/topic/pedidos.nuevos", convertirADto(nuevoPedido));
        return nuevoPedido;
    }

    public Optional<Pedido> findById(String id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> obtenerPedidosPendientes() {
        return pedidoRepository.findByEstado("PENDIENTE");
    }

    public Pedido asignarRepartidor(String pedidoId, String repartidorId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setRepartidorId(repartidorId);
        pedido.setEstado("ACEPTADO");
        return pedidoRepository.save(pedido);
    }

    public void rechazarPedido(String pedidoId, String repartidorId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado("RECHAZADO");
        pedidoRepository.save(pedido);
    }

    // Cambiado a público para poder usarlo en el controller
    public PedidoDto convertirADto(Pedido pedido) {
        return PedidoDto.fromEntity(pedido);
    }
}
