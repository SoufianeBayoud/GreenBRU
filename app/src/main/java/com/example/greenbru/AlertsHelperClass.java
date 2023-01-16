package com.example.greenbru;

public class AlertsHelperClass {
    String title, description, imageURL;
    double lat, longitude;

    public AlertsHelperClass(String title, String description, String imageURL, double lat, double longitude) {
        this.title = title;
        this.description = description;
        this.imageURL = imageURL;
        this.lat = lat;
        this.longitude = longitude;
    }

    public AlertsHelperClass() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }
}
