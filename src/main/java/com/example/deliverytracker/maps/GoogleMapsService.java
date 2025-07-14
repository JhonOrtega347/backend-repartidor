package com.example.deliverytracker.maps;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class GoogleMapsService {
    @Value("${google.api.key}")
    private String apiKey;

    public RouteInfo calculateRouteInfo(double originLat, double originLng, double destLat, double destLng) {
        String url = UriComponentsBuilder.fromHttpUrl("https://maps.googleapis.com/maps/api/directions/json")
                .queryParam("origin", originLat + "," + originLng)
                .queryParam("destination", destLat + "," + destLng)
                .queryParam("key", apiKey)
                .build().toUriString();

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        JSONObject json = new JSONObject(response);
        JSONArray routes = json.getJSONArray("routes");
        if (routes.length() == 0) {
            throw new RuntimeException("No se encontró ruta entre los puntos dados");
        }
        JSONObject leg = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0);
        double distanceKm = leg.getJSONObject("distance").getDouble("value") / 1000.0;
        int durationMin = (int) Math.round(leg.getJSONObject("duration").getDouble("value") / 60.0);
        return new RouteInfo(distanceKm, durationMin);
    }
} 