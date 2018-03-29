package com.example.user.findplacesnearfinal.Fragments;


import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.findplacesnearfinal.DataBase.PlacesTable;
import com.example.user.findplacesnearfinal.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {

    View v;

    PlacesTable place;

    public InfoFragment(){

    }

    @SuppressLint("ValidFragment")
    public InfoFragment(PlacesTable place) {
        // Required empty public constructor

        this.place = place;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.map_and_info_layout, container, false);

        setMap();

        TextView name_info = v.findViewById(R.id.name_info);
        name_info.setText(place.getName());

        ImageView carImage = v.findViewById(R.id.carImage_info);
        carImage.setOnClickListener((View view) -> {
            Toast.makeText(getActivity(), "car click", Toast.LENGTH_SHORT).show();

            try
            {
                // Launch Waze to look for Hawaii:
                String url = "https://waze.com/ul?q=66%20Acacia%20Avenue&ll= " + place.getLat() + "," + place.getLng() + "&navigate=yes";
                Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(url));
                startActivity( intent );
            }
            catch (ActivityNotFoundException ex)
            {
                // If Waze is not installed, open it in Google Play:
                Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                startActivity(intent);
            }
        });

        //image resource
        ImageView imageView = v.findViewById(R.id.image_info);

        //check if there is any image resource in the Photo list array
        if (!place.getPhoto_reference().equals("")) {

            String reference = place.getPhoto_reference();

            String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                        + reference
                        + "&key=AIzaSyBwpg6a0MQuMKzVTHlwzCmhTksktUCqHf8";

            Picasso.with(getActivity())
                    .load(url)
                    .resize(90, 90)
                    .centerCrop()
                    .into(imageView);
        }

        imageView.setOnClickListener((View view) ->{

            //
        });

        TextView addressInfo = v.findViewById(R.id.address_info);
        addressInfo.setText(place.getAddress());

        final SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout) v.findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        return v;
    }

    private void setMap() {

        MapFragment mapFragment = new MapFragment();
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                LatLng latLng = new LatLng(place.getLat(),place.getLng());
                //update location and zoom 0 is the most far
                googleMap.addMarker(new MarkerOptions().position(latLng).title(place.getAddress()));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng , 10));

            }
        });
        getFragmentManager().beginTransaction().addToBackStack("replacing").replace(R.id.map_layout, mapFragment).commit();
    }

    @Override
    public void onPause() {
        super.onPause();

    }
}
