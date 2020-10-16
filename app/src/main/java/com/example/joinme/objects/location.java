package com.example.joinme.objects;

import java.util.HashMap;

public class location {
    double latitude,longtitude;
    String address;
    public location(HashMap data){
        latitude = (double)data.get("Latitude");
        longtitude = (double)data.get("Longtitude");
        address = (String)data.get("address");
    }
}
