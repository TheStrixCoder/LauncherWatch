package com.bid.launcherwatch.cleananimation;

import android.content.Context;
import android.util.TypedValue;

public class SizeUtil {
    public static float Dp2Px(Context context, float dpi) {
        return TypedValue.applyDimension(1, dpi, context.getResources().getDisplayMetrics());
    }

    public static float Sp2Px(Context context, float sp) {
        return TypedValue.applyDimension(2, sp, context.getResources().getDisplayMetrics());
    }
}