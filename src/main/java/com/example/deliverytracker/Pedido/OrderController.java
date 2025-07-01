package com.example.deliverytracker.Pedido;

import com.example.deliverytracker.websocket.model.LocationUpdate;
import com.example.deliverytracker.websocket.model.Role;
import com.example.deliverytracker.websocket.model.UbicacionActivaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import com.example.deliverytracker.Pedido.PedidoDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*") // Permitir solicitudes desde cualquier origen
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(PedidoSocketController.class);

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UbicacionActivaService ubicacionActivaService;

    @PostMapping
    public ResponseEntity<PedidoDto> crearPedido(@RequestBody Pedido newpedido) {
        Pedido pedido = pedidoService.crearPedido(newpedido);
        PedidoDto dto = PedidoDto.fromEntity(pedido);


        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarPedido(
            @PathVariable Long id,
            @RequestParam String clienteId) {
        try {
            Pedido pedido = pedidoService.cancelarPedido(id, clienteId);
            return ResponseEntity.ok(PedidoDto.fromEntity(pedido));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }



    @GetMapping("/{id}")
    public ResponseEntity<PedidoDto> getPedido(@PathVariable Long id) {
        return pedidoService.findById(id)
                .map(pedido -> ResponseEntity.ok(pedidoService.convertirADto(pedido)))
                .orElse(ResponseEntity.notFound().build());
    }
}