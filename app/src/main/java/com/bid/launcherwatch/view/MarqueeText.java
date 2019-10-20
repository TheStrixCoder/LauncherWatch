package com.bid.launcherwatch.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeText extends TextView {
    public MarqueeText(Context paramContext) {
        super(paramContext);
    }

    public MarqueeText(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public MarqueeText(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    public boolean isFocused() {
        return true;
    }

    protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect) {
        super.onFocusChanged(paramBoolean,paramInt,paramRect);
    }
}
