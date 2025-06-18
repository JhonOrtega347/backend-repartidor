package com.example.deliverytracker.controller;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.deliverytracker.model.LocationUpdate; // Un POJO para tu objeto de ubicación
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Opcional: Si necesitas un servicio para manejar la lógica de negocio y persistencia
// import com.example.locationtracker.service.LocationService;
@Controller
public class LocationWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(LocationWebSocketController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final ConcurrentHashMap<String, LocationUpdate> activeLocations = new ConcurrentHashMap<>();

    @MessageMapping("/update-location")
    public void handleLocationUpdate(LocationUpdate locationUpdate) {

        logger.info("INGRESANDO AL METODO  /update-location");
        String id = locationUpdate.getUserId();
        activeLocations.put(id, locationUpdate);
        System.out.println("/update-location  -> " + id + "" + locationUpdate);
        logger.info("locationUpdate.getUserId()  : {},   locationUpdate: {}  ", id, locationUpdate);

        // Convertir el mapa de ubicaciones activas en una lista
        List<LocationUpdate> locationsList = activeLocations.values().stream().collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(locationsList));
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
        }
        // Enviar **todas** las ubicaciones a los clientes suscritos

        messagingTemplate.convertAndSend("/topic/locations", locationsList);
    }
}