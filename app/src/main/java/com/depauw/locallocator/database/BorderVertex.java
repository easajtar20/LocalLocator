package com.depauw.locallocator.database;

public class BorderVertex {

    private float latitude;
    private float longitude;

    public BorderVertex(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }
}
