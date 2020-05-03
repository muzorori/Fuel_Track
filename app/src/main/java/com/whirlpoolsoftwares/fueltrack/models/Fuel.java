package com.whirlpoolsoftwares.fueltrack.models;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class Fuel {
    private String name;
    private String status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Fuel(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public Fuel(){

    }
}
