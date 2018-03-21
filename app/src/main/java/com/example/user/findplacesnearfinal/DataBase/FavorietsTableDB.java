package com.example.user.findplacesnearfinal.DataBase;

import com.orm.SugarRecord;

/**
 * Created by user on 16/03/2018.
 */

public class FavorietsTableDB extends SugarRecord {

    private String name;
    private String photo_reference;

    public FavorietsTableDB() {
    }

    public FavorietsTableDB(String name, String photo_reference) {
        this.name = name;
        this.photo_reference = photo_reference;
    }

    public String getName() {
        return name;
    }

    public String getPhoto_reference() {
        return photo_reference;
    }
}
