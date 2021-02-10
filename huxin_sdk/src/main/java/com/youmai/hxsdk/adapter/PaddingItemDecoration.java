package com.youmai.hxsdk.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by colin on 2016/8/29.
 */
public class PaddingItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpace;

    public PaddingItemDecoration(int space) {
        mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = mSpace;
        outRect.top = mSpace;
        outRect.right = mSpace;
        outRect.bottom = mSpace;
    }
}

