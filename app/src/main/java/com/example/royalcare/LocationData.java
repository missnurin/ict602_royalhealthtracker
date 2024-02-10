package com.example.royalcare;

public class LocationData {
    private double latitude;
    private double longitude;
    private String fullName;
    private String userAgent;
    private String dateTime;

    // Default constructor required for Firebase
    public LocationData() {
    }

    public LocationData(double latitude, double longitude, String fullName, String userAgent, String dateTime) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.fullName = fullName;
        this.userAgent = userAgent;
        this.dateTime = dateTime;
    }

    // Getters and setters for latitude, longitude, fullName, userAgent, and dateTime
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
