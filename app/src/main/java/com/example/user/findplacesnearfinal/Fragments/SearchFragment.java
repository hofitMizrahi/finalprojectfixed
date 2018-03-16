package com.example.user.findplacesnearfinal.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
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
import com.example.user.findplacesnearfinal.Helper.SwipeController;
import com.example.user.findplacesnearfinal.Model.OpenHours;
import com.example.user.findplacesnearfinal.Model.Place;
import com.example.user.findplacesnearfinal.Model.allResults;
import com.example.user.findplacesnearfinal.R;
import com.example.user.findplacesnearfinal.Service.GitHubService;
import com.example.user.findplacesnearfinal.DataBase.PlacesTable;
import com.example.user.findplacesnearfinal.remote.RetrofitClient;
import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarContext;
import com.xw.repo.BubbleSeekBar;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;

public class SearchFragment extends Fragment implements LocationListener {

    private String API_KEY = "AIzaSyBwpg6a0MQuMKzVTHlwzCmhTksktUCqHf8";
    private final int REQUEST_CODE = 9;

    private LocationManager locationManager;
    Location lastKnownLocation;
    private String lastKnowLoc;
    public static boolean searchWithLocationAPI = false;

    View myView;
    RecyclerView recyclerView;
    EditText searchTXT;
    Button locationBtn;
    SeekBar seekBar;
    int myProgress;

    ArrayList<PlacesTable> allPlaces;

    // Required empty public constructor
    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //inflate the layout
        myView = inflater.inflate(R.layout.search_fragment, container, false);

        SugarContext.init(getActivity());

        //initialization the LocationManager
        //check permission && GPS
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        //initialization the RecyclerView, the location button, seekBar
        recyclerView = myView.findViewById(R.id.myList_RV);
        locationBtn = myView.findViewById(R.id.locationChangeBtn);

        seekBar = myView.findViewById(R.id.mySeekBar_id);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                Log.i("Progress", String.valueOf(progress) );
                myProgress = progress;
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
            searchWithLocationAPI = true;
            locationBtn.setBackgroundResource(R.drawable.location_green);
            setRecyclerFromDB();
        }else {

            isGpsEnable();
            checkLocationPermission();
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
                String textEnteredByTheUser = getUserText();
                GitHubService apiService = RetrofitClient.getClient().create(GitHubService.class);
                Call<allResults> repos = null;

                //if user don't write nothing
                if (textEnteredByTheUser.equals("")) {

                    Toast.makeText(getActivity(), "please enter something", Toast.LENGTH_SHORT).show();

                } else {

                    //if user wont to search by text and don't wont to use GPS!!
                    if (!searchWithLocationAPI) {

                        Toast.makeText(getActivity(), "text", Toast.LENGTH_SHORT).show();
                        repos = apiService.getTextSearchResults(textEnteredByTheUser, API_KEY);

                    } else {

                        Toast.makeText(getActivity(), "nearBy", Toast.LENGTH_SHORT).show();
                        String radius = String.valueOf(myProgress * 1000);

                        repos = apiService.getNearbyResults(lastKnowLoc, radius, textEnteredByTheUser, API_KEY);
                    }

                    repos.enqueue(new Callback<allResults>() {

                        @Override
                        public void onResponse(Call<allResults> call, Response<allResults> response) {

                            Toast.makeText(getActivity(), "its works(:", Toast.LENGTH_SHORT).show();
                            allResults results = response.body();

                            if (results.getResults().isEmpty()) {

                                //if i have zero results
                                Toast.makeText(getActivity(), "No Results - no data in the array", Toast.LENGTH_SHORT).show();

                                //i have results in the array
                            } else {

                                PlacesTable.deleteAll(PlacesTable.class);

                                for (int i = 0; i < results.getResults().size(); i++) {

                                    Place place = results.getResults().get(i);
                                    String photo ="";

                                    if(place.getPhotos() == null){
                                        photo = "";
                                    }else {

                                        photo = place.getPhotos().get(0).getPhoto_reference();
                                    }

                                    String isOpen = null;

                                    if (place.getOpening_hours() == null) {

                                        isOpen = null;

                                    } else if(place.getOpening_hours() != null && place.getOpening_hours().isOpen_now() == true) {

                                        isOpen = "true";
                                    }else {
                                        isOpen = "false";
                                    }

                                    String placeAddress = "";
                                    if(place.getFormatted_address() != null){

                                        placeAddress = place.getFormatted_address();
                                    }else if(place.getVicinity() != null){

                                        placeAddress = place.getVicinity();
                                    }


                                    PlacesTable sugarPlaceTable = new PlacesTable(place.getGeometry().getLocation().getLat(),
                                            place.getGeometry().getLocation().getLng(),
                                            place.getIcon(), place.getName(), isOpen,
                                            photo, place.getRating(), place.getTypes(),
                                            placeAddress
                                    );

                                    sugarPlaceTable.save();
                                }
                            }

                            allPlaces = (ArrayList<PlacesTable>) PlacesTable.listAll(PlacesTable.class);

                            setRecyclerFromDB();

                        }

                        @Override
                        public void onFailure(Call<allResults> call, Throwable t) {

                            Toast.makeText(getActivity(), "fail call", Toast.LENGTH_SHORT).show();

                        }

                    });
                }
            }

        });

        return myView;
    }

//--------------------------------------------------------------------------------------------------------------------

    /**
     * set the RecyclerView From DataBase
     */
    private void setRecyclerFromDB() {

        LatLng latLng = null;

        if(lastKnownLocation != null){

            latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //setting txt adapter
        RecyclerView.Adapter Adapter = new MyRecyclerAdapter(getActivity(), latLng);
        recyclerView.setAdapter(Adapter);

        SwipeController swipeController = new SwipeController();
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);
        Adapter.notifyDataSetChanged(); //refresh
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
            lastKnowLoc = location.getLatitude() + "," + location.getLongitude();
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
     * get current location method
     */
    @SuppressLint("MissingPermission")
    private void getLocation() {

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        lastKnownLocation = ((LocationManager) getActivity().getSystemService(LOCATION_SERVICE)).getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (lastKnownLocation == null) {
            Toast.makeText(getActivity(), "location not available :(", Toast.LENGTH_SHORT).show();
            lastKnownLocation = ((LocationManager) getActivity().getSystemService(LOCATION_SERVICE)).getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        }

        if(lastKnownLocation != null){
            Toast.makeText(getActivity(), "lat: " + lastKnownLocation.getLatitude() + " lon:" + lastKnownLocation.getLongitude(), Toast.LENGTH_SHORT).show();
            lastKnowLoc = lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude();

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
                    REQUEST_CODE);

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

            case REQUEST_CODE: {

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

}
