package com.example.user.findplacesnearfinal.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.findplacesnearfinal.DataBase.FavorietsTableDB;
import com.example.user.findplacesnearfinal.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.myViewHolder> {

    Context context;
    ArrayList<FavorietsTableDB> allFavoriets;

    public FavoritesAdapter(Context context) {
        this.context = context;
        allFavoriets = (ArrayList<FavorietsTableDB>) FavorietsTableDB.listAll(FavorietsTableDB.class);

    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.single_favoriet_item, null);
        myViewHolder viewHolder = new myViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {

        FavorietsTableDB myFavorites = allFavoriets.get(position);
        holder.bindMyCityData(myFavorites);
    }

    @Override
    public int getItemCount() {
        return allFavoriets.size();
    }


    public class  myViewHolder extends RecyclerView.ViewHolder{

        View v;

        public myViewHolder(View itemView) {
            super(itemView);

            v = itemView;
        }


        public void bindMyCityData(FavorietsTableDB favorites) {

            ImageView favoriteImage = v.findViewById(R.id.favorite_IV);

            //check if there is any image resource in the Photo list array
            if (!favorites.getPhoto_reference().equals("")) {

                String reference = favorites.getPhoto_reference();

                String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + reference + "&key=AIzaSyBwpg6a0MQuMKzVTHlwzCmhTksktUCqHf8";
                Picasso.with(context)
                        .load(url)
                        .resize(90, 90)
                        .centerCrop()
                        .into(favoriteImage);
            }

            ((TextView) v.findViewById(R.id.title_favorites_TV)).setText(favorites.getName());

        }
    }
}
