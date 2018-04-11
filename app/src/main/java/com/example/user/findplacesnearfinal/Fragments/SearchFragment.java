package com.example.user.findplacesnearfinal.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.findplacesnearfinal.Adapters.MyRecyclerAdapter;
import com.example.user.findplacesnearfinal.helpClasses.CallToRetrofit;
import com.example.user.findplacesnearfinal.Helper.SwipeController;
import com.example.user.findplacesnearfinal.R;
import com.example.user.findplacesnearfinal.SugarDataBase.PlacesDB;
import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarContext;

import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;

public class SearchFragment extends Fragment implements LocationListener {

    private final int PERMISSION_REQUEST_CODE = 9;

    private LocationManager locationManager;
    public static Location lastKnownLocation;
    private String lastKnowStringLoc;
    private boolean searchWithLocationAPI = false;

    View myView;
    RecyclerView recyclerView;
    Button locationBtn;
    EditText searchTXT;
    SeekBar seekBar;
    int progressBarValue;
    TextView seekBarProgressText;

    private Boolean isPrefUseKm;

    private ArrayList<PlacesDB> allPlaces;
    private MyRecyclerAdapter adapter;

    // Required empty public constructor
    public SearchFragment() {
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //inflate the layout
        myView = inflater.inflate(R.layout.search_fragment, container, false);

        SugarContext.init(getActivity());

        //initialization the LocationManager
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        //get the user miles or Km pref
        getKmOrMilesFromPreference();

        //initialization the RecyclerView, the location button, seekBar
        recyclerView = myView.findViewById(R.id.myList_RV);
        locationBtn = myView.findViewById(R.id.locationChangeBtn);
        seekBar = myView.findViewById(R.id.mySeekBar_id);
        seekBarProgressText = myView.findViewById(R.id.progress_forseekbar_TV);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                Log.i("Progress", String.valueOf(progress));
                progressBarValue = progress+5; // 5 - 30
                seekBarProgressText.setText(String.valueOf(progressBarValue));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


//-------------------------------------------------------------------------------------------------------------------

        /**
         * check if i have permission to the location && GPS is on, the gps image will be green in the beginning
         */
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            getLocation();
            seekBar.setVisibility(View.VISIBLE);
            seekBarProgressText.setVisibility(View.VISIBLE);
            searchWithLocationAPI = true;
            locationBtn.setBackgroundResource(R.drawable.location_green);

        } else {

            isGpsEnable();
            checkLocationPermission();
        }

//--------------------------------------------------------------------------------------------------------

        try{

            getLocation();

        }catch (Exception ee){

            ee.printStackTrace();
        }

        setRecyclerFromDB();

//--------------------------------------------------------------------------------------------------------

        /**
         *  user click on locationBtn to change it
         */
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // if gps image is ON (R.drawable.location_green) --> the locationButton change to OFF
                if (locationBtn.getBackground().getConstantState()
                        == getResources().getDrawable(R.drawable.location_green)
                        .getConstantState()) {

                    locationBtn.setBackgroundResource(R.drawable.not_location);
                    seekBar.setVisibility(View.INVISIBLE);
                    seekBarProgressText.setVisibility(View.INVISIBLE);

                    searchWithLocationAPI = false;

                    // if user wont to set the Gps btn to ON - gps need to be Enable and also the permission check are true
                } else if (locationBtn.getBackground().getConstantState()
                        == getResources().getDrawable(R.drawable.not_location)
                        .getConstantState()
                        && ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                        && locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    getLocation();
                    locationBtn.setBackgroundResource(R.drawable.location_green);
                    seekBar.setVisibility(View.VISIBLE);
                    seekBarProgressText.setVisibility(View.VISIBLE);
                    searchWithLocationAPI = true;

                } else {

                    checkLocationPermission();
                    isGpsEnable();
                }
            }
        });

//--------------------------------------------------------------------------------------------------------------------

        /**
         * when the user click on the search button, start retrofit connection
         * Depends if the location service is on or off
         */
        ImageView imageView = myView.findViewById(R.id.search_image);
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // take the text that the user enter to EditText
                String userText = getUserText();
                if(lastKnowStringLoc != null){

                    //call retrofit and update recyclerView
                    CallToRetrofit retrofitCall = new CallToRetrofit(getActivity(), userText, progressBarValue,
                            lastKnowStringLoc, searchWithLocationAPI,
                            lastKnownLocation, recyclerView, adapter);
                    retrofitCall.startCallRetrofitApi();
                }else {

                    checkLocationPermission();
                    isGpsEnable();
                }
            }

        });

        return myView;
    }

//*******************************************************************************************************************

    /**
     * set the RecyclerView From DataBase
     */
    private void setRecyclerFromDB() {

        LatLng latLng = null;

        if(lastKnownLocation != null) {

            latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

        }else {

            // no location found
            latLng = new LatLng(0, 0);

        }

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            //setting adapter
            adapter = new MyRecyclerAdapter(getActivity(), latLng);
            recyclerView.setAdapter(adapter);

            SwipeController swipeController = new SwipeController();
            ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
            itemTouchhelper.attachToRecyclerView(recyclerView);
            adapter.notifyDataSetChanged(); //refresh
    }

//------------------------------------------------------------------------------------------------------------------

    //get user enter text to search
    private String getUserText() {

        searchTXT = myView.findViewById(R.id.searchtext_ET);
        return searchTXT.getText().toString();
    }

    /**
     * OnResume && OnPause
      */

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            getLocation();
            locationBtn.setBackgroundResource(R.drawable.location_green);
            seekBar.setVisibility(View.VISIBLE);
            searchWithLocationAPI = true;

        } else {
            locationBtn.setBackgroundResource(R.drawable.not_location);
            seekBar.setVisibility(View.INVISIBLE);
            searchWithLocationAPI = false;
        }

        setRecyclerFromDB();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.removeUpdates(this);
        }
    }

//------------------------------------------------------------------------------------------------------------

    /**
     * implement methods From LocationListener Object
     */
    @Override
    public void onLocationChanged(Location location) {

        if (location == null) {

        } else {
            lastKnowStringLoc = location.getLatitude() + "," + location.getLongitude();
            Log.i("LOC", "lat: " + location.getLatitude() + " lon:" + location.getLongitude());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            getLocation();
            locationBtn.setBackgroundResource(R.drawable.location_green);
            seekBar.setVisibility(View.VISIBLE);
            searchWithLocationAPI = true;
        }
    }

    @Override
    public void onProviderDisabled(String provider) {

        locationBtn.setBackgroundResource(R.drawable.not_location);
        seekBar.setVisibility(View.INVISIBLE);
        searchWithLocationAPI = false;
    }

//---------------------------------------------------------------------------------------------------------------------------------

    /**
     * get current location method from GPS || Network
     */
    @SuppressLint("MissingPermission")
    private void getLocation() {

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        lastKnownLocation = ((LocationManager) getActivity().getSystemService(LOCATION_SERVICE)).getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (lastKnownLocation == null) {
            Toast.makeText(getActivity(), "location not available with gps:(", Toast.LENGTH_SHORT).show();
            lastKnownLocation = ((LocationManager) getActivity().getSystemService(LOCATION_SERVICE)).getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        }

        if(lastKnownLocation != null){
            Toast.makeText(getActivity(), "lat: " + lastKnownLocation.getLatitude() + " lon:" + lastKnownLocation.getLongitude(), Toast.LENGTH_SHORT).show();
            lastKnowStringLoc = lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude();

        }

        if(lastKnownLocation == null){
            Toast.makeText(getActivity(), "location don't found", Toast.LENGTH_SHORT).show();
        }
    }

//-------------------------------------------------------------------------------------------------------------------------

    /**
     * location Permission
     */
    public boolean checkLocationPermission() {

        // if i don't have permission
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);

            return false;

        } else {

            getLocation();
            return true;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {

            case PERMISSION_REQUEST_CODE: {

                // if request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    {
                        // if user have GPS provider Enable
                        if(isGpsEnable()){

                            getLocation();
                        }
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

//-----------------------------------------------------------------------------------------------------------------

    /**
     * check if gps enable, if not show alert dialog and intent to open GPS
     */
    private boolean isGpsEnable() {

        // if i don't have gps
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getActivity(), "GPS is disable!", Toast.LENGTH_LONG).show();

            // ask for gps and sent to it by intent
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    getActivity());
            alertDialogBuilder
                    .setMessage("GPS is disabled in your device. Enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Enable GPS",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    Intent callGPSSettingIntent = new Intent(
                                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    getActivity().startActivity(callGPSSettingIntent);
                                    dialog.cancel();
                                }
                            });
            alertDialogBuilder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
            return false;
        }
        Toast.makeText(getActivity(), "GPS is Enable!", Toast.LENGTH_LONG).show();
        return true;
    }

//---------------------------------------------------------------------------------------------------------------------

    //get the value from shearPreference
    public void  getKmOrMilesFromPreference(){

        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        //get value from SharedPrefs
        String showInList = sharedPreferences.getString("list_preference", "list");

        if(showInList.equals("Km")) {

            Toast.makeText(getActivity(), "Km", Toast.LENGTH_SHORT).show();
            isPrefUseKm = true;
        }
        else if(showInList.equals("Miles"))
        {
            Toast.makeText(getActivity(), "miles", Toast.LENGTH_SHORT).show();
            isPrefUseKm = false;

        }else {

            Toast.makeText(getActivity(), "force KM", Toast.LENGTH_SHORT).show();
            isPrefUseKm = true;
        }

    }

}
