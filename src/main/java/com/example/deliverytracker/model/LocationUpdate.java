package com.example.deliverytracker.model;

// import javax.persistence.Entity;
// import javax.persistence.Id;

// @Entity
public class LocationUpdate {
    // @Id
    private String userId;
    private Role role;
    private double latitude;
    private double longitude;
    private String timestamp;

    public LocationUpdate() {
    }

    public LocationUpdate(String userId, double latitude, double longitude, String timestamp) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public Role getRole() {
        return role;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    // Optional: toString
    @Override
    public String toString() {
        return "LocationUpdate{" +
                "userId='" + userId + '\'' +
                ", role=" + role +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
