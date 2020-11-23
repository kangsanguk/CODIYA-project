package com.sample.covid19;

public class Road {
    String number;
    String address;
    double lat;
    double lng;

    public String getNumber(){
        return number;
    }

    public void setNumber(String number){
        this.number = number;
    }

    public String getAddress(){
        return address;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public double getLat(){
        return lat;
    }

    public void setLat(double lat){
        this.lat = lat;
    }

    public double getLng(){
        return lng;
    }

    public void setLng(double lng){
        this.lng = lng;
    }

}
