package com.example.user.findplacesnearfinal.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.findplacesnearfinal.CalculateDistance;
import com.example.user.findplacesnearfinal.Model.Place;
import com.example.user.findplacesnearfinal.R;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.user.findplacesnearfinal.Fragments.SearchFragment.searchWithLocationAPI;

public class MyRecyclerAdapter extends RecyclerView.Adapter <MyRecyclerAdapter.myViewHolder> {

    ArrayList<Place> placeArrayList;
    Context context;
    LatLng latLng;

    public MyRecyclerAdapter(ArrayList<Place> placeArrayList, Context context, LatLng latLng) {
        this.placeArrayList = placeArrayList;
        this.context = context;
        this.latLng = latLng;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.single_item_model, null);//getContext refers to get value of context variable
        myViewHolder viewHolder = new myViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(myViewHolder singleItem, int position) {

        Place place = placeArrayList.get(position);
        singleItem.bindMyCityData(place);
    }

    @Override
    public int getItemCount() {
        return placeArrayList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{

        View holderView;

         public myViewHolder(View itemView) {
         super(itemView);

         this.holderView = itemView;
        }

        @SuppressLint("ResourceType")
        public void bindMyCityData(Place place) {

            // title
            TextView title = holderView.findViewById(R.id.title_TV);
            title.setText(place.getName());

//--------------------------------------------------------------------------------------------------

            // address
            TextView address = holderView.findViewById(R.id.address_TV);

            String PlaceAddress = null;

            //if i use the textSearch API
            if(!searchWithLocationAPI){
                PlaceAddress = place.getFormatted_address();

            //if i use nearBy API
            }else {
                PlaceAddress = place.getVicinity();
            }

            if(!PlaceAddress.contains(",")){

                address.setText(PlaceAddress);
            }else{

                String[] parts = PlaceAddress.split(",", 2);
                String part1 = parts[0]; // address
                String part2 = parts[1]; // country

                address.setText(part1 + "\n" + part2);
            }
//--------------------------------------------------------------------------------------------------

            // open or close - boolean
            TextView openOrCloseSing = holderView.findViewById(R.id.openOrCloseSing);

            //if don't have open hours array at all
            if(place.getOpening_hours() == null){

                // don't show this text
                openOrCloseSing.setVisibility(View.INVISIBLE);
            }else if (place.getOpening_hours() != null && place.getOpening_hours().isOpen_now() == true) {

                openOrCloseSing.setBackgroundResource(R.drawable.open_shape);
                openOrCloseSing.setText(R.string.open);
            } else if (place.getOpening_hours() != null && place.getOpening_hours().isOpen_now() == false) {
                openOrCloseSing.setText(R.string.closed);
                openOrCloseSing.setBackgroundResource(R.drawable.close_shape);
            }



            //image resource
            ImageView imageView = holderView.findViewById(R.id.imageView);

            //check if there is any image resource in the Photo list array
            if(place.getPhotos() != null && place.getPhotos().get(0).getPhoto_reference() != null){

                String reference = place.getPhotos().get(0).getPhoto_reference();


                String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + reference + "&key=AIzaSyBwpg6a0MQuMKzVTHlwzCmhTksktUCqHf8";
                Picasso.with(context)
                        .load(url)
                        .resize(90, 90)
                        .centerCrop()
                        .into(imageView);
            }

//-----------------------------------------------------------------------------------------------------------------------------------------------------------

            // km || miles
            TextView meters = holderView.findViewById(R.id.KM_TV);

            LatLng endP = new LatLng(place.getGeometry().getLocation().getLat(), place.getGeometry().getLocation().getLng());

            CalculateDistance calculateDistance = new CalculateDistance();
            double distance = calculateDistance.getDistance(latLng, endP);

            String allmeters = String.valueOf(round(distance, 1));

            meters.setText(allmeters);

            }
        }

        private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

}
