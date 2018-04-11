package com.example.user.findplacesnearfinal.Fragments;


import android.graphics.Canvas;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.findplacesnearfinal.Adapters.FavoritesAdapter;
import com.example.user.findplacesnearfinal.Adapters.MyRecyclerAdapter;
import com.example.user.findplacesnearfinal.Helper.SwipeController;
import com.example.user.findplacesnearfinal.R;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;

public class FavoritesFragment extends Fragment {

    RecyclerView favoritesRecyclerView;

    public FavoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_favorites, container, false);

        favoritesRecyclerView = myView.findViewById(R.id.favoriet_RV);

        favoritesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        //setting txt adapter
        RecyclerView.Adapter Adapter = new FavoritesAdapter(getActivity());
        favoritesRecyclerView.setAdapter(Adapter);

        SwipeController swipeController = new SwipeController();
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(favoritesRecyclerView);
        Adapter.notifyDataSetChanged(); //refresh



        return myView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

}
