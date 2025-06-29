package com.example.deliverytracker.websocket.controller;

import com.example.deliverytracker.websocket.model.GoogleDirectionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
public class RutaController {

    private final GoogleDirectionsService directionsService;

    @GetMapping
    public Mono<Map> obtenerRuta(
            @RequestParam double origenLat,
            @RequestParam double origenLng,
            @RequestParam double destinoLat,
            @RequestParam double destinoLng) {

        return directionsService.obtenerRuta(origenLat, origenLng, destinoLat, destinoLng);
    }
}
