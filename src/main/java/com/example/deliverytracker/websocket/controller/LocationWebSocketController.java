package com.example.deliverytracker.websocket.controller;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.example.deliverytracker.websocket.model.UbicacionActivaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.deliverytracker.websocket.model.LocationUpdate; // Un POJO para tu objeto de ubicación
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Opcional: Si necesitas un servicio para manejar la lógica de negocio y persistencia
// import com.example.locationtracker.service.LocationService;
@Controller
public class LocationWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(LocationWebSocketController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UbicacionActivaService ubicacionActivaService;

    @MessageMapping("/update-location")
    public void handleLocationUpdate(LocationUpdate locationUpdate) {
        logger.info("📍 Ubicación recibida de {}: {}", locationUpdate.getUserId(), locationUpdate);

        if (locationUpdate.getRole() == null) {
            logger.warn("⚠️ El campo 'role' es NULL para el usuario: {}", locationUpdate.getUserId());
        } else {
            logger.info("✅ Role recibido correctamente: {}", locationUpdate.getRole());
        }

        ubicacionActivaService.actualizarUbicacion(locationUpdate);

        List<LocationUpdate> locationsList = ubicacionActivaService.obtenerUbicaciones().stream().toList();

        messagingTemplate.convertAndSend("/topic/locations", locationsList);
    }
}