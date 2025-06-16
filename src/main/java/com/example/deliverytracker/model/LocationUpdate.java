package com.example.deliverytracker.model;

import lombok.Data; // Si usas Lombok
// import javax.persistence.Entity; // Si usas JPA
// import javax.persistence.Id;

// @Entity // Si es una entidad JPA
@Data // Genera getters, setters, toString, equals, hashCode con Lombok
public class LocationUpdate {
    // @Id // Si es una entidad JPA y necesitas un ID
    private String userId;
    private Role role;
    private double latitude;
    private double longitude;
    private long timestamp; // O LocalDateTime, Instant, etc.

    // Constructor, getters y setters si no usas Lombok
    public LocationUpdate() {}

    public LocationUpdate(String userId, double latitude, double longitude, long timestamp) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    // Getters y Setters...
    // @Override public String toString() { ... }
}
