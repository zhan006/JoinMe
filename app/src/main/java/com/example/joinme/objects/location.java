package com.example.joinme.objects;

import android.location.Location;

import java.io.Serializable;
import java.util.HashMap;

public class location implements Serializable {
    public double latitude,longtitude;
    public String address;
    public location(){}

    public location(HashMap data){
        latitude = (double)data.get("latitude");
        longtitude = (double)data.get("longtitude");
        address = (String)data.get("address");
    }

    public location(double latitude, double longtitude) {
        this.latitude = latitude;
        this.longtitude = longtitude;
    }
    public location(double latitude, double longtitude,String address) {
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.address = address;
    }

    public void setlatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setlongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longtitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }


    //calculate the distance to different format of locations.
    public double distanceTo(location l){
        return GetDistance(longtitude,latitude,l.getLongitude(),l.getLatitude());
    }

    public double distanceTo(Location l){
        return GetDistance(longtitude,latitude,l.getLongitude(),l.getLatitude());
    }

    public double distanceTo(double lo, double la){
        return GetDistance(longtitude,latitude,lo,la);
    }

    //Using formula provided By GOOGLE MAP
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


