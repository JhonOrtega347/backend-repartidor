package com.example.deliverytracker.websocket.model;

import com.example.deliverytracker.Pedido.PedidoSocketController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class UbicacionActivaService {
    private static final Logger log = LoggerFactory.getLogger(PedidoSocketController.class);

    private final ConcurrentHashMap<String, LocationUpdate> activeLocations = new ConcurrentHashMap<>();

    public void actualizarUbicacion(LocationUpdate loc) {
        log.info("✅ Actualizando ubicación activa: {}", loc);
        activeLocations.put(loc.getUserId(), loc);
    }

    public Collection<LocationUpdate> obtenerUbicaciones() {
        log.info("📦 Ubicaciones activas actuales: {}", activeLocations.values());
        return activeLocations.values();
    }
}
