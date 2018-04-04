package com.example.user.findplacesnearfinal.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.user.findplacesnearfinal.SugarDataBase.FavorietsDB;
import com.example.user.findplacesnearfinal.Fragments.FavoritesFragment;
import com.example.user.findplacesnearfinal.Fragments.InfoFragment;
import com.example.user.findplacesnearfinal.Fragments.SearchFragment;
import com.example.user.findplacesnearfinal.Fragments.MyPrefsFragment;
import com.example.user.findplacesnearfinal.R;
import com.example.user.findplacesnearfinal.Service.MyFragmentChanger;
import com.example.user.findplacesnearfinal.SugarDataBase.PlacesDB;
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

    BroadcastReceiver connectedBroadcast;
    BroadcastReceiver disconnectedBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //refresh sugar database
        SugarContext.init(this);

        //connected to Broadcast to listen the mobile Power
        connectedOrDisConnectedPowerChanging();

        //settings for UI
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //set up the custom tool bar
        setToolBar();

        //initialize the fragment
        mapFragment = new MapFragment();
        favoritesFragment = new FavoritesFragment();

        //set up the screen
        screenPositionOrder();

    }

    private void connectedOrDisConnectedPowerChanging() {

        connectedBroadcast = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                Toast.makeText(context, "Power Connected", Toast.LENGTH_SHORT).show();
            }
        };

        registerReceiver(connectedBroadcast, new IntentFilter(Intent.ACTION_POWER_CONNECTED) );

        disconnectedBroadcast = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                Toast.makeText(context, "Power Disconnected", Toast.LENGTH_SHORT).show();
            }
        };

        registerReceiver(disconnectedBroadcast, new IntentFilter(Intent.ACTION_POWER_DISCONNECTED) );
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
            InfoFragment fragmentB = new InfoFragment();
            getFragmentManager().beginTransaction().addToBackStack("replacing").replace(R.id.search_tablet_layout, searchFragment).commit();

            getFragmentManager().beginTransaction().addToBackStack("replacing").replace(R.id.tablet_map_layout, fragmentB).commit();
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
     * menu && toolBar
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

                changeToFavoritesFragment();
                break;

            case R.id.closeApp:

                finish();
                break;

            case R.id.showSettings:
                getFragmentManager().beginTransaction().addToBackStack("replacing")
                        .replace(R.id.main_portrait_layout, new MyPrefsFragment()).commit();
                break;
            case R.id.delete_favorites:
                FavorietsDB.deleteAll(FavorietsDB.class);
                break;
        }
        return true;
    }

    private void setToolBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar_id);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

//--------------------------------------------------------------------------------------------------------------

    @Override
    public void changeFragments(final PlacesDB place) {

        if(isPortrait()) {
            InfoFragment fragmentB = new InfoFragment(place);

            mapFragment = new MapFragment();
            getFragmentManager().beginTransaction().addToBackStack("replacing").replace(R.id.main_portrait_layout, fragmentB).commit();
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

    @Override
    public void changeToFavoritesFragment() {

        getFragmentManager().beginTransaction().addToBackStack("replacing").replace(R.id.main_portrait_layout, favoritesFragment).commit();

    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(connectedBroadcast);
        unregisterReceiver(disconnectedBroadcast);
    }
}