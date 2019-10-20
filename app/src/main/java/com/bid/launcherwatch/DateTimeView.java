package com.bid.launcherwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings.System;
import android.text.format.Time;
import android.util.AttributeSet;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeView extends TextView {
    private boolean mAttachedToWindow;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (!"android.intent.action.TIME_TICK".equals(intent.getAction()) || java.lang.System.currentTimeMillis() >= DateTimeView.this.mUpdateTimeMillis) {
                DateTimeView.this.mLastFormat = null;
                DateTimeView.this.update();
            }
        }
    };
    private ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean selfChange) {
            DateTimeView.this.mLastFormat = null;
            //DateTimeView.this.update();
        }
    };
    int mLastDisplay = -1;
    DateFormat mLastFormat;
    Date mTime;
    long mTimeMillis;
    /* access modifiers changed from: private */
    public long mUpdateTimeMillis;

    public DateTimeView(Context context) {
        super(context);
    }

    public DateTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerReceivers();
        this.mAttachedToWindow = true;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unregisterReceivers();
        this.mAttachedToWindow = false;
    }

    public void setTime(long time) {
        Time t = new Time();
        t.set(time);
        t.second = 0;
        this.mTimeMillis = t.toMillis(false);
        this.mTime = new Date(t.year - 1900, t.month, t.monthDay, t.hour, t.minute, 0);
        update();
    }

    /* access modifiers changed from: 0000 */
    public void update() {
        int display;
        DateFormat format;
        if (this.mTime != null) {
            long nanoTime = java.lang.System.nanoTime();
            Date date = this.mTime;
            Time t = new Time();
            t.set(this.mTimeMillis);
            t.second = 0;
            t.hour -= 12;
            long twelveHoursBefore = t.toMillis(false);
            t.hour += 12;
            long twelveHoursAfter = t.toMillis(false);
            t.hour = 0;
            t.minute = 0;
            long midnightBefore = t.toMillis(false);
            t.monthDay++;
            long midnightAfter = t.toMillis(false);
            t.set(java.lang.System.currentTimeMillis());
            t.second = 0;
            long nowMillis = t.normalize(false);
            if ((nowMillis < midnightBefore || nowMillis >= midnightAfter) && (nowMillis < twelveHoursBefore || nowMillis >= twelveHoursAfter)) {
                display = 1;
            } else {
                display = 0;
            }
            if (display != this.mLastDisplay || this.mLastFormat == null) {
                switch (display) {
                    case 0:
                        format = getTimeFormat();
                        break;
                    case 1:
                        format = getDateFormat();
                        break;
                    default:
                        RuntimeException runtimeException = new RuntimeException("unknown display value: " + display);
                        throw runtimeException;
                }
                this.mLastFormat = format;
            } else {
                format = this.mLastFormat;
            }
            setText(format.format(this.mTime));
            if (display == 0) {
                if (twelveHoursAfter <= midnightAfter) {
                    twelveHoursAfter = midnightAfter;
                }
                this.mUpdateTimeMillis = twelveHoursAfter;
            } else if (this.mTimeMillis < nowMillis) {
                this.mUpdateTimeMillis = 0;
            } else {
                if (twelveHoursBefore >= midnightBefore) {
                    twelveHoursBefore = midnightBefore;
                }
                this.mUpdateTimeMillis = twelveHoursBefore;
            }
            long nanoTime2 = java.lang.System.nanoTime();
        }
    }

    private DateFormat getTimeFormat() {
        return android.text.format.DateFormat.getTimeFormat(getContext());
    }

    private DateFormat getDateFormat() {
        String format = System.getString(getContext().getContentResolver(), "date_format");
        if (format == null || "".equals(format)) {
            return DateFormat.getDateInstance(3);
        }
        try {
            return new SimpleDateFormat(format);
        } catch (IllegalArgumentException e) {
            return DateFormat.getDateInstance(3);
        }
    }

    private void registerReceivers() {
        Context context = getContext();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.TIME_TICK");
        filter.addAction("android.intent.action.TIME_SET");
        filter.addAction("android.intent.action.CONFIGURATION_CHANGED");
        filter.addAction("android.intent.action.TIMEZONE_CHANGED");
        context.registerReceiver(this.mBroadcastReceiver, filter);
        context.getContentResolver().registerContentObserver(System.getUriFor("date_format"), true, this.mContentObserver);
    }

    private void unregisterReceivers() {
        Context context = getContext();
        context.unregisterReceiver(this.mBroadcastReceiver);
        context.getContentResolver().unregisterContentObserver(this.mContentObserver);
    }
}
