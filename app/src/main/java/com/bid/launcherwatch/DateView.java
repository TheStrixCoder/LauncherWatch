package com.bid.launcherwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateView extends TextView {
    private final Date mCurrentTime = new Date();
    /* access modifiers changed from: private */
    public SimpleDateFormat mDateFormat;
    private String mFmt = "eeeMMMMd";
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.TIME_TICK".equals(action) || "android.intent.action.TIME_SET".equals(action) || "android.intent.action.TIMEZONE_CHANGED".equals(action) || "android.intent.action.LOCALE_CHANGED".equals(action)) {
                if ("android.intent.action.LOCALE_CHANGED".equals(action) || "android.intent.action.TIMEZONE_CHANGED".equals(action) || "android.intent.action.TIME_SET".equals(action)) {
                    DateView.this.mDateFormat = null;
                }
                DateView.this.updateClock();
            }
        }
    };
    private String mLastText;

    public DateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (getTag() != null) {
            this.mFmt = (String) getTag();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.TIME_TICK");
        filter.addAction("android.intent.action.TIME_SET");
        filter.addAction("android.intent.action.TIMEZONE_CHANGED");
        filter.addAction("android.intent.action.LOCALE_CHANGED");
        getContext().registerReceiver(this.mIntentReceiver, filter, null, null);
        updateClock();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mDateFormat = null;
        getContext().unregisterReceiver(this.mIntentReceiver);
    }

    /* access modifiers changed from: protected */
    public void updateClock() {
        if (this.mDateFormat == null) {
            if (this.mFmt.equals("HHmm") || this.mFmt.equals("hmm")) {
                if (DateFormat.is24HourFormat(getContext())) {
                    this.mFmt = "HHmm";
                } else {
                    this.mFmt = "hmm";
                }
            }
            this.mDateFormat = new SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(), this.mFmt));
        }
        this.mCurrentTime.setTime(System.currentTimeMillis());
        String text = this.mDateFormat.format(this.mCurrentTime);
        if (!text.equals(this.mLastText)) {
            setText(text);
            this.mLastText = text;
        }
    }
}
