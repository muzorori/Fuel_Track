package com.whirlpoolsoftwares.fueltrack.models;

public class Stations {
    private String name,diesel,petrol,gas,address;
    private double latitude,longitude;

    public Stations() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiesel() {
        return diesel;
    }

    public void setDiesel(String diesel) {
        this.diesel = diesel;
    }

    public String getPetrol() {
        return petrol;
    }

    public void setPetrol(String petrol) {
        this.petrol = petrol;
    }

    public String getGas() {
        return gas;
    }

    public void setGas(String gas) {
        this.gas = gas;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Stations(String name, String diesel, String petrol, String gas, double latitude, double longitude, String address) {
        this.name = name;
        this.address = address;
        this.diesel = diesel;
        this.petrol = petrol;
        this.gas = gas;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
