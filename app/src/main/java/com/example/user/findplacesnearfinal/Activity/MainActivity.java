package com.example.user.findplacesnearfinal.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.user.findplacesnearfinal.Fragments.FragmentB;
import com.example.user.findplacesnearfinal.R;
import com.example.user.findplacesnearfinal.Fragments.SearchFragment;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        screenPositionOrder();
        setToolBar();

    }


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

            FragmentB fragmentB = new FragmentB();
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
//                favoritesFragment = new FavoritesFragment();
//                getFragmentManager().beginTransaction().addToBackStack("replacing").replace(R.id.main_layout, favoritesFragment).commit();

        }
        return true;
    }

//---------------------------------------------------------------------------------------------------------------------------------
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//
//            locationManager.removeUpdates(this);
//        }
//    }


    private void setToolBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar_id);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

}