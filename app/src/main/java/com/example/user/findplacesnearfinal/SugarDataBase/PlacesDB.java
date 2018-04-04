package com.example.user.findplacesnearfinal.SugarDataBase;

import com.orm.SugarRecord;

public class PlacesDB extends SugarRecord{

    private double lat;
    private double lng;
    private String icon;
    private String name;
    private String isOpen;
    private String photo_reference;
    private double rating;
    private String[] types;
    private String address;
    private boolean isFavoriet;

    public PlacesDB() {
    }

    public PlacesDB(double lat, double lng, String icon, String name, String isOpen, String photo_reference, double rating, String[] types, String address, boolean isFavoriet) {
        this.lat = lat;
        this.lng = lng;
        this.icon = icon;
        this.name = name;
        this.isOpen = isOpen;
        this.photo_reference = photo_reference;
        this.rating = rating;
        this.types = types;
        this.address = address;
        this.isFavoriet = isFavoriet;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getOpening_hours() {
        return isOpen;
    }

    public String getPhoto_reference() {
        return photo_reference;
    }

    public double getRating() {
        return rating;
    }

    public String[] getTypes() {
        return types;
    }

    public String getAddress() {
        return address;
    }

    public boolean isFavoriet() {
        return isFavoriet;
    }

    public String isOpen() {
        return isOpen;
    }
}
