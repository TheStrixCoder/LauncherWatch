package com.bid.launcherwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

public class BatteryPercentageView extends TextView {
    private int[] mColors;
    private final BroadcastReceiver mIntentReceiver;

    /* renamed from: com.mediatek.watchapp.BatteryPercentageView$1 */
    class C01001 extends BroadcastReceiver {
        C01001() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.BATTERY_CHANGED")) {
                int level = (int) ((((float) intent.getIntExtra("level", 0)) * 100.0f) / ((float) intent.getIntExtra("scale", 100)));
                BatteryPercentageView.this.setText(BatteryPercentageView.this.getBatteryPercentage(level));
                BatteryPercentageView.this.setTextColor(BatteryPercentageView.this.getColorForLevel(level));
            }
        }
    }

    public BatteryPercentageView(Context context) {
        this(context, null);
    }

    public BatteryPercentageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BatteryPercentageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mIntentReceiver = new C01001();
        init(context);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.BATTERY_CHANGED");
        Intent sticky = getContext().registerReceiver(this.mIntentReceiver, filter);
        if (sticky != null) {
            this.mIntentReceiver.onReceive(getContext(), sticky);
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(this.mIntentReceiver);
    }

    private void init(Context context) {
        Resources res = context.getResources();
        TypedArray levels = res.obtainTypedArray(R.array.batterymeter_color_levels);
        TypedArray colors = res.obtainTypedArray(R.array.batterymeter_color_values);
        int mNum = levels.length();
        this.mColors = new int[(mNum * 2)];
        for (int i = 0; i < mNum; i++) {
            this.mColors[i * 2] = levels.getInt(i, 0);
            this.mColors[(i * 2) + 1] = colors.getColor(i, 0);
        }
        levels.recycle();
        colors.recycle();
    }

    private String getBatteryPercentage(int mLevel) {
        return String.valueOf(mLevel) + "%";
    }

    private int getColorForLevel(int percent) {
        int color = 0;
        for (int i = 0; i < this.mColors.length; i += 2) {
            int thresh = this.mColors[i];
            color = this.mColors[i + 1];
            if (percent <= thresh) {
                return color;
            }
        }
        return color;
    }
}
