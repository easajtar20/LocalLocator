package com.depauw.locallocator.database;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Worksite {

    private String name;
    private String state;
    private String information;
    private int local;
    private float latitude;
    private float longitude;
    private Marker marker;

    public Worksite(String name, String state, String information, int local, float latitude, float longitude) {
        this.name = name;
        this.state = state;
        this.information = information;
        this.local = local;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public String getInformation() {
        return information;
    }

    public int getLocal() {
        return local;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public Marker getMarker() { return marker;}

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
