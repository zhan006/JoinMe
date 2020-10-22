package com.example.joinme.objects;

import java.util.HashMap;

public class location {
    double latitude,longtitude;
    String address;
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
}
