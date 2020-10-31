package com.example.joinme.objects;

import android.location.Location;

import java.util.HashMap;

public class location {
    double latitude,longitude;
    String address;
    public location(){};
    public location(HashMap data){
        latitude = (double)data.get("latitude");
        longitude = (double)data.get("longtitude");
        address = (String)data.get("address");
    }

    public location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public location(double latitude, double longitude,String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longtitude) {
        this.longitude = longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public double distanceTo(location l){
        return GetDistance(longitude,latitude,l.getLongitude(),l.getLatitude());
    }

    public double distanceTo(Location l){
        return GetDistance(longitude,latitude,l.getLongitude(),l.getLatitude());
    }

    public double distanceTo(double lo, double la){
        return GetDistance(longitude,latitude,lo,la);
    }

    private static final  double EARTH_RADIUS = 6378137;
    private static double rad(double d){
        return d * Math.PI / 180.0;
    }
    public static double GetDistance(double lon1,double lat1,double lon2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 *Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2)+Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
        s = s * EARTH_RADIUS;
        return s;//unit :: meter
    }


}


