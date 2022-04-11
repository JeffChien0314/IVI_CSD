package com.fxc.ev.launcher.maps.search;

import com.tomtom.navkit2.place.Location;

import java.io.Serializable;

public class FavEditItem implements Serializable {
    private String name;
    private int image;
    private int background;
    private int textColor;
    private Location location;
    private String address;
    private int distance;

    public FavEditItem(){

    }

    public FavEditItem(String name, int image, int background, int textColor, Location location, String address) {
        this.name = name;
        this.image = image;
        this.background = background;
        this.textColor = textColor;
        this.location = location;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
