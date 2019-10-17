package com.android.support.wearable.myView;

import android.content.Context;
import android.support.wearable.view.WearableRecyclerView;
import android.util.AttributeSet;

public class WearableRecyclerViewCustom extends WearableRecyclerView {
    public WearableRecyclerViewCustom(Context context) {
        super(context, (AttributeSet) null);
    }

    public WearableRecyclerViewCustom(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public boolean fling(int velocityX, int velocityY) {
        return super.fling(velocityX, (int) (((float) velocityY) * 0.7f));
    }
}
