package com.bid.launcherwatch;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.support.wearable.myView.WearableListView.Item;

public class AppListItemLayout extends LinearLayout implements Item {
    private ImageView mCircle;
    private final float mFadedAlpha;
    private TextView mName;
    private float mScale;

    public AppListItemLayout(Context context) {
        this(context, null);
    }

    public AppListItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppListItemLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mFadedAlpha = 0.4f;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mCircle = (ImageView) findViewById(R.id.app_icon);
        this.mName = (TextView) findViewById(R.id.app_name);
    }

    public float getProximityMinValue() {
        return 1.0f;
    }

    public float getProximityMaxValue() {
        return 1.6f;
    }

    public float getCurrentProximityValue() {
        return this.mScale;
    }

    public void setScalingAnimatorValue(float scale) {
        this.mScale = scale;
        this.mCircle.setScaleX(scale);
        this.mCircle.setScaleY(scale);
    }

    public void onScaleUpStart() {
        this.mName.setAlpha(1.0f);
    }

    public void onScaleDownStart() {
        this.mName.setAlpha(0.4f);
    }
}
