package com.example.user.findplacesnearfinal.Helper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by user on 23/02/2018.
 */

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private RecyclerItemTouchLisener touchLisener;

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchLisener lisener) {
        super(dragDirs, swipeDirs);
        this.touchLisener = lisener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }
}
