package com.whirlpoolsoftwares.fueltrack.models;

public class FuelCards {
    private long total;
    private String text;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public FuelCards() {
    }

    public FuelCards(long total, String text) {

        this.total = total;
        this.text = text;
    }
}
