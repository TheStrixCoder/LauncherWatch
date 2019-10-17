package com.bid.launcherwatch;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

abstract class WiiteWatchFace extends View {
    /* access modifiers changed from: 0000 */
    public abstract void onTouch(float f, float f2);

    public WiiteWatchFace(Context context) {
        this(context, null);
    }

    public WiiteWatchFace(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WiiteWatchFace(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public float distance(float x1, float y1, float x2, float y2) {
        float x = x2 - x1;
        float y = y2 - y1;
        return (float)Math.sqrt((x * x) + (y * y));
    }
}
