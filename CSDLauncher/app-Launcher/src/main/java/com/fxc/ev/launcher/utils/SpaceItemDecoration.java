package com.fxc.ev.launcher.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    public SpaceItemDecoration() {
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % 2; // item column

        /*if (column == 0) {
            outRect.right = 6;
        } else {
            outRect.left = 6;
            outRect.right = 12;
        }*/
        outRect.bottom = 18;
    }
}
