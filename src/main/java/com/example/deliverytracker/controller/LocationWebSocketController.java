package com.example.deliverytracker.controller;


import com.example.deliverytracker.model.LocationUpdate; // Un POJO para tu objeto de ubicación
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

// Opcional: Si necesitas un servicio para manejar la lógica de negocio y persistencia
// import com.example.locationtracker.service.LocationService;

@Controller
public class LocationWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // Para enviar mensajes a clientes suscritos

    // Opcional: Inyectar un servicio si tienes lógica de negocio o persistencia
    // @Autowired
    // private LocationService locationService;

    /**
     * Maneja los mensajes entrantes de ubicación desde los clientes.
     * Los clientes deben enviar a "/app/update-location".
     *
     * @param locationUpdate El objeto LocationUpdate enviado por el cliente.
     */
    @MessageMapping("/update-location") // Define el destino de la aplicación (parte de "/app/update-location")
    public void handleLocationUpdate(LocationUpdate locationUpdate) {
        System.out.println("Received location update: " + locationUpdate.getUserId() +
                " - Lat: " + locationUpdate.getLatitude() +
                ", Lon: " + locationUpdate.getLongitude());

        // Opcional: Guardar la ubicación en la base de datos
        // locationService.saveLocation(locationUpdate);

        // Envía la actualización de ubicación a todos los clientes suscritos a "/topic/locations"
        // Esto es lo que permite la "tiempo real" en otros dispositivos.
        messagingTemplate.convertAndSend("/topic/locations", locationUpdate);
    }

    // Si tuvieras un endpoint REST para inicializar o hacer otras cosas:
    // @GetMapping("/api/last-locations")
    // public ResponseEntity<List<LocationUpdate>> getLastLocations() {
    //     // Obtener las últimas ubicaciones de la base de datos
    //     List<LocationUpdate> lastLocations = locationService.getAllLastLocations();
    //     return ResponseEntity.ok(lastLocations);
    // }
}
