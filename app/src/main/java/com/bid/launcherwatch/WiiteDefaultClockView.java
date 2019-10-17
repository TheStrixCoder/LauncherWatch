package com.bid.launcherwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;

public class WiiteDefaultClockView extends View {
    private Drawable Handle_dial_drawable;
    private Drawable Handle_hour_drawable;
    private Drawable Handle_minute_drawable;
    private Drawable Handle_second_drawable;
    private int ScreenHeight;
    private int ScreenWidth;
    private boolean isChange = false;
    /* access modifiers changed from: private */
    public Time mCalendar = new Time();
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler();
    private float mHour;
    private float mMinute;
    private float mSecond;
    /* access modifiers changed from: private */
    public Runnable mTicker;
    /* access modifiers changed from: private */
    public boolean mTickerStopped;
    private Drawable qw_06_hour_minute_0;
    private Drawable qw_06_hour_minute_1;
    private Drawable qw_06_hour_minute_2;
    private Drawable qw_06_hour_minute_3;
    private Drawable qw_06_hour_minute_4;
    private Drawable qw_06_hour_minute_5;
    private Drawable qw_06_hour_minute_6;
    private Drawable qw_06_hour_minute_7;
    private Drawable qw_06_hour_minute_8;
    private Drawable qw_06_hour_minute_9;
    private Drawable qw_06_hour_minute_point;
    private Drawable qw_06_steps_0;
    private Drawable qw_06_steps_1;
    private Drawable qw_06_steps_2;
    private Drawable qw_06_steps_3;
    private Drawable qw_06_steps_4;
    private Drawable qw_06_steps_5;
    private Drawable qw_06_steps_6;
    private Drawable qw_06_steps_7;
    private Drawable qw_06_steps_8;
    private Drawable qw_06_steps_9;
    private Drawable qw_06_year_month_day_0;
    private Drawable qw_06_year_month_day_1;
    private Drawable qw_06_year_month_day_2;
    private Drawable qw_06_year_month_day_3;
    private Drawable qw_06_year_month_day_4;
    private Drawable qw_06_year_month_day_5;
    private Drawable qw_06_year_month_day_6;
    private Drawable qw_06_year_month_day_7;
    private Drawable qw_06_year_month_day_8;
    private Drawable qw_06_year_month_day_9;
    private Drawable qw_06_year_month_day_point;
    private int steps = 0;
    private int steps_records = 0;

    public WiiteDefaultClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        intView();
    }

    public WiiteDefaultClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        intView();
    }

    public WiiteDefaultClockView(Context context) {
        super(context);
        intView();
    }

    private void intView() {
        if (this.Handle_dial_drawable == null) {
            this.Handle_dial_drawable = getResources().getDrawable(R.drawable.qw_06_dial);
        }
        if (this.Handle_hour_drawable == null) {
            this.Handle_hour_drawable = getResources().getDrawable(R.drawable.qw_06_hour);
        }
        if (this.Handle_minute_drawable == null) {
            this.Handle_minute_drawable = getResources().getDrawable(R.drawable.qw_06_minute);
        }
        if (this.Handle_second_drawable == null) {
            this.Handle_second_drawable = getResources().getDrawable(R.drawable.qw_06_second);
        }
        if (this.qw_06_year_month_day_0 == null) {
            this.qw_06_year_month_day_0 = getResources().getDrawable(R.drawable.qw_06_year_month_day_0);
        }
        if (this.qw_06_year_month_day_1 == null) {
            this.qw_06_year_month_day_1 = getResources().getDrawable(R.drawable.qw_06_year_month_day_1);
        }
        if (this.qw_06_year_month_day_2 == null) {
            this.qw_06_year_month_day_2 = getResources().getDrawable(R.drawable.qw_06_year_month_day_2);
        }
        if (this.qw_06_year_month_day_3 == null) {
            this.qw_06_year_month_day_3 = getResources().getDrawable(R.drawable.qw_06_year_month_day_3);
        }
        if (this.qw_06_year_month_day_4 == null) {
            this.qw_06_year_month_day_4 = getResources().getDrawable(R.drawable.qw_06_year_month_day_4);
        }
        if (this.qw_06_year_month_day_5 == null) {
            this.qw_06_year_month_day_5 = getResources().getDrawable(R.drawable.qw_06_year_month_day_5);
        }
        if (this.qw_06_year_month_day_6 == null) {
            this.qw_06_year_month_day_6 = getResources().getDrawable(R.drawable.qw_06_year_month_day_6);
        }
        if (this.qw_06_year_month_day_7 == null) {
            this.qw_06_year_month_day_7 = getResources().getDrawable(R.drawable.qw_06_year_month_day_7);
        }
        if (this.qw_06_year_month_day_8 == null) {
            this.qw_06_year_month_day_8 = getResources().getDrawable(R.drawable.qw_06_year_month_day_8);
        }
        if (this.qw_06_year_month_day_9 == null) {
            this.qw_06_year_month_day_9 = getResources().getDrawable(R.drawable.qw_06_year_month_day_9);
        }
        if (this.qw_06_year_month_day_point == null) {
            this.qw_06_year_month_day_point = getResources().getDrawable(R.drawable.qw_06_year_month_day_point);
        }
        if (this.qw_06_hour_minute_0 == null) {
            this.qw_06_hour_minute_0 = getResources().getDrawable(R.drawable.qw_06_hour_minute_0);
        }
        if (this.qw_06_hour_minute_1 == null) {
            this.qw_06_hour_minute_1 = getResources().getDrawable(R.drawable.qw_06_hour_minute_1);
        }
        if (this.qw_06_hour_minute_2 == null) {
            this.qw_06_hour_minute_2 = getResources().getDrawable(R.drawable.qw_06_hour_minute_2);
        }
        if (this.qw_06_hour_minute_3 == null) {
            this.qw_06_hour_minute_3 = getResources().getDrawable(R.drawable.qw_06_hour_minute_3);
        }
        if (this.qw_06_hour_minute_4 == null) {
            this.qw_06_hour_minute_4 = getResources().getDrawable(R.drawable.qw_06_hour_minute_4);
        }
        if (this.qw_06_hour_minute_5 == null) {
            this.qw_06_hour_minute_5 = getResources().getDrawable(R.drawable.qw_06_hour_minute_5);
        }
        if (this.qw_06_hour_minute_6 == null) {
            this.qw_06_hour_minute_6 = getResources().getDrawable(R.drawable.qw_06_hour_minute_6);
        }
        if (this.qw_06_hour_minute_7 == null) {
            this.qw_06_hour_minute_7 = getResources().getDrawable(R.drawable.qw_06_hour_minute_7);
        }
        if (this.qw_06_hour_minute_8 == null) {
            this.qw_06_hour_minute_8 = getResources().getDrawable(R.drawable.qw_06_hour_minute_8);
        }
        if (this.qw_06_hour_minute_9 == null) {
            this.qw_06_hour_minute_9 = getResources().getDrawable(R.drawable.qw_06_hour_minute_9);
        }
        if (this.qw_06_hour_minute_point == null) {
            this.qw_06_hour_minute_point = getResources().getDrawable(R.drawable.qw_06_hour_minute_point);
        }
        if (this.qw_06_steps_0 == null) {
            this.qw_06_steps_0 = getResources().getDrawable(R.drawable.qw_06_steps_0);
        }
        if (this.qw_06_steps_1 == null) {
            this.qw_06_steps_1 = getResources().getDrawable(R.drawable.qw_06_steps_1);
        }
        if (this.qw_06_steps_2 == null) {
            this.qw_06_steps_2 = getResources().getDrawable(R.drawable.qw_06_steps_2);
        }
        if (this.qw_06_steps_3 == null) {
            this.qw_06_steps_3 = getResources().getDrawable(R.drawable.qw_06_steps_3);
        }
        if (this.qw_06_steps_4 == null) {
            this.qw_06_steps_4 = getResources().getDrawable(R.drawable.qw_06_steps_4);
        }
        if (this.qw_06_steps_5 == null) {
            this.qw_06_steps_5 = getResources().getDrawable(R.drawable.qw_06_steps_5);
        }
        if (this.qw_06_steps_6 == null) {
            this.qw_06_steps_6 = getResources().getDrawable(R.drawable.qw_06_steps_6);
        }
        if (this.qw_06_steps_7 == null) {
            this.qw_06_steps_7 = getResources().getDrawable(R.drawable.qw_06_steps_7);
        }
        if (this.qw_06_steps_8 == null) {
            this.qw_06_steps_8 = getResources().getDrawable(R.drawable.qw_06_steps_8);
        }
        if (this.qw_06_steps_9 == null) {
            this.qw_06_steps_9 = getResources().getDrawable(R.drawable.qw_06_steps_9);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.ScreenWidth = MeasureSpec.getSize(widthMeasureSpec);
        this.ScreenHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.steps_records = WatchApp.getSteps(getContext());
        int rotateX = this.ScreenWidth / 2;
        int rotateY = this.ScreenHeight / 2;
        if (this.isChange) {
            this.Handle_dial_drawable.setBounds(0, 0, this.ScreenWidth, this.ScreenHeight);
            this.Handle_dial_drawable.draw(canvas);
        }
        canvas.save();
        if (this.isChange) {
            canvas.rotate((this.mHour / 12.0f) * 360.0f, (float) rotateX, (float) rotateY);
            this.Handle_hour_drawable.setBounds(0, 0, this.ScreenWidth, this.ScreenHeight);
            this.Handle_hour_drawable.draw(canvas);
            canvas.restore();
        }
        canvas.save();
        if (this.isChange) {
            canvas.rotate((this.mMinute / 60.0f) * 360.0f, (float) rotateX, (float) rotateY);
            this.Handle_minute_drawable.setBounds(0, 0, this.ScreenWidth, this.ScreenHeight);
            this.Handle_minute_drawable.draw(canvas);
            canvas.restore();
        }
        canvas.save();
        if (this.isChange) {
            canvas.rotate((this.mSecond / 60.0f) * 360.0f, (float) rotateX, (float) rotateX);
            this.Handle_second_drawable.setBounds(0, 0, this.ScreenWidth, this.ScreenHeight);
            this.Handle_second_drawable.draw(canvas);
            canvas.restore();
        }
        int year_first = this.mCalendar.year / 1000;
        int yesr_second = (this.mCalendar.year % 1000) / 100;
        int year_third = ((this.mCalendar.year % 1000) % 100) / 10;
        int year_four = ((this.mCalendar.year % 1000) % 100) % 10;
        this.qw_06_year_month_day_2.setBounds(rotateX - 90, rotateY - 65, rotateX - 50, rotateY - 25);
        this.qw_06_year_month_day_0.setBounds(rotateX - 75, rotateY - 65, rotateX - 35, rotateY - 25);
        this.qw_06_year_month_day_0.setBounds(rotateX - 60, rotateY - 65, rotateX - 20, rotateY - 25);
        this.qw_06_year_month_day_0.setBounds(rotateX - 45, rotateY - 65, rotateX - 5, rotateY - 25);
        switch (year_first) {
            case 1:
                this.qw_06_year_month_day_1.setBounds(rotateX - 90, rotateY - 65, rotateX - 50, rotateY - 25);
                this.qw_06_year_month_day_1.draw(canvas);
                break;
            case 2:
                this.qw_06_year_month_day_2.setBounds(rotateX - 90, rotateY - 65, rotateX - 50, rotateY - 25);
                this.qw_06_year_month_day_2.draw(canvas);
                break;
        }
        switch (yesr_second) {
            case 0:
                this.qw_06_year_month_day_0.setBounds(rotateX - 75, rotateY - 65, rotateX - 35, rotateY - 25);
                this.qw_06_year_month_day_0.draw(canvas);
                break;
            case 1:
                this.qw_06_year_month_day_1.setBounds(rotateX - 75, rotateY - 65, rotateX - 35, rotateY - 25);
                this.qw_06_year_month_day_1.draw(canvas);
                break;
            case 2:
                this.qw_06_year_month_day_2.setBounds(rotateX - 75, rotateY - 65, rotateX - 35, rotateY - 25);
                this.qw_06_year_month_day_2.draw(canvas);
                break;
            case 3:
                this.qw_06_year_month_day_3.setBounds(rotateX - 75, rotateY - 65, rotateX - 35, rotateY - 25);
                this.qw_06_year_month_day_3.draw(canvas);
                break;
            case 4:
                this.qw_06_year_month_day_4.setBounds(rotateX - 75, rotateY - 65, rotateX - 35, rotateY - 25);
                this.qw_06_year_month_day_4.draw(canvas);
                break;
            case 5:
                this.qw_06_year_month_day_5.setBounds(rotateX - 75, rotateY - 65, rotateX - 35, rotateY - 25);
                this.qw_06_year_month_day_5.draw(canvas);
                break;
            case 6:
                this.qw_06_year_month_day_6.setBounds(rotateX - 75, rotateY - 65, rotateX - 35, rotateY - 25);
                this.qw_06_year_month_day_6.draw(canvas);
                break;
            case 7:
                this.qw_06_year_month_day_7.setBounds(rotateX - 75, rotateY - 65, rotateX - 35, rotateY - 25);
                this.qw_06_year_month_day_7.draw(canvas);
                break;
            case 8:
                this.qw_06_year_month_day_8.setBounds(rotateX - 75, rotateY - 65, rotateX - 35, rotateY - 25);
                this.qw_06_year_month_day_8.draw(canvas);
                break;
            case 9:
                this.qw_06_year_month_day_9.setBounds(rotateX - 75, rotateY - 65, rotateX - 35, rotateY - 25);
                this.qw_06_year_month_day_9.draw(canvas);
                break;
        }
        switch (year_third) {
            case 0:
                this.qw_06_year_month_day_0.setBounds(rotateX - 60, rotateY - 65, rotateX - 20, rotateY - 25);
                this.qw_06_year_month_day_0.draw(canvas);
                break;
            case 1:
                this.qw_06_year_month_day_1.setBounds(rotateX - 60, rotateY - 65, rotateX - 20, rotateY - 25);
                this.qw_06_year_month_day_1.draw(canvas);
                break;
            case 2:
                this.qw_06_year_month_day_2.setBounds(rotateX - 60, rotateY - 65, rotateX - 20, rotateY - 25);
                this.qw_06_year_month_day_2.draw(canvas);
                break;
            case 3:
                this.qw_06_year_month_day_3.setBounds(rotateX - 60, rotateY - 65, rotateX - 20, rotateY - 25);
                this.qw_06_year_month_day_3.draw(canvas);
                break;
            case 4:
                this.qw_06_year_month_day_4.setBounds(rotateX - 60, rotateY - 65, rotateX - 20, rotateY - 25);
                this.qw_06_year_month_day_4.draw(canvas);
                break;
            case 5:
                this.qw_06_year_month_day_5.setBounds(rotateX - 60, rotateY - 65, rotateX - 20, rotateY - 25);
                this.qw_06_year_month_day_5.draw(canvas);
                break;
            case 6:
                this.qw_06_year_month_day_6.setBounds(rotateX - 60, rotateY - 65, rotateX - 20, rotateY - 25);
                this.qw_06_year_month_day_6.draw(canvas);
                break;
            case 7:
                this.qw_06_year_month_day_7.setBounds(rotateX - 60, rotateY - 65, rotateX - 20, rotateY - 25);
                this.qw_06_year_month_day_7.draw(canvas);
                break;
            case 8:
                this.qw_06_year_month_day_8.setBounds(rotateX - 60, rotateY - 65, rotateX - 20, rotateY - 25);
                this.qw_06_year_month_day_8.draw(canvas);
                break;
            case 9:
                this.qw_06_year_month_day_9.setBounds(rotateX - 60, rotateY - 65, rotateX - 20, rotateY - 25);
                this.qw_06_year_month_day_9.draw(canvas);
                break;
        }
        switch (year_four) {
            case 0:
                this.qw_06_year_month_day_0.setBounds(rotateX - 45, rotateY - 65, rotateX - 5, rotateY - 25);
                this.qw_06_year_month_day_0.draw(canvas);
                break;
            case 1:
                this.qw_06_year_month_day_1.setBounds(rotateX - 45, rotateY - 65, rotateX - 5, rotateY - 25);
                this.qw_06_year_month_day_1.draw(canvas);
                break;
            case 2:
                this.qw_06_year_month_day_2.setBounds(rotateX - 45, rotateY - 65, rotateX - 5, rotateY - 25);
                this.qw_06_year_month_day_2.draw(canvas);
                break;
            case 3:
                this.qw_06_year_month_day_3.setBounds(rotateX - 45, rotateY - 65, rotateX - 5, rotateY - 25);
                this.qw_06_year_month_day_3.draw(canvas);
                break;
            case 4:
                this.qw_06_year_month_day_4.setBounds(rotateX - 45, rotateY - 65, rotateX - 5, rotateY - 25);
                this.qw_06_year_month_day_4.draw(canvas);
                break;
            case 5:
                this.qw_06_year_month_day_5.setBounds(rotateX - 45, rotateY - 65, rotateX - 5, rotateY - 25);
                this.qw_06_year_month_day_5.draw(canvas);
                break;
            case 6:
                this.qw_06_year_month_day_6.setBounds(rotateX - 45, rotateY - 65, rotateX - 5, rotateY - 25);
                this.qw_06_year_month_day_6.draw(canvas);
                break;
            case 7:
                this.qw_06_year_month_day_7.setBounds(rotateX - 45, rotateY - 65, rotateX - 5, rotateY - 25);
                this.qw_06_year_month_day_7.draw(canvas);
                break;
            case 8:
                this.qw_06_year_month_day_8.setBounds(rotateX - 45, rotateY - 65, rotateX - 5, rotateY - 25);
                this.qw_06_year_month_day_8.draw(canvas);
                break;
            case 9:
                this.qw_06_year_month_day_9.setBounds(rotateX - 45, rotateY - 65, rotateX - 5, rotateY - 25);
                this.qw_06_year_month_day_9.draw(canvas);
                break;
        }
        this.qw_06_year_month_day_point.setBounds(rotateX - 30, rotateY - 65, rotateX + 10, rotateY - 25);
        this.qw_06_year_month_day_point.draw(canvas);
        if (this.mCalendar.month + 1 < 10) {
            this.qw_06_year_month_day_0.setBounds(rotateX - 15, rotateY - 65, rotateX + 25, rotateY - 25);
            this.qw_06_year_month_day_0.draw(canvas);
            if (this.mCalendar.month + 1 == 1) {
                this.qw_06_year_month_day_1.setBounds(rotateX, rotateY - 65, rotateX + 40, rotateY - 25);
                this.qw_06_year_month_day_1.draw(canvas);
            } else if (this.mCalendar.month + 1 == 2) {
                this.qw_06_year_month_day_2.setBounds(rotateX, rotateY - 65, rotateX + 40, rotateY - 25);
                this.qw_06_year_month_day_2.draw(canvas);
            } else if (this.mCalendar.month + 1 == 3) {
                this.qw_06_year_month_day_3.setBounds(rotateX, rotateY - 65, rotateX + 40, rotateY - 25);
                this.qw_06_year_month_day_3.draw(canvas);
            } else if (this.mCalendar.month + 1 == 4) {
                this.qw_06_year_month_day_4.setBounds(rotateX, rotateY - 65, rotateX + 40, rotateY - 25);
                this.qw_06_year_month_day_4.draw(canvas);
            } else if (this.mCalendar.month + 1 == 5) {
                this.qw_06_year_month_day_5.setBounds(rotateX, rotateY - 65, rotateX + 40, rotateY - 25);
                this.qw_06_year_month_day_5.draw(canvas);
            } else if (this.mCalendar.month + 1 == 6) {
                this.qw_06_year_month_day_6.setBounds(rotateX, rotateY - 65, rotateX + 40, rotateY - 25);
                this.qw_06_year_month_day_6.draw(canvas);
            } else if (this.mCalendar.month + 1 == 7) {
                this.qw_06_year_month_day_7.setBounds(rotateX, rotateY - 65, rotateX + 40, rotateY - 25);
                this.qw_06_year_month_day_7.draw(canvas);
            } else if (this.mCalendar.month + 1 == 8) {
                this.qw_06_year_month_day_8.setBounds(rotateX, rotateY - 65, rotateX + 40, rotateY - 25);
                this.qw_06_year_month_day_8.draw(canvas);
            } else if (this.mCalendar.month + 1 == 9) {
                this.qw_06_year_month_day_9.setBounds(rotateX, rotateY - 65, rotateX + 40, rotateY - 25);
                this.qw_06_year_month_day_9.draw(canvas);
            }
        } else {
            this.qw_06_year_month_day_1.setBounds(rotateX - 15, rotateY - 65, rotateX + 25, rotateY - 25);
            this.qw_06_year_month_day_1.draw(canvas);
            if (this.mCalendar.month + 1 == 10) {
                this.qw_06_year_month_day_0.setBounds(rotateX, rotateY - 65, rotateX + 40, rotateY - 25);
                this.qw_06_year_month_day_0.draw(canvas);
            } else if (this.mCalendar.month + 1 == 11) {
                this.qw_06_year_month_day_1.setBounds(rotateX, rotateY - 65, rotateX + 40, rotateY - 25);
                this.qw_06_year_month_day_1.draw(canvas);
            } else if (this.mCalendar.month + 1 == 12) {
                this.qw_06_year_month_day_2.setBounds(rotateX, rotateY - 65, rotateX + 40, rotateY - 25);
                this.qw_06_year_month_day_2.draw(canvas);
            }
        }
        this.qw_06_year_month_day_point.setBounds(rotateX + 15, rotateY - 65, rotateX + 55, rotateY - 25);
        this.qw_06_year_month_day_point.draw(canvas);
        if (this.mCalendar.monthDay < 10) {
            this.qw_06_year_month_day_0.setBounds(rotateX + 30, rotateY - 65, rotateX + 70, rotateY - 25);
            this.qw_06_year_month_day_0.draw(canvas);
            if (this.mCalendar.monthDay == 1) {
                this.qw_06_year_month_day_1.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_1.draw(canvas);
            } else if (this.mCalendar.monthDay == 2) {
                this.qw_06_year_month_day_2.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_2.draw(canvas);
            } else if (this.mCalendar.monthDay == 3) {
                this.qw_06_year_month_day_3.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_3.draw(canvas);
            } else if (this.mCalendar.monthDay == 4) {
                this.qw_06_year_month_day_4.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_4.draw(canvas);
            } else if (this.mCalendar.monthDay == 5) {
                this.qw_06_year_month_day_5.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_5.draw(canvas);
            } else if (this.mCalendar.monthDay == 6) {
                this.qw_06_year_month_day_6.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_6.draw(canvas);
            } else if (this.mCalendar.monthDay == 7) {
                this.qw_06_year_month_day_7.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_7.draw(canvas);
            } else if (this.mCalendar.monthDay == 8) {
                this.qw_06_year_month_day_8.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_8.draw(canvas);
            } else if (this.mCalendar.monthDay == 9) {
                this.qw_06_year_month_day_9.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_9.draw(canvas);
            }
        } else if (this.mCalendar.monthDay < 20) {
            this.qw_06_year_month_day_1.setBounds(rotateX + 30, rotateY - 65, rotateX + 70, rotateY - 25);
            this.qw_06_year_month_day_1.draw(canvas);
            if (this.mCalendar.monthDay == 11) {
                this.qw_06_year_month_day_1.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_1.draw(canvas);
            } else if (this.mCalendar.monthDay == 12) {
                this.qw_06_year_month_day_2.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_2.draw(canvas);
            } else if (this.mCalendar.monthDay == 13) {
                this.qw_06_year_month_day_3.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_3.draw(canvas);
            } else if (this.mCalendar.monthDay == 14) {
                this.qw_06_year_month_day_4.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_4.draw(canvas);
            } else if (this.mCalendar.monthDay == 15) {
                this.qw_06_year_month_day_5.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_5.draw(canvas);
            } else if (this.mCalendar.monthDay == 16) {
                this.qw_06_year_month_day_6.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_6.draw(canvas);
            } else if (this.mCalendar.monthDay == 17) {
                this.qw_06_year_month_day_7.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_7.draw(canvas);
            } else if (this.mCalendar.monthDay == 18) {
                this.qw_06_year_month_day_8.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_8.draw(canvas);
            } else if (this.mCalendar.monthDay == 19) {
                this.qw_06_year_month_day_9.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_9.draw(canvas);
            } else if (this.mCalendar.monthDay == 10) {
                this.qw_06_year_month_day_0.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_0.draw(canvas);
            }
        } else if (this.mCalendar.monthDay < 30) {
            this.qw_06_year_month_day_2.setBounds(rotateX + 30, rotateY - 65, rotateX + 70, rotateY - 25);
            this.qw_06_year_month_day_2.draw(canvas);
            if (this.mCalendar.monthDay == 21) {
                this.qw_06_year_month_day_1.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_1.draw(canvas);
            } else if (this.mCalendar.monthDay == 22) {
                this.qw_06_year_month_day_2.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_2.draw(canvas);
            } else if (this.mCalendar.monthDay == 23) {
                this.qw_06_year_month_day_3.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_3.draw(canvas);
            } else if (this.mCalendar.monthDay == 24) {
                this.qw_06_year_month_day_4.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_4.draw(canvas);
            } else if (this.mCalendar.monthDay == 25) {
                this.qw_06_year_month_day_5.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_5.draw(canvas);
            } else if (this.mCalendar.monthDay == 26) {
                this.qw_06_year_month_day_6.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_6.draw(canvas);
            } else if (this.mCalendar.monthDay == 27) {
                this.qw_06_year_month_day_7.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_7.draw(canvas);
            } else if (this.mCalendar.monthDay == 28) {
                this.qw_06_year_month_day_8.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_8.draw(canvas);
            } else if (this.mCalendar.monthDay == 29) {
                this.qw_06_year_month_day_9.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_9.draw(canvas);
            } else if (this.mCalendar.monthDay == 20) {
                this.qw_06_year_month_day_0.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_0.draw(canvas);
            }
        } else if (this.mCalendar.monthDay < 32) {
            this.qw_06_year_month_day_3.setBounds(rotateX + 30, rotateY - 65, rotateX + 70, rotateY - 25);
            this.qw_06_year_month_day_3.draw(canvas);
            if (this.mCalendar.monthDay == 30) {
                this.qw_06_year_month_day_0.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_0.draw(canvas);
            } else if (this.mCalendar.monthDay == 31) {
                this.qw_06_year_month_day_1.setBounds(rotateX + 45, rotateY - 65, rotateX + 85, rotateY - 25);
                this.qw_06_year_month_day_1.draw(canvas);
            }
        }
        if (DateFormat.is24HourFormat(getContext())) {
            if (this.mCalendar.hour < 10) {
                this.qw_06_hour_minute_0.setBounds(rotateX - 120, rotateY - 40, rotateX - 20, rotateY + 62);
                this.qw_06_hour_minute_0.draw(canvas);
                if (this.mCalendar.hour == 1) {
                    this.qw_06_hour_minute_1.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_1.draw(canvas);
                } else if (this.mCalendar.hour == 2) {
                    this.qw_06_hour_minute_2.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_2.draw(canvas);
                } else if (this.mCalendar.hour == 3) {
                    this.qw_06_hour_minute_3.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_3.draw(canvas);
                } else if (this.mCalendar.hour == 4) {
                    this.qw_06_hour_minute_4.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_4.draw(canvas);
                } else if (this.mCalendar.hour == 5) {
                    this.qw_06_hour_minute_5.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_5.draw(canvas);
                } else if (this.mCalendar.hour == 6) {
                    this.qw_06_hour_minute_6.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_6.draw(canvas);
                } else if (this.mCalendar.hour == 7) {
                    this.qw_06_hour_minute_7.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_7.draw(canvas);
                } else if (this.mCalendar.hour == 8) {
                    this.qw_06_hour_minute_8.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_8.draw(canvas);
                } else if (this.mCalendar.hour == 9) {
                    this.qw_06_hour_minute_9.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_9.draw(canvas);
                } else if (this.mCalendar.hour == 0) {
                    this.qw_06_hour_minute_0.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_0.draw(canvas);
                }
            } else if (this.mCalendar.hour < 20) {
                this.qw_06_hour_minute_1.setBounds(rotateX - 120, rotateY - 40, rotateX - 20, rotateY + 62);
                this.qw_06_hour_minute_1.draw(canvas);
                if (this.mCalendar.hour == 11) {
                    this.qw_06_hour_minute_1.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_1.draw(canvas);
                } else if (this.mCalendar.hour == 12) {
                    this.qw_06_hour_minute_2.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_2.draw(canvas);
                } else if (this.mCalendar.hour == 13) {
                    this.qw_06_hour_minute_3.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_3.draw(canvas);
                } else if (this.mCalendar.hour == 14) {
                    this.qw_06_hour_minute_4.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_4.draw(canvas);
                } else if (this.mCalendar.hour == 15) {
                    this.qw_06_hour_minute_5.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_5.draw(canvas);
                } else if (this.mCalendar.hour == 16) {
                    this.qw_06_hour_minute_6.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_6.draw(canvas);
                } else if (this.mCalendar.hour == 17) {
                    this.qw_06_hour_minute_7.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_7.draw(canvas);
                } else if (this.mCalendar.hour == 18) {
                    this.qw_06_hour_minute_8.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_8.draw(canvas);
                } else if (this.mCalendar.hour == 19) {
                    this.qw_06_hour_minute_9.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_9.draw(canvas);
                } else if (this.mCalendar.hour == 10) {
                    this.qw_06_hour_minute_0.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_0.draw(canvas);
                }
            } else if (this.mCalendar.hour < 24) {
                this.qw_06_hour_minute_2.setBounds(rotateX - 115, rotateY - 40, rotateX - 15, rotateY + 62);
                this.qw_06_hour_minute_2.draw(canvas);
                if (this.mCalendar.hour == 20) {
                    this.qw_06_hour_minute_0.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_0.draw(canvas);
                } else if (this.mCalendar.hour == 21) {
                    this.qw_06_hour_minute_1.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_1.draw(canvas);
                } else if (this.mCalendar.hour == 22) {
                    this.qw_06_hour_minute_2.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_2.draw(canvas);
                } else if (this.mCalendar.hour == 23) {
                    this.qw_06_hour_minute_3.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                    this.qw_06_hour_minute_3.draw(canvas);
                }
            } else if (this.mCalendar.hour == 24) {
                this.qw_06_hour_minute_0.setBounds(rotateX - 120, rotateY - 40, rotateX - 20, rotateY + 62);
                this.qw_06_hour_minute_0.draw(canvas);
                this.qw_06_hour_minute_0.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_0.draw(canvas);
            }
        } else if (this.mCalendar.hour < 10) {
            this.qw_06_hour_minute_0.setBounds(rotateX - 120, rotateY - 40, rotateX - 20, rotateY + 62);
            this.qw_06_hour_minute_0.draw(canvas);
            if (this.mCalendar.hour == 1) {
                this.qw_06_hour_minute_1.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_1.draw(canvas);
            } else if (this.mCalendar.hour == 2) {
                this.qw_06_hour_minute_2.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_2.draw(canvas);
            } else if (this.mCalendar.hour == 3) {
                this.qw_06_hour_minute_3.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_3.draw(canvas);
            } else if (this.mCalendar.hour == 4) {
                this.qw_06_hour_minute_4.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_4.draw(canvas);
            } else if (this.mCalendar.hour == 5) {
                this.qw_06_hour_minute_5.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_5.draw(canvas);
            } else if (this.mCalendar.hour == 6) {
                this.qw_06_hour_minute_6.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_6.draw(canvas);
            } else if (this.mCalendar.hour == 7) {
                this.qw_06_hour_minute_7.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_7.draw(canvas);
            } else if (this.mCalendar.hour == 8) {
                this.qw_06_hour_minute_8.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_8.draw(canvas);
            } else if (this.mCalendar.hour == 9) {
                this.qw_06_hour_minute_9.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_9.draw(canvas);
            } else if (this.mCalendar.hour == 0) {
                this.qw_06_hour_minute_0.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_0.draw(canvas);
            }
        } else if (this.mCalendar.hour < 13) {
            this.qw_06_hour_minute_1.setBounds(rotateX - 120, rotateY - 40, rotateX - 20, rotateY + 62);
            this.qw_06_hour_minute_1.draw(canvas);
            if (this.mCalendar.hour == 11) {
                this.qw_06_hour_minute_1.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_1.draw(canvas);
            } else if (this.mCalendar.hour == 12) {
                this.qw_06_hour_minute_2.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_2.draw(canvas);
            } else if (this.mCalendar.hour == 10) {
                this.qw_06_hour_minute_0.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_0.draw(canvas);
            }
        } else if (this.mCalendar.hour < 22) {
            this.qw_06_hour_minute_0.setBounds(rotateX - 120, rotateY - 40, rotateX - 20, rotateY + 62);
            this.qw_06_hour_minute_0.draw(canvas);
            if (this.mCalendar.hour == 13) {
                this.qw_06_hour_minute_1.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_1.draw(canvas);
            } else if (this.mCalendar.hour == 14) {
                this.qw_06_hour_minute_2.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_2.draw(canvas);
            } else if (this.mCalendar.hour == 15) {
                this.qw_06_hour_minute_3.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_3.draw(canvas);
            } else if (this.mCalendar.hour == 16) {
                this.qw_06_hour_minute_4.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_4.draw(canvas);
            } else if (this.mCalendar.hour == 17) {
                this.qw_06_hour_minute_5.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_5.draw(canvas);
            } else if (this.mCalendar.hour == 18) {
                this.qw_06_hour_minute_6.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_6.draw(canvas);
            } else if (this.mCalendar.hour == 19) {
                this.qw_06_hour_minute_7.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_7.draw(canvas);
            } else if (this.mCalendar.hour == 20) {
                this.qw_06_hour_minute_8.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_8.draw(canvas);
            } else if (this.mCalendar.hour == 21) {
                this.qw_06_hour_minute_9.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_9.draw(canvas);
            }
        } else if (this.mCalendar.hour < 24) {
            this.qw_06_hour_minute_1.setBounds(rotateX - 120, rotateY - 40, rotateX - 20, rotateY + 62);
            this.qw_06_hour_minute_1.draw(canvas);
            if (this.mCalendar.hour == 22) {
                this.qw_06_hour_minute_0.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_0.draw(canvas);
            } else if (this.mCalendar.hour == 23) {
                this.qw_06_hour_minute_1.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
                this.qw_06_hour_minute_1.draw(canvas);
            }
        } else if (this.mCalendar.hour == 24) {
            this.qw_06_hour_minute_1.setBounds(rotateX - 120, rotateY - 40, rotateX - 20, rotateY + 62);
            this.qw_06_hour_minute_1.draw(canvas);
            this.qw_06_hour_minute_2.setBounds(rotateX - 80, rotateY - 40, rotateX + 20, rotateY + 62);
            this.qw_06_hour_minute_2.draw(canvas);
        }
        if (this.mCalendar.second % 2 == 0) {
            this.qw_06_hour_minute_point.setBounds(rotateX - 50, rotateY - 50, rotateX + 50, rotateY + 52);
            this.qw_06_hour_minute_point.draw(canvas);
        }
        if (this.mCalendar.minute < 10) {
            this.qw_06_hour_minute_0.setBounds(rotateX - 20, rotateY - 40, rotateX + 80, rotateY + 62);
            this.qw_06_hour_minute_0.draw(canvas);
            if (this.mCalendar.minute == 0) {
                this.qw_06_hour_minute_0.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_0.draw(canvas);
            } else if (this.mCalendar.minute == 1) {
                this.qw_06_hour_minute_1.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_1.draw(canvas);
            } else if (this.mCalendar.minute == 2) {
                this.qw_06_hour_minute_2.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_2.draw(canvas);
            } else if (this.mCalendar.minute == 3) {
                this.qw_06_hour_minute_3.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_3.draw(canvas);
            } else if (this.mCalendar.minute == 4) {
                this.qw_06_hour_minute_4.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_4.draw(canvas);
            } else if (this.mCalendar.minute == 5) {
                this.qw_06_hour_minute_5.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_5.draw(canvas);
            } else if (this.mCalendar.minute == 6) {
                this.qw_06_hour_minute_6.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_6.draw(canvas);
            } else if (this.mCalendar.minute == 7) {
                this.qw_06_hour_minute_7.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_7.draw(canvas);
            } else if (this.mCalendar.minute == 8) {
                this.qw_06_hour_minute_8.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_8.draw(canvas);
            } else if (this.mCalendar.minute == 9) {
                this.qw_06_hour_minute_9.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_9.draw(canvas);
            }
        } else if (this.mCalendar.minute < 20) {
            this.qw_06_hour_minute_1.setBounds(rotateX - 20, rotateY - 40, rotateX + 80, rotateY + 62);
            this.qw_06_hour_minute_1.draw(canvas);
            if (this.mCalendar.minute == 10) {
                this.qw_06_hour_minute_0.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_0.draw(canvas);
            } else if (this.mCalendar.minute == 11) {
                this.qw_06_hour_minute_1.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_1.draw(canvas);
            } else if (this.mCalendar.minute == 12) {
                this.qw_06_hour_minute_2.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_2.draw(canvas);
            } else if (this.mCalendar.minute == 13) {
                this.qw_06_hour_minute_3.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_3.draw(canvas);
            } else if (this.mCalendar.minute == 14) {
                this.qw_06_hour_minute_4.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_4.draw(canvas);
            } else if (this.mCalendar.minute == 15) {
                this.qw_06_hour_minute_5.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_5.draw(canvas);
            } else if (this.mCalendar.minute == 16) {
                this.qw_06_hour_minute_6.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_6.draw(canvas);
            } else if (this.mCalendar.minute == 17) {
                this.qw_06_hour_minute_7.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_7.draw(canvas);
            } else if (this.mCalendar.minute == 18) {
                this.qw_06_hour_minute_8.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_8.draw(canvas);
            } else if (this.mCalendar.minute == 19) {
                this.qw_06_hour_minute_9.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_9.draw(canvas);
            }
        } else if (this.mCalendar.minute < 30) {
            this.qw_06_hour_minute_2.setBounds(rotateX - 20, rotateY - 40, rotateX + 80, rotateY + 62);
            this.qw_06_hour_minute_2.draw(canvas);
            if (this.mCalendar.minute == 20) {
                this.qw_06_hour_minute_0.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_0.draw(canvas);
            } else if (this.mCalendar.minute == 21) {
                this.qw_06_hour_minute_1.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_1.draw(canvas);
            } else if (this.mCalendar.minute == 22) {
                this.qw_06_hour_minute_2.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_2.draw(canvas);
            } else if (this.mCalendar.minute == 23) {
                this.qw_06_hour_minute_3.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_3.draw(canvas);
            } else if (this.mCalendar.minute == 24) {
                this.qw_06_hour_minute_4.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_4.draw(canvas);
            } else if (this.mCalendar.minute == 25) {
                this.qw_06_hour_minute_5.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_5.draw(canvas);
            } else if (this.mCalendar.minute == 26) {
                this.qw_06_hour_minute_6.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_6.draw(canvas);
            } else if (this.mCalendar.minute == 27) {
                this.qw_06_hour_minute_7.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_7.draw(canvas);
            } else if (this.mCalendar.minute == 28) {
                this.qw_06_hour_minute_8.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_8.draw(canvas);
            } else if (this.mCalendar.minute == 29) {
                this.qw_06_hour_minute_9.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_9.draw(canvas);
            }
        } else if (this.mCalendar.minute < 40) {
            this.qw_06_hour_minute_3.setBounds(rotateX - 20, rotateY - 40, rotateX + 80, rotateY + 62);
            this.qw_06_hour_minute_3.draw(canvas);
            if (this.mCalendar.minute == 30) {
                this.qw_06_hour_minute_0.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_0.draw(canvas);
            } else if (this.mCalendar.minute == 31) {
                this.qw_06_hour_minute_1.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_1.draw(canvas);
            } else if (this.mCalendar.minute == 32) {
                this.qw_06_hour_minute_2.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_2.draw(canvas);
            } else if (this.mCalendar.minute == 33) {
                this.qw_06_hour_minute_3.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_3.draw(canvas);
            } else if (this.mCalendar.minute == 34) {
                this.qw_06_hour_minute_4.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_4.draw(canvas);
            } else if (this.mCalendar.minute == 35) {
                this.qw_06_hour_minute_5.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_5.draw(canvas);
            } else if (this.mCalendar.minute == 36) {
                this.qw_06_hour_minute_6.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_6.draw(canvas);
            } else if (this.mCalendar.minute == 37) {
                this.qw_06_hour_minute_7.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_7.draw(canvas);
            } else if (this.mCalendar.minute == 38) {
                this.qw_06_hour_minute_8.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_8.draw(canvas);
            } else if (this.mCalendar.minute == 39) {
                this.qw_06_hour_minute_9.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_9.draw(canvas);
            }
        } else if (this.mCalendar.minute < 50) {
            this.qw_06_hour_minute_4.setBounds(rotateX - 20, rotateY - 40, rotateX + 80, rotateY + 62);
            this.qw_06_hour_minute_4.draw(canvas);
            if (this.mCalendar.minute == 40) {
                this.qw_06_hour_minute_0.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_0.draw(canvas);
            } else if (this.mCalendar.minute == 41) {
                this.qw_06_hour_minute_1.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_1.draw(canvas);
            } else if (this.mCalendar.minute == 42) {
                this.qw_06_hour_minute_2.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_2.draw(canvas);
            } else if (this.mCalendar.minute == 43) {
                this.qw_06_hour_minute_3.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_3.draw(canvas);
            } else if (this.mCalendar.minute == 44) {
                this.qw_06_hour_minute_4.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_4.draw(canvas);
            } else if (this.mCalendar.minute == 45) {
                this.qw_06_hour_minute_5.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_5.draw(canvas);
            } else if (this.mCalendar.minute == 46) {
                this.qw_06_hour_minute_6.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_6.draw(canvas);
            } else if (this.mCalendar.minute == 47) {
                this.qw_06_hour_minute_7.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_7.draw(canvas);
            } else if (this.mCalendar.minute == 48) {
                this.qw_06_hour_minute_8.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_8.draw(canvas);
            } else if (this.mCalendar.minute == 49) {
                this.qw_06_hour_minute_9.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_9.draw(canvas);
            }
        } else if (this.mCalendar.minute < 60) {
            this.qw_06_hour_minute_5.setBounds(rotateX - 20, rotateY - 40, rotateX + 80, rotateY + 62);
            this.qw_06_hour_minute_5.draw(canvas);
            if (this.mCalendar.minute == 50) {
                this.qw_06_hour_minute_0.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_0.draw(canvas);
            } else if (this.mCalendar.minute == 51) {
                this.qw_06_hour_minute_1.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_1.draw(canvas);
            } else if (this.mCalendar.minute == 52) {
                this.qw_06_hour_minute_2.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_2.draw(canvas);
            } else if (this.mCalendar.minute == 53) {
                this.qw_06_hour_minute_3.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_3.draw(canvas);
            } else if (this.mCalendar.minute == 54) {
                this.qw_06_hour_minute_4.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_4.draw(canvas);
            } else if (this.mCalendar.minute == 55) {
                this.qw_06_hour_minute_5.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_5.draw(canvas);
            } else if (this.mCalendar.minute == 56) {
                this.qw_06_hour_minute_6.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_6.draw(canvas);
            } else if (this.mCalendar.minute == 57) {
                this.qw_06_hour_minute_7.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_7.draw(canvas);
            } else if (this.mCalendar.minute == 58) {
                this.qw_06_hour_minute_8.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_8.draw(canvas);
            } else if (this.mCalendar.minute == 59) {
                this.qw_06_hour_minute_9.setBounds(rotateX + 20, rotateY - 40, rotateX + 120, rotateY + 62);
                this.qw_06_hour_minute_9.draw(canvas);
            }
        }
        int steps_records_first = this.steps_records / 10000;
        int steps_records_second = (this.steps_records % 10000) / 1000;
        int steps_records_third = ((this.steps_records % 10000) % 1000) / 100;
        int steps_records_four = (((this.steps_records % 10000) % 1000) % 100) / 10;
        int steps_records_five = (((this.steps_records % 10000) % 1000) % 100) % 10;
        if (steps_records_first == 0) {
            this.qw_06_steps_0.setBounds(rotateX - 50, rotateY + 45, rotateX + 22, rotateY + 105);
            this.qw_06_steps_0.draw(canvas);
        } else if (steps_records_first == 1) {
            this.qw_06_steps_1.setBounds(rotateX - 50, rotateY + 45, rotateX + 22, rotateY + 105);
            this.qw_06_steps_1.draw(canvas);
        } else if (steps_records_first == 2) {
            this.qw_06_steps_2.setBounds(rotateX - 50, rotateY + 45, rotateX + 22, rotateY + 105);
            this.qw_06_steps_2.draw(canvas);
        } else if (steps_records_first == 3) {
            this.qw_06_steps_3.setBounds(rotateX - 50, rotateY + 45, rotateX + 22, rotateY + 105);
            this.qw_06_steps_3.draw(canvas);
        } else if (steps_records_first == 4) {
            this.qw_06_steps_4.setBounds(rotateX - 50, rotateY + 45, rotateX + 22, rotateY + 105);
            this.qw_06_steps_4.draw(canvas);
        } else if (steps_records_first == 5) {
            this.qw_06_steps_5.setBounds(rotateX - 50, rotateY + 45, rotateX + 22, rotateY + 105);
            this.qw_06_steps_5.draw(canvas);
        } else if (steps_records_first == 6) {
            this.qw_06_steps_6.setBounds(rotateX - 50, rotateY + 45, rotateX + 22, rotateY + 105);
            this.qw_06_steps_6.draw(canvas);
        } else if (steps_records_first == 7) {
            this.qw_06_steps_7.setBounds(rotateX - 50, rotateY + 45, rotateX + 22, rotateY + 105);
            this.qw_06_steps_7.draw(canvas);
        } else if (steps_records_first == 8) {
            this.qw_06_steps_8.setBounds(rotateX - 50, rotateY + 45, rotateX + 22, rotateY + 105);
            this.qw_06_steps_8.draw(canvas);
        } else if (steps_records_first == 9) {
            this.qw_06_steps_9.setBounds(rotateX - 50, rotateY + 45, rotateX + 22, rotateY + 105);
            this.qw_06_steps_9.draw(canvas);
        }
        if (steps_records_second == 0) {
            this.qw_06_steps_0.setBounds(rotateX - 35, rotateY + 45, rotateX + 37, rotateY + 105);
            this.qw_06_steps_0.draw(canvas);
        } else if (steps_records_second == 1) {
            this.qw_06_steps_1.setBounds(rotateX - 35, rotateY + 45, rotateX + 37, rotateY + 105);
            this.qw_06_steps_1.draw(canvas);
        } else if (steps_records_second == 2) {
            this.qw_06_steps_2.setBounds(rotateX - 35, rotateY + 45, rotateX + 37, rotateY + 105);
            this.qw_06_steps_2.draw(canvas);
        } else if (steps_records_second == 3) {
            this.qw_06_steps_3.setBounds(rotateX - 35, rotateY + 45, rotateX + 37, rotateY + 105);
            this.qw_06_steps_3.draw(canvas);
        } else if (steps_records_second == 4) {
            this.qw_06_steps_4.setBounds(rotateX - 35, rotateY + 45, rotateX + 37, rotateY + 105);
            this.qw_06_steps_4.draw(canvas);
        } else if (steps_records_second == 5) {
            this.qw_06_steps_5.setBounds(rotateX - 35, rotateY + 45, rotateX + 37, rotateY + 105);
            this.qw_06_steps_5.draw(canvas);
        } else if (steps_records_second == 6) {
            this.qw_06_steps_6.setBounds(rotateX - 35, rotateY + 45, rotateX + 37, rotateY + 105);
            this.qw_06_steps_6.draw(canvas);
        } else if (steps_records_second == 7) {
            this.qw_06_steps_7.setBounds(rotateX - 35, rotateY + 45, rotateX + 37, rotateY + 105);
            this.qw_06_steps_7.draw(canvas);
        } else if (steps_records_second == 8) {
            this.qw_06_steps_8.setBounds(rotateX - 35, rotateY + 45, rotateX + 37, rotateY + 105);
            this.qw_06_steps_8.draw(canvas);
        } else if (steps_records_second == 9) {
            this.qw_06_steps_9.setBounds(rotateX - 35, rotateY + 45, rotateX + 37, rotateY + 105);
            this.qw_06_steps_9.draw(canvas);
        }
        if (steps_records_third == 0) {
            this.qw_06_steps_0.setBounds(rotateX - 20, rotateY + 45, rotateX + 52, rotateY + 105);
            this.qw_06_steps_0.draw(canvas);
        } else if (steps_records_third == 1) {
            this.qw_06_steps_1.setBounds(rotateX - 20, rotateY + 45, rotateX + 52, rotateY + 105);
            this.qw_06_steps_1.draw(canvas);
        } else if (steps_records_third == 2) {
            this.qw_06_steps_2.setBounds(rotateX - 20, rotateY + 45, rotateX + 52, rotateY + 105);
            this.qw_06_steps_2.draw(canvas);
        } else if (steps_records_third == 3) {
            this.qw_06_steps_3.setBounds(rotateX - 20, rotateY + 45, rotateX + 52, rotateY + 105);
            this.qw_06_steps_3.draw(canvas);
        } else if (steps_records_third == 4) {
            this.qw_06_steps_4.setBounds(rotateX - 20, rotateY + 45, rotateX + 52, rotateY + 105);
            this.qw_06_steps_4.draw(canvas);
        } else if (steps_records_third == 5) {
            this.qw_06_steps_5.setBounds(rotateX - 20, rotateY + 45, rotateX + 52, rotateY + 105);
            this.qw_06_steps_5.draw(canvas);
        } else if (steps_records_third == 6) {
            this.qw_06_steps_6.setBounds(rotateX - 20, rotateY + 45, rotateX + 52, rotateY + 105);
            this.qw_06_steps_6.draw(canvas);
        } else if (steps_records_third == 7) {
            this.qw_06_steps_7.setBounds(rotateX - 20, rotateY + 45, rotateX + 52, rotateY + 105);
            this.qw_06_steps_7.draw(canvas);
        } else if (steps_records_third == 8) {
            this.qw_06_steps_8.setBounds(rotateX - 20, rotateY + 45, rotateX + 52, rotateY + 105);
            this.qw_06_steps_8.draw(canvas);
        } else if (steps_records_third == 9) {
            this.qw_06_steps_9.setBounds(rotateX - 20, rotateY + 45, rotateX + 52, rotateY + 105);
            this.qw_06_steps_9.draw(canvas);
        }
        if (steps_records_four == 0) {
            this.qw_06_steps_0.setBounds(rotateX - 5, rotateY + 45, rotateX + 67, rotateY + 105);
            this.qw_06_steps_0.draw(canvas);
        } else if (steps_records_four == 1) {
            this.qw_06_steps_1.setBounds(rotateX - 5, rotateY + 45, rotateX + 67, rotateY + 105);
            this.qw_06_steps_1.draw(canvas);
        } else if (steps_records_four == 2) {
            this.qw_06_steps_2.setBounds(rotateX - 5, rotateY + 45, rotateX + 67, rotateY + 105);
            this.qw_06_steps_2.draw(canvas);
        } else if (steps_records_four == 3) {
            this.qw_06_steps_3.setBounds(rotateX - 5, rotateY + 45, rotateX + 67, rotateY + 105);
            this.qw_06_steps_3.draw(canvas);
        } else if (steps_records_four == 4) {
            this.qw_06_steps_4.setBounds(rotateX - 5, rotateY + 45, rotateX + 67, rotateY + 105);
            this.qw_06_steps_4.draw(canvas);
        } else if (steps_records_four == 5) {
            this.qw_06_steps_5.setBounds(rotateX - 5, rotateY + 45, rotateX + 67, rotateY + 105);
            this.qw_06_steps_5.draw(canvas);
        } else if (steps_records_four == 6) {
            this.qw_06_steps_6.setBounds(rotateX - 5, rotateY + 45, rotateX + 67, rotateY + 105);
            this.qw_06_steps_6.draw(canvas);
        } else if (steps_records_four == 7) {
            this.qw_06_steps_7.setBounds(rotateX - 5, rotateY + 45, rotateX + 67, rotateY + 105);
            this.qw_06_steps_7.draw(canvas);
        } else if (steps_records_four == 8) {
            this.qw_06_steps_8.setBounds(rotateX - 5, rotateY + 45, rotateX + 67, rotateY + 105);
            this.qw_06_steps_8.draw(canvas);
        } else if (steps_records_four == 9) {
            this.qw_06_steps_9.setBounds(rotateX - 5, rotateY + 45, rotateX + 67, rotateY + 105);
            this.qw_06_steps_9.draw(canvas);
        }
        if (steps_records_five == 0) {
            this.qw_06_steps_0.setBounds(rotateX + 10, rotateY + 45, rotateX + 82, rotateY + 105);
            this.qw_06_steps_0.draw(canvas);
        } else if (steps_records_five == 1) {
            this.qw_06_steps_1.setBounds(rotateX + 10, rotateY + 45, rotateX + 82, rotateY + 105);
            this.qw_06_steps_1.draw(canvas);
        } else if (steps_records_five == 2) {
            this.qw_06_steps_2.setBounds(rotateX + 10, rotateY + 45, rotateX + 82, rotateY + 105);
            this.qw_06_steps_2.draw(canvas);
        } else if (steps_records_five == 3) {
            this.qw_06_steps_3.setBounds(rotateX + 10, rotateY + 45, rotateX + 82, rotateY + 105);
            this.qw_06_steps_3.draw(canvas);
        } else if (steps_records_five == 4) {
            this.qw_06_steps_4.setBounds(rotateX + 10, rotateY + 45, rotateX + 82, rotateY + 105);
            this.qw_06_steps_4.draw(canvas);
        } else if (steps_records_five == 5) {
            this.qw_06_steps_5.setBounds(rotateX + 10, rotateY + 45, rotateX + 82, rotateY + 105);
            this.qw_06_steps_5.draw(canvas);
        } else if (steps_records_five == 6) {
            this.qw_06_steps_6.setBounds(rotateX + 10, rotateY + 45, rotateX + 82, rotateY + 105);
            this.qw_06_steps_6.draw(canvas);
        } else if (steps_records_five == 7) {
            this.qw_06_steps_7.setBounds(rotateX + 10, rotateY + 45, rotateX + 82, rotateY + 105);
            this.qw_06_steps_7.draw(canvas);
        } else if (steps_records_five == 8) {
            this.qw_06_steps_8.setBounds(rotateX + 10, rotateY + 45, rotateX + 82, rotateY + 105);
            this.qw_06_steps_8.draw(canvas);
        } else if (steps_records_five == 9) {
            this.qw_06_steps_9.setBounds(rotateX + 10, rotateY + 45, rotateX + 82, rotateY + 105);
            this.qw_06_steps_9.draw(canvas);
        }
    }

    public void onAttachedToWindow() {
        this.mTickerStopped = false;
        super.onAttachedToWindow();
        runTicker();
    }

    public void onTimeChanged(Time mCalendar2) {
        mCalendar2.setToNow();
        int hour = mCalendar2.hour;
        int minute = mCalendar2.minute;
        int second = mCalendar2.second;
        this.mHour = ((float) hour) + (this.mMinute / 60.0f);
        this.mMinute = ((float) minute) + (((float) second) / 60.0f);
        this.mSecond = (float) second;
        this.isChange = true;
    }

    public void runTicker() {
        this.mTicker = new Runnable() {
            public void run() {
                if (!WiiteDefaultClockView.this.mTickerStopped) {
                    WiiteDefaultClockView.this.onTimeChanged(WiiteDefaultClockView.this.mCalendar);
                    if (WatchApp.getClockViewStatus()) {
                        WiiteDefaultClockView.this.invalidate();
                    }
                    long now = SystemClock.uptimeMillis();
                    WiiteDefaultClockView.this.mHandler.postAtTime(WiiteDefaultClockView.this.mTicker, now + (1000 - (now % 1000)));
                }
            }
        };
        this.mTicker.run();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mTickerStopped = true;
    }
}
