package com.example.user.findplacesnearfinal.Fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.findplacesnearfinal.R;
import com.google.android.gms.maps.MapFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentB extends Fragment {


    public FragmentB() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_and_info_layout, container, false);

        MapFragment mapFragment = new MapFragment();
        getFragmentManager().beginTransaction().addToBackStack("replacing").replace(R.id.map_layout, mapFragment).commit();

        return v;
    }

}
