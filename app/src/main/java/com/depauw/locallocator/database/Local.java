package com.depauw.locallocator.database;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;

import java.util.List;

public class Local {
    private int identifier;
    private int color;
    private Polygon polygon;
    private int founded;
    private String phone;
    private String address;

    private double minLat;
    private double maxLat;
    private double minLng;
    private double maxLng;

    public Local(int identifier){
        this.identifier = identifier;
        this.color = Color.BLACK;
    }
    public Local(int identifier, int color){
        this.identifier = identifier;
        this.color = color;
    }

    public Local(int identifier, int founded, String phone, String address){
        this.identifier = identifier;
        this.founded = founded;
        this.phone = phone;
        this.address = address;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
        defineExtremes();
    }

    private void defineExtremes(){
        minLat = Double.MAX_VALUE;
        maxLat = -Double.MAX_VALUE;
        minLng = Double.MAX_VALUE;
        maxLng = -Double.MAX_VALUE;

        List<LatLng> points = polygon.getPoints();
        for(int i = 0; i < points.size(); i++){
            double lat = points.get(i).latitude;
            double lng = points.get(i).longitude;

            if(lat < minLat) {
                minLat = lat;
            }
            if(lat > maxLat){
                maxLat = lat;
            }
            if(lng < minLng){
                minLng = lng;
            }
            if(lng > maxLng){
                maxLng = lng;
            }
        }
    }

    public int getIdentifier(){
        return identifier;
    }

    public int getColor(){
        return color;
    }

    public void setColor(int color){
        this.color = color;
    }

    public Polygon getPolygon(){
        return polygon;
    }

    public int getFounded(){
        return founded;
    }

    public String getPhone(){
        return phone;
    }

    public String getAddress(){
        return address;
    }

    public double getMinLat() {
        return minLat;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public double getMinLng() {
        return minLng;
    }

    public double getMaxLng() {
        return maxLng;
    }
}
