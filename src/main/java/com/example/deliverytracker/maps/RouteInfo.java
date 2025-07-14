package com.example.deliverytracker.maps;

public class RouteInfo {
    private double distanceKm;
    private int estimatedMinutes;

    public RouteInfo(double distanceKm, int estimatedMinutes) {
        this.distanceKm = distanceKm;
        this.estimatedMinutes = estimatedMinutes;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(int estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }
} 