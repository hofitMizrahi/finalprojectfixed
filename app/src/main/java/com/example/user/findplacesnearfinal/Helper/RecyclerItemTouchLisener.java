package com.example.user.findplacesnearfinal.Helper;

import android.support.v7.widget.RecyclerView;

/**
 * Created by user on 23/02/2018.
 */

interface RecyclerItemTouchLisener {

    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
}
