package com.example.user.findplacesnearfinal.Activity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.user.findplacesnearfinal.Fragments.FavoritesFragment;
import com.example.user.findplacesnearfinal.Fragments.SearchFragment;
import com.example.user.findplacesnearfinal.R;
import com.example.user.findplacesnearfinal.Service.MyFragmentChanger;
import com.example.user.findplacesnearfinal.DataBase.PlacesTable;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.orm.SugarContext;

public class MainActivity extends AppCompatActivity implements MyFragmentChanger{

    MapFragment mapFragment;
    FavoritesFragment favoritesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SugarContext.init(this);

        mapFragment = new MapFragment();
        favoritesFragment = new FavoritesFragment();
        screenPositionOrder();
        setToolBar();
    }

//-------------------------------------------------------------------------------------------------

    /**
     * method that Initializing the layouts by the device orientation and if its mobile or tablet.
     */

    public void screenPositionOrder() {

        //if its mobile and portrait
        if (!isTablet(this) && isPortrait()) {

            SearchFragment searchFragment = new SearchFragment();
            getFragmentManager().beginTransaction().addToBackStack("replacing").replace(R.id.main_portrait_layout, searchFragment).commit();

            //if its mobile and landscape OR if its tablet
        } else if (!isTablet(this) && !isPortrait() || isTablet(this)) {

            SearchFragment searchFragment = new SearchFragment();
            getFragmentManager().beginTransaction().addToBackStack("replacing").replace(R.id.search_tablet_layout, searchFragment).commit();

            getFragmentManager().beginTransaction().addToBackStack("replacing").replace(R.id.tablet_map_layout, mapFragment).commit();
        }
    }

    /**
     * Checks if the device is a tablet (7" or greater).
     */
    private boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * checks the orientation of the screen.
     */
    private boolean isPortrait() {

        // landscape checker
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            return false;
        }
        // else is portrait
        return true;
    }

//-------------------------------------------------------------------------------------------------

    /**
     * menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.favorite_popup:

                getFragmentManager().beginTransaction().addToBackStack("replacing").replace(R.id.main_portrait_layout, favoritesFragment).commit();

        }
        return true;
    }

    private void setToolBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar_id);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

//--------------------------------------------------------------------------------------------------------------

    /**
     *
     * @param place - Place Object to send the map Fragment
     */

    @Override
    public void changeFragments(final PlacesTable place) {

        if(isPortrait()) {
            mapFragment = new MapFragment();
            getFragmentManager().beginTransaction().addToBackStack("replacing").replace(R.id.main_portrait_layout, mapFragment).commit();
        }
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                LatLng latLng = new LatLng(place.getLat(),place.getLng());
                //update location and zoom 0 is the most far
                googleMap.addMarker(new MarkerOptions().position(latLng));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng , 10));

                //googleMap.addMarker(new MarkerOptions().position(latLng).title(location.getPlace()));

            }
        });

    }
}