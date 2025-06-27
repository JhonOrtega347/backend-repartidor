package com.example.deliverytracker.Pedido;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*") // Permitir solicitudes desde cualquier origen
public class OrderController {
    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<Pedido> crearPedido(@RequestBody Pedido pedido) {
        Pedido nuevoPedido = pedidoService.crearPedido(pedido);
        return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
    }

    @GetMapping("/pendientes")
    public List<Pedido> getPedidosPendientes() {
        return pedidoService.obtenerPedidosPendientes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDto> getPedido(@PathVariable String id) {
        return pedidoService.findById(id)
                .map(pedido -> ResponseEntity.ok(pedidoService.convertirADto(pedido)))
                .orElse(ResponseEntity.notFound().build());
    }
}