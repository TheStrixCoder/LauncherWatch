package com.bid.launcherwatch.online;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class BounceListView extends ListView {
    private Context mContext;
    private int mMaxYOverscrollDistance;

    public BounceListView(Context context) {
        super(context);
        this.mContext = context;
        initBounceListView();
    }

    public BounceListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initBounceListView();
    }

    public BounceListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initBounceListView();
    }

    private void initBounceListView() {
        this.mMaxYOverscrollDistance = (int) (50.0f * this.mContext.getResources().getDisplayMetrics().density);
    }

    /* access modifiers changed from: protected */
    public boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, this.mMaxYOverscrollDistance, isTouchEvent);
    }
}
