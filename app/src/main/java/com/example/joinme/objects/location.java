package com.example.joinme.objects;

import android.location.Location;

import java.util.HashMap;

public class location {
    double latitude,longtitude;
    String address;
    public location(){};
    public location(HashMap data){
        latitude = (double)data.get("latitude");
        longtitude = (double)data.get("longtitude");
        address = (String)data.get("address");
    }

    public location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longtitude = longitude;
    }
    public location(double latitude, double longitude,String address) {
        this.latitude = latitude;
        this.longtitude = longitude;
        this.address = address;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public double distanceTo(location l){
        return Math.abs(l.latitude-latitude)+Math.abs(l.longtitude-longtitude);
    }

    public double distanceTo(Location l){
        return Math.abs(l.getLatitude()-latitude)+Math.abs(l.getLongitude()-longtitude);
    }

    public double distanceTo(double la, double lo){
        return Math.abs(la-this.latitude)+Math.abs(lo-longtitude);
    }
}
