package com.example.user.findplacesnearfinal.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
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
import android.widget.Toast;

import com.example.user.findplacesnearfinal.Adapters.MyRecyclerAdapter;
import com.example.user.findplacesnearfinal.Helper.SwipeController;
import com.example.user.findplacesnearfinal.Model.Place;
import com.example.user.findplacesnearfinal.Model.allResults;
import com.example.user.findplacesnearfinal.R;
import com.example.user.findplacesnearfinal.Service.GitHubService;
import com.example.user.findplacesnearfinal.remote.RetrofitClient;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;

public class SearchFragment extends Fragment implements LocationListener {

    private String API_KEY = "AIzaSyBwpg6a0MQuMKzVTHlwzCmhTksktUCqHf8";
    private final int REQUEST_CODE = 9;

    private LocationManager locationManager;
    private String lastKnowLoc;
    public static boolean searchWithLocationAPI = false;

    View myView;
    RecyclerView recyclerView;
    EditText searchTXT;
    Button locationBtn;
    SeekBar seekBar;

    // Required empty public constructor
    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //inflate the layout
        myView = inflater.inflate(R.layout.search_fragment, container, false);

        //initialization the LocationManager
        //check permission && GPS
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        checkLocationPermission();
        isGpsEnable();

        //initialization the RecyclerView, the location button, seekBar
        recyclerView = myView.findViewById(R.id.myList_RV);
        locationBtn = myView.findViewById(R.id.locationChangeBtn);
        seekBar = myView.findViewById(R.id.seekBar);

//-------------------------------------------------------------------------------------------------------------------

        /**
         * initialization the SeekBar ,check if there are permissions to location and for GPS
         * if i have permission to the location && GPS is on, the the gps image will be green in the beginning
         */
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            seekBar.setVisibility(View.VISIBLE);
            searchWithLocationAPI = true;
            locationBtn.setBackgroundResource(R.drawable.location_green);
        }

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // if gps image is ON (R.drawable.location_green) --> the locationButton change to OFF
                if (locationBtn.getBackground().getConstantState() == getResources().getDrawable(R.drawable.location_green).getConstantState()) {

                    locationBtn.setBackgroundResource(R.drawable.not_location);
                    seekBar.setVisibility(View.INVISIBLE);
                    searchWithLocationAPI = false;

                } else if (locationBtn.getBackground().getConstantState() == getResources().getDrawable(R.drawable.not_location).getConstantState()
                        && ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                        && locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
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

                //if user don't write nothing
                if (textEnteredByTheUser.equals("")) {

                    Toast.makeText(getActivity(), "please enter something", Toast.LENGTH_SHORT).show();
                }

                GitHubService apiService = RetrofitClient.getClient().create(GitHubService.class);

                String lastPoint = lastKnowLoc;
                String[] output = lastPoint.split(",");
                double lat = Double.valueOf(output[0]);
                double lng = Double.valueOf(output[1]);

                final LatLng latLng = new LatLng(lat,lng);

                //if user wont to search by text and don't wont to use GPS!!
                if (!searchWithLocationAPI) {

                    Toast.makeText(getActivity(), "text", Toast.LENGTH_SHORT).show();


                    Call<allResults> repos = apiService.getTextSearchResults(textEnteredByTheUser, API_KEY);

                    repos.enqueue(new Callback<allResults>() {

                        @Override
                        public void onResponse(Call<allResults> call, Response<allResults> response) {

                            Toast.makeText(getActivity(), "its works(:", Toast.LENGTH_SHORT).show();

                            /// do sum(:

                            if (response.isSuccessful()) {

                                ArrayList<Place> myData = new ArrayList<>();

                                allResults results = response.body();

                                myData.addAll(results.getResults());

                                if (response.isSuccessful() && myData.isEmpty()) {
                                    Toast.makeText(getActivity(), "No Results - no data in the array", Toast.LENGTH_SHORT).show();//TOAST MESSAGE IF WE HAVE JSON WITH ZERO RESULTS
                                } else {

                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));//LinearLayoutManager, GridLayoutManager ,StaggeredGridLayoutManagerFor defining how single row of recycler view will look .  LinearLayoutManager shows items in horizontal or vertical scrolling list. Don't confuse with type of layout you use in xml
                                    //setting txt adapter
                                    RecyclerView.Adapter Adapter = new MyRecyclerAdapter(myData, getActivity(), latLng);
                                    recyclerView.setAdapter(Adapter);

                                    SwipeController swipeController = new SwipeController();
                                    ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
                                    itemTouchhelper.attachToRecyclerView(recyclerView);
                                    Adapter.notifyDataSetChanged();//refresh
                                }

                            }

                        }


                        @Override
                        public void onFailure(Call<allResults> call, Throwable t) {

                            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {

                    Toast.makeText(getActivity(), "nearBy", Toast.LENGTH_SHORT).show();

                    String radius = "1000";
                    Toast.makeText(getActivity(), lastKnowLoc, Toast.LENGTH_SHORT).show();


                    Call<allResults> repos = apiService.getNearbyResults(lastKnowLoc, radius, textEnteredByTheUser, API_KEY);

                    repos.enqueue(new Callback<allResults>() {

                        @Override
                        public void onResponse(Call<allResults> call, Response<allResults> response) {

                            Toast.makeText(getActivity(), "its works(:", Toast.LENGTH_SHORT).show();

                            /// do sum(:

                            if (response.isSuccessful()) {

                                ArrayList<Place> myData = new ArrayList<>();

                                allResults results = response.body();

                                myData.addAll(results.getResults());

                                if (response.isSuccessful() && myData.isEmpty()) {
                                    Toast.makeText(getActivity(), "No Results - no data in the array", Toast.LENGTH_SHORT).show();//TOAST MESSAGE IF WE HAVE JSON WITH ZERO RESULTS
                                } else {

                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));//LinearLayoutManager, GridLayoutManager ,StaggeredGridLayoutManagerFor defining how single row of recycler view will look .  LinearLayoutManager shows items in horizontal or vertical scrolling list. Don't confuse with type of layout you use in xml
                                    //setting txt adapter
                                    RecyclerView.Adapter Adapter = new MyRecyclerAdapter(myData, getActivity(), latLng);
                                    recyclerView.setAdapter(Adapter);

                                    SwipeController swipeController = new SwipeController();
                                    ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
                                    itemTouchhelper.attachToRecyclerView(recyclerView);
                                    Adapter.notifyDataSetChanged();//refresh
                                }

                            }

                        }

                        @Override
                        public void onFailure(Call<allResults> call, Throwable t) {

                        }
                    });

                }

            }

        });

//--------------------------------------------------------------------------------------------------------------------
        return myView;
    }

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

    //get user enter text to search
    private String getUserText() {

        searchTXT = myView.findViewById(R.id.searchtext_ET);
        return searchTXT.getText().toString();
    }

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

    @SuppressLint("MissingPermission")
    private void getLocation() {

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        Location myLocation = ((LocationManager) getActivity().getSystemService(LOCATION_SERVICE)).getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (myLocation == null) {
            Toast.makeText(getActivity(), "location not available :(", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getActivity(), "lat: " + myLocation.getLatitude() + " lon:" + myLocation.getLongitude(), Toast.LENGTH_SHORT).show();
            lastKnowLoc = myLocation.getLatitude() + "," + myLocation.getLongitude();

        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {

            case REQUEST_CODE: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    {
                        getLocation();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    // check if gps enable, if not show alert dialog and intent to open GPS
    private boolean isGpsEnable() {

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
