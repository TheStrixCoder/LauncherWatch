package com.bid.launcherwatch.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class CustListView extends ListView {
    public CustListView(Context context) {
        super(context);
    }

    public CustListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }
}
