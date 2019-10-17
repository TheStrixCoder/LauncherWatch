package com.bid.launcherwatch;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class HorizontalViewPager extends ViewPager {
    private int mCurrentX;
    private int mCurrentY;
    private boolean mSwipeEnabled = true;

    public HorizontalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mSwipeEnabled) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!this.mSwipeEnabled) {
            return false;
        }
        int y = (int) event.getY();
        int x = (int) event.getX();
        if (event.getAction() == 2 && getCurrentItem() == 0 && this.mCurrentX < x) {
            return false;
        }
        this.mCurrentY = y;
        this.mCurrentX = x;
        return super.onInterceptTouchEvent(event);
    }
}
