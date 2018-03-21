package com.example.user.findplacesnearfinal.DataBase;

import com.example.user.findplacesnearfinal.Model.Geometry;
import com.example.user.findplacesnearfinal.Model.Location;
import com.example.user.findplacesnearfinal.Model.OpenHours;
import com.example.user.findplacesnearfinal.Model.Photo;
import com.orm.SugarRecord;

import java.util.List;

public class PlacesTable extends SugarRecord{

    private double lat;
    private double lng;
    private String icon;
    private String name;
    private String isOpen;
    private String photo_reference;
    private double rating;
    private String[] types;
    private String address;

    public PlacesTable() {
    }

    public PlacesTable(double lat, double lng, String icon, String name, String isOpen, String photo_reference, double rating, String[] types, String address) {
        this.lat = lat;
        this.lng = lng;
        this.icon = icon;
        this.name = name;
        this.isOpen = isOpen;
        this.photo_reference = photo_reference;
        this.rating = rating;
        this.types = types;
        this.address = address;
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
}
