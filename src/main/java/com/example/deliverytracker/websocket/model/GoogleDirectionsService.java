package com.example.deliverytracker.websocket.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleDirectionsService {

    private final WebClient webClient;

    @Value("${google.api.key}")
    private String apiKey;

    public Mono<Map> obtenerRuta(double origenLat, double origenLng, double destinoLat, double destinoLng) {
        String origin = origenLat + "," + origenLng;
        String destination = destinoLat + "," + destinoLng;

        log.info("🌐 Consultando ruta desde {} hasta {}", origin, destination);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/json")
                        .queryParam("origin", origin)
                        .queryParam("destination", destination)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(Map.class);
    }
}

