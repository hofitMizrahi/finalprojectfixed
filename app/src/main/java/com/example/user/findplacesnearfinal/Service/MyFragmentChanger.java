package com.example.user.findplacesnearfinal.Service;

import com.example.user.findplacesnearfinal.DataBase.PlacesTable;

/**
 * Created by user on 06/03/2018.
 */

public interface MyFragmentChanger {

    void changeFragments(PlacesTable place);

    void changeToFavorietsFragment();
}
