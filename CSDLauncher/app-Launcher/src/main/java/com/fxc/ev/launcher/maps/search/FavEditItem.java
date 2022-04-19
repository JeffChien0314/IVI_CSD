package com.fxc.ev.launcher.maps.search;

import java.io.Serializable;
import java.util.Objects;

public class FavEditItem implements Serializable {
    private String name;
    private int image;
    private int background;
    private int textColor;
    private String coordinate;
    private String address;
    private int distance;

    public FavEditItem(){

    }

    public FavEditItem(String name, int image, int background, int textColor, String coordinate, String address) {
        this.name = name;
        this.image = image;
        this.background = background;
        this.textColor = textColor;
        this.coordinate = coordinate;
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

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavEditItem that = (FavEditItem) o;
        return Objects.equals(coordinate, that.coordinate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinate);
    }
}
