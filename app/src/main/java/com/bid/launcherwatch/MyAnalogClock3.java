package com.bid.launcherwatch;


import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.bid.launcherwatch.WatchApp.installedClock;
import com.bid.launcherwatch.view.ClockInfo;
import com.bid.launcherwatch.view.ClockInfo.Num;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MyAnalogClock3 extends WiiteWatchFace {
    private int SCREEN_WIDE = 400;
    private int centerX1;
    private int centerY1;
    List<ClockInfo> clockInfos = null;
    /* access modifiers changed from: private */
    public int hour;
    private List listNum1;
    private List listNum2;
    /* access modifiers changed from: private */
    public boolean mAnimationEnd = false;
    /* access modifiers changed from: private */
    public int mBatteryLevel;
    private Time mCalendar;
    /* access modifiers changed from: private */
    public int mCalorCount;
    private Camera mCamera;
    private Matrix mCameraMatrix;
    /* access modifiers changed from: private */
    public float mCameraRotateX = 10.0f;
    /* access modifiers changed from: private */
    public float mCameraRotateY = 10.0f;
    /* access modifiers changed from: private */
    public boolean mChanged;
    private ValueAnimator mClockAnim;
    private ClockSkinConfigure mClockSkinConfigure = new ClockSkinConfigure();
    private String mClockskinPath = null;
    private Context mContext;
    /* access modifiers changed from: private */
    public int mDate;
    private int mDialHeight;
    private int mDialWidth;
    private Drawable mDrawBattery;
    private Drawable mDrawBatteryGray;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler();
    /* access modifiers changed from: private */
    public float mHour;
    /* access modifiers changed from: private */
    public float mHour24;
    /* access modifiers changed from: private */
    public long mMilSecond;
    /* access modifiers changed from: private */
    public float mMinutes;
    /* access modifiers changed from: private */
    public int mMonth;
    private Paint mPaint = new Paint();
    Rect mRect = new Rect();
    RectF mRectF = new RectF();
    /* access modifiers changed from: private */
    public float mSecond;
    /* access modifiers changed from: private */
    public int mSecondHandDuring = 60;
    private ValueAnimator mShakeAnim;
    private int mStep = 0;
    /* access modifiers changed from: private */
    public int mStepCount;
    /* access modifiers changed from: private */
    public Runnable mTicker;
    /* access modifiers changed from: private */
    public boolean mTickerStopped = false;
    /* access modifiers changed from: private */
    public int mWeek;
    /* access modifiers changed from: private */
    public int mYear;
    private int maxWidth = 0;
    /* access modifiers changed from: private */
    public int minute;
    private Calendar moonPhaseCalendar;
    private int moonPhaseType = -1;
    private List<ClockInfo> parseClock;
    private String pkg;
    private Resources r;
    private float scale = 1.0f;
    /* access modifiers changed from: private */
    public int second;
    private List<ClockInfo> touchClock;

    public class ClockSkinConfigure {
        private boolean SmoothRun = true;

        public ClockSkinConfigure() {
        }

        public void setSmoothRun(boolean smoothRun) {
            this.SmoothRun = smoothRun;
        }
    }

    public /* bridge */ /* synthetic */ float distance(float x1, float y1, float x2, float y2) {
        return super.distance(x1, y1, x2, y2);
    }

    public MyAnalogClock3(Context context, String clockskinPath) {
        super(context);
        this.mContext = context;
        this.mClockskinPath = clockskinPath;
        init();
    }

    /* access modifiers changed from: 0000 */
    public void onTouch(float x, float y) {
        if (this.touchClock != null && this.touchClock.size() > 0) {
            for (int i = 0; i < this.touchClock.size(); i++) {
                try {
                    String centerX = ((ClockInfo) this.touchClock.get(i)).getCenterX();
                    if (centerX == null) {
                        centerX = "0";
                    }
                    int iCenterX = Integer.valueOf(centerX).intValue();
                    String centerY = ((ClockInfo) this.touchClock.get(i)).getCenterY();
                    if (centerY == null) {
                        centerY = "0";
                    }
                    int iCenterY = Integer.valueOf(centerY).intValue();
                    String range = ((ClockInfo) this.touchClock.get(i)).getRange();
                    if (range == null) {
                        range = "30";
                    }
                    int iRange = Integer.valueOf(range).intValue();
                    int xl = this.centerX1 + iCenterX;
                    int yl = this.centerY1 + iCenterY;
                    String cls = ((ClockInfo) this.touchClock.get(i)).getCls();
                    String pkg2 = ((ClockInfo) this.touchClock.get(i)).getPkg();
                    if (!(cls == null || pkg2 == null)) {
                        if (distance(x, y, (float) xl, (float) yl) <= ((float) iRange)) {
                            Intent intent = new Intent();
                            ComponentName componentName = new ComponentName(pkg2, cls);
                            intent.setComponent(componentName);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                            try {
                                this.mContext.startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                            }
                        }
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    public void init() {
        this.listNum1 = new ArrayList();
        for (int i = 0; i < 20; i++) {
            this.listNum1.add(Integer.valueOf(0));
        }
        this.listNum2 = new ArrayList();
        for (int i2 = 0; i2 < 20; i2++) {
            this.listNum2.add(Integer.valueOf(0));
        }
        this.pkg = get_cur_theme_package(this.mContext);
        this.r = getResource(this.mContext, this.pkg);
        this.touchClock = new ArrayList();
        Log.i("qs_ClockInfo","path:"+mClockskinPath);
        this.parseClock = parseClockSinXML(this.r, this.pkg, "clock_skin");
        parseTouchClock(this.parseClock);
        Log.d("qs_ClockInfo", "parseClock-" + this.parseClock.size());
        this.mDialWidth = this.SCREEN_WIDE;
        this.mDialHeight = this.SCREEN_WIDE;
        if (this.mCalendar == null) {
            this.mCalendar = new Time();
        }
        this.mDrawBattery = getContext().getResources().getDrawable(R.drawable.clock_battery_panel);
        this.mDrawBatteryGray = getContext().getResources().getDrawable(R.drawable.clock_battery_panel_gray);
        this.mCameraMatrix = new Matrix();
        this.mCamera = new Camera();
    }

    private XmlResourceParser getXmlRes(Resources r2, String pkg2, String id_name) {
        int ID = r2.getIdentifier(id_name, "xml", pkg2);
        if (ID != 0) {
            return r2.getXml(ID);
        }
        return null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001a, code lost:
        r5 = r7.next();
        r1 = r0;
     */
    private void parseXML(Resources r2, String pkg2, String xml, ClockInfo clock) {
        XmlPullParser parser;
        Num clocknum=new Num();
        List<Num> nums = new ArrayList<>();
        try {
            if (this.mClockskinPath == null) {
                parser = getXmlRes(r2, pkg2, xml);
            } else {
                parser = getXmlParser(xml);
                if (parser == null) {
                    Log.e("xiaocai", "parser == null");
                    return;
                }
            }
            int eventType = parser.getEventType();
            Num clocknum2 = null;
            while (eventType != 1) {
                switch (eventType) {
                    case 0:
                        try {
                            Log.d("qs_parseXML", "eventType-0");
                            //clocknum = clocknum2;
                            eventType=parser.next();
                            break;
                        } catch (Exception e) {
                            Num num = clocknum2;
                            Log.w("MyAnalogClock3", "Got XmlPullParserException while parsing toppackage.", e);
                        }

                    case 2:

                        ClockInfo clockInfo = new ClockInfo();
                        clockInfo.getClass();
                        clocknum = new Num();
                        Log.d("qs_parseXML", "eventType-2 "+parser.getName()+" "+parser.getText());
                        if (!parser.getName().equals("image")) {
                            eventType=parser.next();
                            break;
                        } else {
                            //int eventType2 = parser.next();
                            if(parser.next() == XmlPullParser.TEXT)
                            {
                                clocknum.setNumDrawable(getDrawableRes(r2, pkg2, parser.getText()));
                                Log.d("qs_parseXML", "NumDrawable-" + parser.getText());
                                //nums.add(clocknum);
                                eventType=parser.next();
                            }
                            break;
                        }
                    case 3:

                        Log.d("qs_parseXML", "eventType-3");
                        if (parser.getName().equals("image")) {
                            nums.add(clocknum);
                            clock.setNums(nums);
                        }
                        clocknum = clocknum2;
                        eventType=parser.next();
                        break;
                    default:
                        eventType=parser.next();
                        clocknum = clocknum2;
                        break;
                }
            }
        } catch (XmlPullParserException e3) {
            //e = e3;
            Log.w("MyAnalogClock3", "Got XmlPullParserException while parsing toppackage.", e3);
        } catch (IOException e4) {
            //e = e4;
            Log.w("MyAnalogClock3", "Got IOException while parsing toppackage.", e4);
        }
    }

    private void parseTouchClock(List<ClockInfo> clock) {
        if (clock != null && clock.size() > 0) {
            for (int i = 0; i < clock.size(); i++) {
                String arraytype = ((ClockInfo) clock.get(i)).getArraytype();
                if (arraytype == null) {
                    arraytype = "0";
                }
                if (arraytype.equals("100")) {
                    this.touchClock.add((ClockInfo) clock.get(i));
                }
            }
        }
    }


    private List<ClockInfo> parseClockSinXML(Resources r2, String pkg2, String xml) {
        XmlPullParser parser;
        ClockInfo clockInfo;
        try {
            if (this.mClockskinPath == null) {
                parser = getXmlRes(r2, pkg2, xml);
            } else {
                parser = getXmlParser(xml);
            }
            int eventType = parser.getEventType();
            ClockInfo clock = new ClockInfo();
            while (eventType != 1) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        Log.d("qs_parseClockSinXML", "eventType-0");
                        this.clockInfos = new ArrayList();
                        clockInfo = clock;
                        eventType=parser.next();
                        break;

                    case XmlPullParser.START_TAG:
                        Log.d("qs_parseClockSinXML", "eventType-2");
                        if (!parser.getName().equals("drawable")) {
                            if (!parser.getName().equals("configure")) {
                                if (!parser.getName().equals("smoothRun")) {
                                    if (!parser.getName().equals("name")) {
                                        if (!parser.getName().equals("centerX")) {
                                            if (!parser.getName().equals("centerY")) {
                                                if (!parser.getName().equals("rotate")) {
                                                    if (!parser.getName().equals("angle")) {
                                                        if (!parser.getName().equals("arraytype")) {
                                                            if (!parser.getName().equals("mulrotate")) {
                                                                if (!parser.getName().equals("startAngle")) {
                                                                    if (!parser.getName().equals("direction")) {
                                                                        if (!parser.getName().equals("textsize")) {
                                                                            if (!parser.getName().equals("textcolor")) {
                                                                                if (!parser.getName().equals("colorarray")) {
                                                                                    if (!parser.getName().equals("color")) {
                                                                                        if (!parser.getName().equals("width")) {
                                                                                            if (!parser.getName().equals("radius")) {
                                                                                                if (!parser.getName().equals("rotatemode")) {
                                                                                                    if (!parser.getName().equals("cls")) {
                                                                                                        if (!parser.getName().equals("pkg")) {
                                                                                                            if (parser.getName().equals("range")) {
                                                                                                                if(parser.next() == XmlPullParser.TEXT)
                                                                                                                {
                                                                                                                    clock.setRange(parser.getText());
                                                                                                                    Log.d("qs_parseClockSinXML", "range-" + parser.getText());
                                                                                                                    clockInfo = clock;

                                                                                                                }
                                                                                                                eventType = parser.next();
                                                                                                                break;
                                                                                                            }
                                                                                                        } else {
                                                                                                            if(parser.next() == XmlPullParser.TEXT)
                                                                                                            {
                                                                                                                clock.setPkg(parser.getText());
                                                                                                                Log.d("qs_parseClockSinXML", "pkg-" + parser.getText());
                                                                                                            }


                                                                                                            clockInfo = clock;
                                                                                                            eventType = parser.next();
                                                                                                            break;
                                                                                                        }
                                                                                                    } else {

                                                                                                        if(parser.next() == XmlPullParser.TEXT)
                                                                                                        {
                                                                                                            clock.setCls(parser.getText());
                                                                                                            Log.d("qs_parseClockSinXML", "cls-" + parser.getText());
                                                                                                        }

                                                                                                        clockInfo = clock;
                                                                                                        eventType = parser.next();
                                                                                                        break;
                                                                                                    }
                                                                                                } else {

                                                                                                    if(parser.next() == XmlPullParser.TEXT)
                                                                                                    {
                                                                                                        clock.setRotatemode(parser.getText());
                                                                                                        Log.d("qs_parseClockSinXML", "radius-" + parser.getText());
                                                                                                    }
                                                                                                    clockInfo = clock;
                                                                                                    eventType = parser.next();
                                                                                                    break;
                                                                                                }
                                                                                            } else {

                                                                                                if(parser.next() == XmlPullParser.TEXT)
                                                                                                {
                                                                                                    clock.setRadius(parser.getText());
                                                                                                    Log.d("qs_parseClockSinXML", "radius-" + parser.getText());
                                                                                                }

                                                                                                clockInfo = clock;
                                                                                                eventType = parser.next();
                                                                                                break;
                                                                                            }
                                                                                        } else {

                                                                                            if(parser.next() == XmlPullParser.TEXT)
                                                                                            {
                                                                                                clock.setWidth(parser.getText());
                                                                                                Log.d("qs_parseClockSinXML", "width-" + parser.getText());
                                                                                            }

                                                                                            clockInfo = clock;
                                                                                            eventType = parser.next();
                                                                                            break;
                                                                                        }
                                                                                    } else {

                                                                                        if(parser.next() == XmlPullParser.TEXT)
                                                                                        {
                                                                                            clock.setColor(parser.getText());
                                                                                            Log.d("qs_parseClockSinXML", "color-" + parser.getText());
                                                                                        }

                                                                                        clockInfo = clock;
                                                                                        eventType = parser.next();
                                                                                        break;
                                                                                    }
                                                                                } else {

                                                                                    if(parser.next() == XmlPullParser.TEXT)
                                                                                    {
                                                                                        clock.setColorArray(parser.getText());
                                                                                        Log.d("qs_parseClockSinXML", "colorarray-" + parser.getText());
                                                                                    }

                                                                                    clockInfo = clock;
                                                                                    eventType = parser.next();
                                                                                    break;
                                                                                }
                                                                            } else {

                                                                                if(parser.next() == XmlPullParser.TEXT)
                                                                                {
                                                                                    clock.setTextcolor(parser.getText());
                                                                                    Log.d("qs_parseClockSinXML", "textcolor-" + parser.getText());
                                                                                }

                                                                                clockInfo = clock;
                                                                                eventType = parser.next();
                                                                                break;
                                                                            }
                                                                        } else {

                                                                            if(parser.next() == XmlPullParser.TEXT)
                                                                            {
                                                                                clock.setTextsize(parser.getText());
                                                                                Log.d("qs_parseClockSinXML", "textsize-" + parser.getText());
                                                                            }

                                                                            clockInfo = clock;
                                                                            eventType = parser.next();
                                                                            break;
                                                                        }
                                                                    } else {

                                                                        if(parser.next() == XmlPullParser.TEXT)
                                                                        {
                                                                            clock.setDirection(parser.getText());
                                                                            Log.d("qs_parseClockSinXML", "direction-" + parser.getText());
                                                                        }

                                                                        clockInfo = clock;
                                                                        eventType = parser.next();
                                                                        break;
                                                                    }
                                                                } else {

                                                                    if(parser.next() == XmlPullParser.TEXT)
                                                                    {
                                                                        clock.setStartAngle(parser.getText());
                                                                        Log.d("qs_parseClockSinXML", "startAngle-" + parser.getText());
                                                                    }

                                                                    clockInfo = clock;
                                                                    eventType = parser.next();
                                                                    break;
                                                                }
                                                            } else {

                                                                if(parser.next() == XmlPullParser.TEXT)
                                                                {
                                                                    clock.setMulrotate(parser.getText());
                                                                    Log.d("qs_parseClockSinXML", "mulrotate-" + parser.getText());
                                                                }

                                                                clockInfo = clock;
                                                                eventType = parser.next();
                                                                break;
                                                            }
                                                        } else {

                                                            if(parser.next() == XmlPullParser.TEXT)
                                                            {
                                                                clock.setArraytype(parser.getText());
                                                                Log.d("qs_parseClockSinXML", "Arraytype-" + parser.getText());
                                                            }

                                                            clockInfo = clock;
                                                            eventType = parser.next();
                                                            break;
                                                        }
                                                    } else {

                                                        if(parser.next() == XmlPullParser.TEXT)
                                                        {
                                                            clock.setAngle(parser.getText());
                                                            Log.d("qs_parseClockSinXML", "angle-" + parser.getText());
                                                        }

                                                        clockInfo = clock;
                                                        eventType = parser.next();
                                                        break;
                                                    }
                                                } else {

                                                    if(parser.next() == XmlPullParser.TEXT)
                                                    {
                                                        clock.setRotate(parser.getText());
                                                        Log.d("qs_parseClockSinXML", "rotate-" + parser.getText());
                                                    }

                                                    clockInfo = clock;
                                                    eventType = parser.next();
                                                    break;
                                                }
                                            } else {

                                                if(parser.next() == XmlPullParser.TEXT)
                                                {
                                                    clock.setCenterY(parser.getText());
                                                    Log.d("qs_parseClockSinXML", "CenterY-" + parser.getText());
                                                }

                                                clockInfo = clock;
                                                eventType = parser.next();
                                                break;
                                            }
                                        } else {

                                            if(parser.next() == XmlPullParser.TEXT)
                                            {
                                                clock.setCenterX(parser.getText());
                                                Log.d("qs_parseClockSinXML", "CenterX-" + parser.getText());
                                            }

                                            clockInfo = clock;
                                            eventType = parser.next();
                                            break;
                                        }
                                    } else {

                                        String name;
                                        if(parser.next() == XmlPullParser.TEXT)
                                        {
                                            name = parser.getText();
                                            Log.d("qs_parseClockSinXML", "name-" + name);

                                            clock.setName(name);
                                            if (!name.endsWith(".xml")) {
                                                clock.setNamepng(getDrawableRes(r2, pkg2, name));
                                                clockInfo = clock;
                                                eventType = parser.next();
                                                break;
                                            } else {
                                                String namexml = name.split("\\.")[0];
                                                Log.d("qs_parseClockSinXML", "namexml-" + namexml);
                                                parseXML(r2, pkg2, namexml, clock);
                                                clockInfo = clock;
                                                eventType = parser.next();
                                                break;
                                            }
                                        }


                                    }
                                } else {
                                    boolean smoothRun;
                                    if(parser.next() == XmlPullParser.TEXT)
                                    {
                                        smoothRun = !parser.getText().equals("false");
                                        this.mClockSkinConfigure.setSmoothRun(smoothRun);
                                        this.mSecondHandDuring = smoothRun ? this.mSecondHandDuring : 1000;
                                        Log.d("qs_parseClockSinXML", "smooth-" + parser.getText());
                                    }

                                    clockInfo = clock;
                                    eventType = parser.next();
                                    break;
                                }
                            }
                        } else {
                            eventType=parser.next();
                            clockInfo = new ClockInfo();
                            clock=new ClockInfo();
                            break;
                        }
                    case XmlPullParser.END_TAG:

                        Log.d("qs_parseClockSinXML", "eventType-3");
                        if (parser.getName().equals("drawable")) {
                            this.clockInfos.add(clock);
                            clockInfo = null;
                            eventType=parser.next();
                            break;
                        }
                    default:
                        try {
                            eventType=parser.next();
                            Log.d("qs_parseClockSinXML", "eventType-9");
                            clockInfo = clock;
                            break;
                        } catch (Exception e) {
                            e = e;
                            ClockInfo clockInfo2 = clock;
                            Log.w("MyAnalogClock3", "Got XmlPullParserException while parsing toppackage.", e);
                            return this.clockInfos;
                        }

                }
            }
        } catch (XmlPullParserException e3) {

            Log.w("MyAnalogClock3", "Got XmlPullParserException while parsing toppackage.", e3);
            return this.clockInfos;
        } catch (IOException e4) {

            Log.w("MyAnalogClock3", "Got IOException while parsing toppackage.", e4);
            return this.clockInfos;
        }
        return this.clockInfos;
    }

    /* access modifiers changed from: private */
    public void onTimeChanged() {
        this.mCalendar.setToNow();
        this.hour = this.mCalendar.hour;
        this.minute = this.mCalendar.minute;
        this.second = this.mCalendar.second;
        this.mYear = this.mCalendar.year;
        this.mMonth = this.mCalendar.month;
        this.mWeek = this.mCalendar.weekDay;
        this.mDate = this.mCalendar.monthDay;
        this.mMilSecond = System.currentTimeMillis() % 60000;
        this.mSecond = (float) this.second;
        this.mMinutes = ((float) this.minute) + (((float) this.second) / 60.0f);
        this.mHour = ((float) this.hour) + (this.mMinutes / 60.0f) + (this.mSecond / 3600.0f);
        this.mHour24 = this.mHour;
        this.mBatteryLevel = (int) WatchApp.getBatteryLevel(this.mContext);
        this.mChanged = true;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        this.mTickerStopped = false;
        super.onAttachedToWindow();
        animationClock();
        startShakeAnim();
    }

    private void animationClock() {
        this.mSecond = 34.0f;
        this.mMinutes = (this.mSecond / 60.0f) + 9.0f;
        this.mHour = (this.mMinutes / 60.0f) + 10.0f + (this.mSecond / 3600.0f);
        this.mMilSecond = ((long) this.mSecond) * 1000;
        this.mCalendar.set(System.currentTimeMillis() + 1000 + 400);
        this.hour = this.mCalendar.hour;
        this.minute = this.mCalendar.minute;
        this.second = this.mCalendar.second;
        this.mYear = this.mCalendar.year;
        this.mMonth = this.mCalendar.month;
        this.mWeek = this.mCalendar.weekDay;
        this.mDate = this.mCalendar.monthDay;
        float toSecond = (float) this.mCalendar.second;
        float toMinutes = ((float) this.mCalendar.minute) + (toSecond / 60.0f);
        float toHour = ((float) this.mCalendar.hour) + (toMinutes / 60.0f) + (toSecond / 3600.0f);
        this.mBatteryLevel = (int) WatchApp.getBatteryLevel(this.mContext);
        this.mStepCount = WatchApp.getSteps(this.mContext);
        this.mCalorCount = (int) Float.parseFloat(WatchApp.getCalories(this.mContext));
        float toHourAnalog = toHour < 4.0f ? toHour + 12.0f : toHour > 14.0f ? toHour - 12.0f : toHour;
        this.mMinutes = toMinutes > 40.0f ? this.mMinutes + 60.0f : this.mMinutes;
        if (toSecond < 5.0f) {
            toSecond += 60.0f;
        }
        this.mClockAnim = ValueAnimator.ofPropertyValuesHolder(new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat("HOUR", new float[]{this.mHour, toHourAnalog}), PropertyValuesHolder.ofFloat("HOUR24", new float[]{this.mHour, toHour}), PropertyValuesHolder.ofFloat("MINUTES", new float[]{this.mMinutes, toMinutes}), PropertyValuesHolder.ofFloat("SECOND", new float[]{this.mSecond, toSecond}), PropertyValuesHolder.ofInt("YEAR", new int[]{2000, this.mYear}), PropertyValuesHolder.ofInt("MONTH", new int[]{0, this.mMonth}), PropertyValuesHolder.ofInt("DATE", new int[]{1, this.mDate}), PropertyValuesHolder.ofInt("WEEK", new int[]{0, this.mWeek}), PropertyValuesHolder.ofInt("BATTERY", new int[]{0, this.mBatteryLevel}), PropertyValuesHolder.ofInt("STEPS", new int[]{0, this.mStepCount}), PropertyValuesHolder.ofInt("CALC", new int[]{0, this.mCalorCount})});
        this.mClockAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        this.mClockAnim.setDuration(1000);
        this.mClockAnim.setRepeatCount(0);
        this.mClockAnim.setRepeatMode(ValueAnimator.RESTART);
        ValueAnimator valueAnimator = this.mClockAnim;
        AnimatorUpdateListener r0 = new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                MyAnalogClock3.this.mHour = ((Float) animation.getAnimatedValue("HOUR")).floatValue();
                MyAnalogClock3.this.mHour24 = ((Float) animation.getAnimatedValue("HOUR24")).floatValue();
                MyAnalogClock3.this.mMinutes = ((Float) animation.getAnimatedValue("MINUTES")).floatValue();
                if (MyAnalogClock3.this.mMinutes > 60.0f) {
                    MyAnalogClock3 myAnalogClock3 = MyAnalogClock3.this;
                    myAnalogClock3.mMinutes = myAnalogClock3.mMinutes - 60.0f;
                }
                MyAnalogClock3.this.mSecond = ((Float) animation.getAnimatedValue("SECOND")).floatValue();
                if (MyAnalogClock3.this.mSecond > 60.0f) {
                    MyAnalogClock3 myAnalogClock32 = MyAnalogClock3.this;
                    myAnalogClock32.mSecond = myAnalogClock32.mSecond - 60.0f;
                }
                MyAnalogClock3.this.mMilSecond = (long) (MyAnalogClock3.this.mSecond * 1000.0f);
                MyAnalogClock3.this.hour = (int) MyAnalogClock3.this.mHour24;
                MyAnalogClock3.this.minute = (int) MyAnalogClock3.this.mMinutes;
                MyAnalogClock3.this.second = (int) MyAnalogClock3.this.mSecond;
                MyAnalogClock3.this.mYear = ((Integer) animation.getAnimatedValue("YEAR")).intValue();
                MyAnalogClock3.this.mMonth = ((Integer) animation.getAnimatedValue("MONTH")).intValue();
                MyAnalogClock3.this.mDate = ((Integer) animation.getAnimatedValue("DATE")).intValue();
                MyAnalogClock3.this.mWeek = ((Integer) animation.getAnimatedValue("WEEK")).intValue();
                MyAnalogClock3.this.mBatteryLevel = ((Integer) animation.getAnimatedValue("BATTERY")).intValue();
                MyAnalogClock3.this.mStepCount = ((Integer) animation.getAnimatedValue("STEPS")).intValue();
                MyAnalogClock3.this.mCalorCount = ((Integer) animation.getAnimatedValue("CALC")).intValue();
                MyAnalogClock3.this.mChanged = true;
                MyAnalogClock3.this.invalidate();
            }
        };
        valueAnimator.addUpdateListener(r0);
        ValueAnimator valueAnimator2 = this.mClockAnim;
        AnimatorListener r02 = new AnimatorListener() {
            public void onAnimationStart(Animator arg0) {
            }

            public void onAnimationRepeat(Animator arg0) {
            }

            public void onAnimationEnd(Animator arg0) {
                MyAnalogClock3.this.mAnimationEnd = true;
                MyAnalogClock3.this.mTicker = new Runnable() {
                    public void run() {
                        if (!MyAnalogClock3.this.mTickerStopped) {
                            MyAnalogClock3.this.onTimeChanged();
                            if (WatchApp.getClockViewStatus()) {
                                MyAnalogClock3.this.invalidate();
                            }
                            long now = SystemClock.uptimeMillis();
                            MyAnalogClock3.this.mHandler.postAtTime(MyAnalogClock3.this.mTicker, now + (((long) MyAnalogClock3.this.mSecondHandDuring) - (now % ((long) MyAnalogClock3.this.mSecondHandDuring))));
                        }
                    }
                };
                MyAnalogClock3.this.mTicker.run();
            }

            public void onAnimationCancel(Animator arg0) {
            }
        };
        valueAnimator2.addListener(r02);
        this.mClockAnim.start();
    }

    private void startShakeAnim() {
        String str = "cameraRotateX";
        String str2 = "cameraRotateY";
        String str3 = "canvasTranslateX";
        String str4 = "canvasTranslateY";
        this.mShakeAnim = ValueAnimator.ofPropertyValuesHolder(new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat("cameraRotateX", new float[]{this.mCameraRotateX, 0.0f}), PropertyValuesHolder.ofFloat("cameraRotateY", new float[]{this.mCameraRotateY, 0.0f})});
        this.mShakeAnim.setInterpolator(new TimeInterpolator() {
            public float getInterpolation(float input) {
                return (float) ((Math.pow(2.0d, (double) (-2.0f * input)) * Math.sin((((double) (input - 0.14285725f)) * 6.283185307179586d) / 0.5714290142059326d)) + 1.0d);
            }
        });
        this.mShakeAnim.setDuration(1000);
        this.mShakeAnim.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                MyAnalogClock3.this.mCameraRotateX = ((Float) animation.getAnimatedValue("cameraRotateX")).floatValue();
                MyAnalogClock3.this.mCameraRotateY = ((Float) animation.getAnimatedValue("cameraRotateY")).floatValue();
            }
        });
        this.mShakeAnim.start();
    }

    private void setCameraRotate(Canvas canvas) {
        this.mCameraMatrix.reset();
        this.mCamera.save();
        this.mCamera.rotateX(this.mCameraRotateX);
        this.mCamera.rotateY(this.mCameraRotateY);
        this.mCamera.getMatrix(this.mCameraMatrix);
        this.mCamera.restore();
        this.mCameraMatrix.preTranslate((float) ((-getWidth()) / 2), (float) ((-getHeight()) / 2));
        this.mCameraMatrix.postTranslate((float) (getWidth() / 2), (float) (getHeight() / 2));
        canvas.concat(this.mCameraMatrix);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mTickerStopped = true;
        if (this.mClockAnim != null) {
            this.mClockAnim.cancel();
        }
        if (this.mShakeAnim != null) {
            this.mShakeAnim.cancel();
        }
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        float hScale = 1.0f;
        float vScale = 1.0f;
        if (widthMode != 0 && widthSize < this.mDialWidth) {
            hScale = ((float) widthSize) / ((float) this.mDialWidth);
        }
        if (heightMode != 0 && heightSize < this.mDialHeight) {
            vScale = ((float) heightSize) / ((float) this.mDialHeight);
        }
        float scale2 = Math.min(hScale, vScale);
        setMeasuredDimension(resolveSizeAndState((int) (((float) this.mDialWidth) * scale2), widthMeasureSpec, 0), resolveSizeAndState((int) (((float) this.mDialHeight) * scale2), heightMeasureSpec, 0));
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mChanged = true;
        resetScreen(getRight() - getLeft());
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0117, code lost:
        if (r133.equals("0") != false) goto L_0x0119;
     */
    public void onDraw(Canvas canvas) {
        Drawable drawable6;
        Drawable drawable7;
        Drawable drawable9;
        Drawable drawable10;
        Drawable drawable1;
        Drawable drawableRes2;
        Drawable drawableRes3;
        Drawable drawableRes1;
        Drawable drawableRes22;
        Drawable drawableRes12;
        Drawable drawableRes23;
        Drawable drawableRes13;
        Drawable drawableRes24;
        Drawable drawable12;
        Drawable drawable2;
        Drawable drawable4;
        Drawable drawable5;
        Drawable drawableRes14;
        Drawable drawableRes25;
        Drawable drawable13;
        Drawable drawable22;
        Drawable drawable42;
        Drawable drawable52;
        Drawable drawable62;
        Drawable drawable72;
        Drawable drawable92;
        Drawable drawable102;
        setCameraRotate(canvas);
        int availableWidth = getRight() - getLeft();
        int availableHeight = getBottom() - getTop();
        this.centerX1 = availableWidth / 2;
        this.centerY1 = availableHeight / 2;
        int w = this.maxWidth;
        int h = this.maxWidth;
        boolean scaled = false;
        if (availableWidth < w || availableHeight < h) {
            scaled = true;
            this.scale = Math.min(((float) availableWidth) / ((float) w), ((float) availableHeight) / ((float) h));
            canvas.save();
            canvas.scale(this.scale, this.scale, (float) this.centerX1, (float) this.centerY1);
        } else if (availableWidth > w && availableHeight > h) {
            scaled = true;
            this.scale = Math.min(((float) availableWidth) / ((float) w), ((float) availableHeight) / ((float) h));
            canvas.save();
            canvas.scale(this.scale, this.scale, (float) this.centerX1, (float) this.centerY1);
        }
        for (int i = 0; i < this.parseClock.size(); i++) {
            try {
                String name = ((ClockInfo) this.parseClock.get(i)).getName();
                String centerX = ((ClockInfo) this.parseClock.get(i)).getCenterX();
                if (centerX == null) {
                    centerX = "0";
                }
                int iCenterX = Integer.valueOf(centerX).intValue();
                String centerY = ((ClockInfo) this.parseClock.get(i)).getCenterY();
                if (centerY == null) {
                    centerY = "0";
                }
                int iCenterY = Integer.valueOf(centerY).intValue();
                String rotate = ((ClockInfo) this.parseClock.get(i)).getRotate();
                String angle = ((ClockInfo) this.parseClock.get(i)).getAngle();
                if (angle == null) {
                    angle = "0";
                }
                String arraytype = ((ClockInfo) this.parseClock.get(i)).getArraytype();
                String mulrotate = ((ClockInfo) this.parseClock.get(i)).getMulrotate();
                if (mulrotate != null) {
                }
                mulrotate = "1";
                String color = ((ClockInfo) this.parseClock.get(i)).getColor();
                if (color == null) {
                    color = "0";
                }
                String width = ((ClockInfo) this.parseClock.get(i)).getWidth();
                if (width == null) {
                    width = "5";
                }
                String radius = ((ClockInfo) this.parseClock.get(i)).getRadius();
                if (radius == null) {
                    radius = "50";
                }
                String startAngle = ((ClockInfo) this.parseClock.get(i)).getStartAngle();
                if (startAngle == null) {
                    startAngle = "0";
                }
                String direction = ((ClockInfo) this.parseClock.get(i)).getDirection();
                if (direction == null) {
                    direction = "1";
                }
                String textsize = ((ClockInfo) this.parseClock.get(i)).getTextsize();
                if (textsize == null) {
                    textsize = "18";
                }
                String colorarray = ((ClockInfo) this.parseClock.get(i)).getColorArray();
                String rotatemode = ((ClockInfo) this.parseClock.get(i)).getRotatemode();
                if (rotatemode == null) {
                    rotatemode = "3";
                }
                List<Num> nums = ((ClockInfo) this.parseClock.get(i)).getNums();
                if (arraytype == null) {
                    Drawable drawableRes = ((ClockInfo) this.parseClock.get(i)).getNamepng();
                    if (rotate != null) {
                        if (rotate.equals("1")) {
                            drawHourhand(canvas, angle, this.mHour, Integer.parseInt(mulrotate), Integer.parseInt(direction), drawableRes, this.mChanged, iCenterX, iCenterY);
                        } else if (rotate.equals("2")) {
                            drawMinuteHand(canvas, angle, this.mMinutes, Integer.parseInt(mulrotate), Integer.parseInt(direction), drawableRes, this.mChanged, iCenterX, iCenterY);
                        } else {
                            if (rotate.equals("3")) {
                                Log.d("qs_onDraw", "onDraw_3_second");
                                drawSecondHand(canvas, angle, Integer.parseInt(mulrotate), Integer.parseInt(direction), drawableRes, this.mChanged, iCenterX, iCenterY);
                            } else {
                                if (rotate.equals("4")) {
                                    drawMonthhand(canvas, startAngle, this.mMonth, drawableRes, this.mChanged, iCenterX, iCenterY);
                                } else {
                                    if (rotate.equals("5")) {
                                        drawWeekhand(canvas, startAngle, this.mWeek, drawableRes, this.mChanged, iCenterX, iCenterY);
                                    } else {
                                        if (rotate.equals("6")) {
                                            drawBatteryhand(canvas, angle, startAngle, Integer.parseInt(direction), Integer.parseInt(mulrotate), this.mBatteryLevel, drawableRes, this.mChanged, iCenterX, iCenterY);
                                        } else {
                                            if (rotate.equals("7")) {
                                                draw24Hourhand(canvas, angle, this.mHour24, drawableRes, this.mChanged, iCenterX, iCenterY);
                                            } else {
                                                if (rotate.equals("8")) {
                                                    drawHourhandShadow(canvas, angle, this.mHour, drawableRes, this.mChanged, iCenterX, iCenterY);
                                                } else {
                                                    if (rotate.equals("9")) {
                                                        drawMinuteHandShadow(canvas, angle, this.mMinutes, drawableRes, this.mChanged, iCenterX, iCenterY);
                                                    } else {
                                                        if (rotate.equals("10")) {
                                                            drawSecondHandShadow(canvas, angle, Integer.parseInt(mulrotate), Integer.parseInt(direction), drawableRes, this.mChanged, iCenterX, iCenterY);
                                                        } else {
                                                            if (rotate.equals("11")) {
                                                                drawMonthDayhand(canvas, startAngle, Integer.parseInt(direction), this.mDate, drawableRes, this.mChanged, iCenterX, iCenterY);
                                                            } else {
                                                                if (rotate.equals("12")) {
                                                                    drawBatteryhand2(canvas, angle, startAngle, Integer.parseInt(direction), Integer.parseInt(mulrotate), this.mBatteryLevel, drawableRes, this.mChanged, iCenterX, iCenterY);
                                                                } else {
                                                                    if (rotate.equals("13")) {
                                                                        drawRotateModeHand(canvas, rotatemode, angle, startAngle, Integer.parseInt(direction), Integer.parseInt(mulrotate), drawableRes, this.mChanged, iCenterX, iCenterY);
                                                                    } else {
                                                                        if (rotate.equals("14")) {
                                                                            drawBalanceHand(canvas, Integer.parseInt(mulrotate), drawableRes, this.mChanged, iCenterX, iCenterY);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        drawDial(canvas, iCenterX, iCenterY, drawableRes);
                    }
                } else {
                    if (arraytype.equals("1")) {
                        int b = (this.mYear % 1000) / 100;
                        int c = ((this.mYear % 1000) % 100) / 10;
                        int d = (((this.mYear % 1000) % 100) % 10) % 10;
                        Drawable drawable14 = ((Num) nums.get(this.mYear / 1000)).getNumDrawable();
                        Drawable drawable23 = ((Num) nums.get(b)).getNumDrawable();
                        Drawable drawable3 = ((Num) nums.get(c)).getNumDrawable();
                        Drawable drawable43 = ((Num) nums.get(d)).getNumDrawable();
                        Drawable drawable53 = ((Num) nums.get(10)).getNumDrawable();
                        if (this.mMonth + 1 < 10) {
                            drawable62 = ((Num) nums.get(0)).getNumDrawable();
                            drawable72 = ((Num) nums.get(this.mMonth + 1)).getNumDrawable();
                        } else {
                            drawable62 = ((Num) nums.get((this.mMonth + 1) / 10)).getNumDrawable();
                            drawable72 = ((Num) nums.get((this.mMonth + 1) % 10)).getNumDrawable();
                        }
                        Drawable drawable8 = ((Num) nums.get(10)).getNumDrawable();
                        if (this.mDate < 10) {
                            drawable92 = ((Num) nums.get(0)).getNumDrawable();
                            drawable102 = ((Num) nums.get(this.mDate)).getNumDrawable();
                        } else {
                            drawable92 = ((Num) nums.get(this.mDate / 10)).getNumDrawable();
                            drawable102 = ((Num) nums.get(this.mDate % 10)).getNumDrawable();
                        }
                        drawDrawable10(canvas, iCenterX, iCenterY, drawable14, drawable23, drawable3, drawable43, drawable53, drawable62, drawable72, drawable8, drawable92, drawable102);
                    } else {
                        if (arraytype.equals("2")) {
                            if (this.mMonth + 1 < 10) {
                                drawable13 = ((Num) nums.get(0)).getNumDrawable();
                                drawable22 = ((Num) nums.get(this.mMonth + 1)).getNumDrawable();
                            } else {
                                drawable13 = ((Num) nums.get((this.mMonth + 1) / 10)).getNumDrawable();
                                drawable22 = ((Num) nums.get((this.mMonth + 1) % 10)).getNumDrawable();
                            }
                            Drawable drawable32 = ((Num) nums.get(10)).getNumDrawable();
                            if (this.mDate < 10) {
                                drawable42 = ((Num) nums.get(0)).getNumDrawable();
                                drawable52 = ((Num) nums.get(this.mDate)).getNumDrawable();
                            } else {
                                drawable42 = ((Num) nums.get(this.mDate / 10)).getNumDrawable();
                                drawable52 = ((Num) nums.get(this.mDate % 10)).getNumDrawable();
                            }
                            drawDrawable5(canvas, true, iCenterX, iCenterY, drawable13, drawable22, drawable32, drawable42, drawable52);
                        } else {
                            if (arraytype.equals("3")) {
                                drawDial(canvas, iCenterX, iCenterY, ((Num) nums.get(this.mMonth)).getNumDrawable());
                            } else {
                                if (arraytype.equals("4")) {
                                    if (this.mDate < 10) {
                                        drawableRes14 = ((Num) nums.get(0)).getNumDrawable();
                                        drawableRes25 = ((Num) nums.get(this.mDate)).getNumDrawable();
                                    } else {
                                        drawableRes14 = ((Num) nums.get(this.mDate / 10)).getNumDrawable();
                                        drawableRes25 = ((Num) nums.get(this.mDate % 10)).getNumDrawable();
                                    }
                                    drawDrawable2(canvas, iCenterX, iCenterY, drawableRes14, drawableRes25);
                                } else {
                                    if (arraytype.equals("5")) {
                                        drawDial(canvas, iCenterX, iCenterY, ((Num) nums.get(this.mWeek)).getNumDrawable());
                                    } else {
                                        if (arraytype.equals("6")) {
                                            Drawable drawable63 = null;
                                            if (!DateFormat.is24HourFormat(this.mContext)) {
                                                if (nums.size() > 11) {
                                                    if (this.mCalendar.hour < 12) {
                                                        drawable63 = ((Num) nums.get(11)).getNumDrawable();
                                                    } else {
                                                        drawable63 = ((Num) nums.get(12)).getNumDrawable();
                                                    }
                                                }
                                                if (this.hour >= 12) {
                                                    this.hour -= 12;
                                                }
                                                if (this.hour == 0) {
                                                    this.hour = 12;
                                                }
                                            }
                                            if (this.hour < 10) {
                                                drawable12 = ((Num) nums.get(0)).getNumDrawable();
                                                drawable2 = ((Num) nums.get(this.hour)).getNumDrawable();
                                            } else {
                                                drawable12 = ((Num) nums.get(this.hour / 10)).getNumDrawable();
                                                drawable2 = ((Num) nums.get(this.hour % 10)).getNumDrawable();
                                            }
                                            Drawable drawable33 = ((Num) nums.get(10)).getNumDrawable();
                                            if (this.minute < 10) {
                                                drawable4 = ((Num) nums.get(0)).getNumDrawable();
                                                drawable5 = ((Num) nums.get(this.minute)).getNumDrawable();
                                            } else {
                                                drawable4 = ((Num) nums.get(this.minute / 10)).getNumDrawable();
                                                drawable5 = ((Num) nums.get(this.minute % 10)).getNumDrawable();
                                            }
                                            drawDrawable6(canvas, false, iCenterX, iCenterY, drawable12, drawable2, drawable33, drawable4, drawable5, drawable63);
                                        } else {
                                            if (arraytype.equals("7")) {
                                                if (!DateFormat.is24HourFormat(this.mContext)) {
                                                    if (this.hour >= 12) {
                                                        this.hour -= 12;
                                                    }
                                                    if (this.hour == 0) {
                                                        this.hour = 12;
                                                    }
                                                }
                                                if (this.hour < 10) {
                                                    drawableRes13 = ((Num) nums.get(0)).getNumDrawable();
                                                    drawableRes24 = ((Num) nums.get(this.hour)).getNumDrawable();
                                                } else {
                                                    drawableRes13 = ((Num) nums.get(this.hour / 10)).getNumDrawable();
                                                    drawableRes24 = ((Num) nums.get(this.hour % 10)).getNumDrawable();
                                                }
                                                drawDrawable2(canvas, iCenterX, iCenterY, drawableRes13, drawableRes24);
                                            } else {
                                                if (arraytype.equals("8")) {
                                                    if (this.minute < 10) {
                                                        drawableRes12 = ((Num) nums.get(0)).getNumDrawable();
                                                        drawableRes23 = ((Num) nums.get(this.minute)).getNumDrawable();
                                                    } else {
                                                        drawableRes12 = ((Num) nums.get(this.minute / 10)).getNumDrawable();
                                                        drawableRes23 = ((Num) nums.get(this.minute % 10)).getNumDrawable();
                                                    }
                                                    drawDrawable2(canvas, iCenterX, iCenterY, drawableRes12, drawableRes23);
                                                } else {
                                                    if (arraytype.equals("9")) {
                                                        if (this.second < 10) {
                                                            drawableRes1 = ((Num) nums.get(0)).getNumDrawable();
                                                            drawableRes22 = ((Num) nums.get(this.second)).getNumDrawable();
                                                        } else {
                                                            drawableRes1 = ((Num) nums.get(this.second / 10)).getNumDrawable();
                                                            drawableRes22 = ((Num) nums.get(this.second % 10)).getNumDrawable();
                                                        }
                                                        drawDrawable2(canvas, iCenterX, iCenterY, drawableRes1, drawableRes22);
                                                    } else {
                                                        if (arraytype.equals("10")) {
                                                            int WeatherIcon = WatchApp.getWeatherIcon(getContext());
                                                            Drawable drawable15 = ((Num) nums.get(0)).getNumDrawable();
                                                            if (WeatherIcon == 9 || WeatherIcon == 10 || WeatherIcon == 27 || WeatherIcon == 28 || WeatherIcon == 1) {
                                                                drawable15 = ((Num) nums.get(0)).getNumDrawable();
                                                            } else if (WeatherIcon == 2 || WeatherIcon == 3 || WeatherIcon == 4 || WeatherIcon == 5) {
                                                                drawable15 = ((Num) nums.get(0)).getNumDrawable();
                                                            } else if (WeatherIcon == 6 || WeatherIcon == 7 || WeatherIcon == 8 || WeatherIcon == 38) {
                                                                drawable15 = ((Num) nums.get(1)).getNumDrawable();
                                                            } else if (WeatherIcon == 11) {
                                                                drawable15 = ((Num) nums.get(3)).getNumDrawable();
                                                            } else if (WeatherIcon == 12 || WeatherIcon == 39 || WeatherIcon == 40) {
                                                                drawable15 = ((Num) nums.get(5)).getNumDrawable();
                                                            } else if (WeatherIcon == 13 || WeatherIcon == 14) {
                                                                drawable15 = ((Num) nums.get(5)).getNumDrawable();
                                                            } else if (WeatherIcon == 15 || WeatherIcon == 16 || WeatherIcon == 41 || WeatherIcon == 42) {
                                                                drawable15 = ((Num) nums.get(7)).getNumDrawable();
                                                            } else if (WeatherIcon == 17) {
                                                                drawable15 = ((Num) nums.get(7)).getNumDrawable();
                                                            } else if (WeatherIcon == 18) {
                                                                drawable15 = ((Num) nums.get(4)).getNumDrawable();
                                                            } else if (WeatherIcon == 19 || WeatherIcon == 43) {
                                                                drawable15 = ((Num) nums.get(8)).getNumDrawable();
                                                            } else if (WeatherIcon == 20 || WeatherIcon == 21) {
                                                                drawable15 = ((Num) nums.get(8)).getNumDrawable();
                                                            } else if (WeatherIcon == 22 || WeatherIcon == 23) {
                                                                drawable15 = ((Num) nums.get(8)).getNumDrawable();
                                                            } else if (WeatherIcon == 24 || WeatherIcon == 25 || WeatherIcon == 44) {
                                                                drawable15 = ((Num) nums.get(8)).getNumDrawable();
                                                            } else if (WeatherIcon == 26 || WeatherIcon == 29) {
                                                                drawable15 = ((Num) nums.get(6)).getNumDrawable();
                                                            } else if (WeatherIcon == 30) {
                                                                drawable15 = ((Num) nums.get(10)).getNumDrawable();
                                                            } else if (WeatherIcon == 31) {
                                                                drawable15 = ((Num) nums.get(11)).getNumDrawable();
                                                            } else if (WeatherIcon == 32) {
                                                                drawable15 = ((Num) nums.get(12)).getNumDrawable();
                                                            } else if (WeatherIcon == 33) {
                                                                drawable15 = ((Num) nums.get(0)).getNumDrawable();
                                                            } else {
                                                                if (!(WeatherIcon == 34 || WeatherIcon == 35 || WeatherIcon == 36)) {
                                                                    if (WeatherIcon == 37) {
                                                                    }
                                                                }
                                                                drawable15 = ((Num) nums.get(0)).getNumDrawable();
                                                            }
                                                            drawDial(canvas, iCenterX, iCenterY, drawable15);
                                                        } else {
                                                            if (arraytype.equals("11")) {
                                                                int temp = WatchApp.getWeatherTemp(this.mContext);
                                                                boolean minus = false;
                                                                if (temp < 0) {
                                                                    minus = true;
                                                                    temp = Math.abs(temp);
                                                                }
                                                                Drawable drawableRes15 = ((Num) nums.get(10)).getNumDrawable();
                                                                if (Math.abs(temp) < 10) {
                                                                    drawableRes2 = ((Num) nums.get(0)).getNumDrawable();
                                                                    drawableRes3 = ((Num) nums.get(temp)).getNumDrawable();
                                                                } else {
                                                                    drawableRes2 = ((Num) nums.get(temp / 10)).getNumDrawable();
                                                                    drawableRes3 = ((Num) nums.get(temp % 10)).getNumDrawable();
                                                                }
                                                                drawDrawable4(canvas, minus, iCenterX, iCenterY, drawableRes15, drawableRes2, drawableRes3, ((Num) nums.get(11)).getNumDrawable());
                                                            } else {
                                                                if (arraytype.equals("12")) {
                                                                    if (this.mAnimationEnd) {
                                                                        this.mStepCount = WatchApp.getSteps(this.mContext);
                                                                    }
                                                                    drawDrawable5(canvas, true, iCenterX, iCenterY, ((Num) nums.get(this.mStepCount / 10000)).getNumDrawable(), ((Num) nums.get((this.mStepCount % 10000) / 1000)).getNumDrawable(), ((Num) nums.get(((this.mStepCount % 10000) % 1000) / 100)).getNumDrawable(), ((Num) nums.get((((this.mStepCount % 10000) % 1000) % 100) / 10)).getNumDrawable(), ((Num) nums.get(((((this.mStepCount % 10000) % 1000) % 100) % 10) % 10)).getNumDrawable());
                                                                } else {
                                                                    if (arraytype.equals("13")) {
                                                                        int rate = WatchApp.getRate(this.mContext);
                                                                        int a = rate / 100;
                                                                        int b2 = (rate % 100) / 10;
                                                                        int c2 = ((rate % 100) % 10) % 10;
                                                                        if (nums != null) {
                                                                            drawDrawable3(canvas, iCenterX, iCenterY, ((Num) nums.get(a)).getNumDrawable(), ((Num) nums.get(b2)).getNumDrawable(), ((Num) nums.get(c2)).getNumDrawable());
                                                                        }
                                                                    } else {
                                                                        if (arraytype.equals("14")) {
                                                                            int a2 = this.mBatteryLevel / 100;
                                                                            int b3 = (this.mBatteryLevel % 100) / 10;
                                                                            int c3 = ((this.mBatteryLevel % 100) % 10) % 10;
                                                                            if (a2 == 0) {
                                                                                drawable1 = ((Num) nums.get(10)).getNumDrawable();
                                                                            } else {
                                                                                drawable1 = ((Num) nums.get(a2)).getNumDrawable();
                                                                            }
                                                                            Drawable drawable24 = ((Num) nums.get(b3)).getNumDrawable();
                                                                            Drawable drawable34 = ((Num) nums.get(c3)).getNumDrawable();
                                                                            Drawable drawable44 = null;
                                                                            if (nums.size() == 12) {
                                                                                drawable44 = ((Num) nums.get(11)).getNumDrawable();
                                                                            }
                                                                            drawDrawable4(canvas, true, iCenterX, iCenterY, drawable1, drawable24, drawable34, drawable44);
                                                                        } else {
                                                                            if (!arraytype.equals("15")) {
                                                                                if (arraytype.equals("16")) {
                                                                                    drawDrawable4(canvas, true, iCenterX, iCenterY, ((Num) nums.get(this.mYear / 1000)).getNumDrawable(), ((Num) nums.get((this.mYear % 1000) / 100)).getNumDrawable(), ((Num) nums.get(((this.mYear % 1000) % 100) / 10)).getNumDrawable(), ((Num) nums.get((((this.mYear % 1000) % 100) % 10) % 10)).getNumDrawable());
                                                                                } else {
                                                                                    if (arraytype.equals("17")) {
                                                                                        ArrayList arrayList = new ArrayList();
                                                                                        for (Num n : nums) {
                                                                                            arrayList.add(n.getNumDrawable());
                                                                                        }
                                                                                        drawBatteryPictureWithCircle(canvas, arrayList, this.centerX1 + iCenterX, this.centerY1 + iCenterY, this.mBatteryLevel, colorarray, 200, false);
                                                                                    } else {
                                                                                        if (arraytype.equals("18")) {
                                                                                            if (this.mAnimationEnd) {
                                                                                                this.mStepCount = WatchApp.getSteps(this.mContext);
                                                                                            }
                                                                                            ArrayList arrayList2 = new ArrayList();
                                                                                            for (Num n2 : nums) {
                                                                                                arrayList2.add(n2.getNumDrawable());
                                                                                            }
                                                                                            drawStepsPictureWithCircle(this.mContext, canvas, Float.valueOf(Float.parseFloat(radius)), arrayList2, this.centerX1 + iCenterX, this.centerY1 + iCenterY, this.mStepCount, ((ClockInfo) this.parseClock.get(i)).getColorArray(), false);
                                                                                        } else {
                                                                                            if (arraytype.equals("19")) {
                                                                                                int moonPhaseType2 = getMoonPhaseType();
                                                                                                if (moonPhaseType2 >= 0 && moonPhaseType2 < nums.size()) {
                                                                                                    drawClockQuietPircture(canvas, ((Num) nums.get(moonPhaseType2)).getNumDrawable(), this.centerX1 + iCenterX, this.centerY1 + iCenterY, true);
                                                                                                }
                                                                                            } else {
                                                                                                if (arraytype.equals("31")) {
                                                                                                    drawDrawable4(canvas, true, iCenterX, iCenterY, ((Num) nums.get(this.mYear / 1000)).getNumDrawable(), ((Num) nums.get((this.mYear % 1000) / 100)).getNumDrawable(), ((Num) nums.get(((this.mYear % 1000) % 100) / 10)).getNumDrawable(), ((Num) nums.get((((this.mYear % 1000) % 100) % 10) % 10)).getNumDrawable());
                                                                                                } else {
                                                                                                    if (!arraytype.equals("32")) {
                                                                                                        if (arraytype.equals("50")) {
                                                                                                            int rate2 = WatchApp.getUnreadPhone(this.mContext);
                                                                                                            if (rate2 > 99) {
                                                                                                                rate2 = 99;
                                                                                                            }
                                                                                                            int b4 = rate2 % 10;
                                                                                                            Drawable drawableRes16 = ((Num) nums.get(rate2 / 10)).getNumDrawable();
                                                                                                            Drawable drawableRes26 = ((Num) nums.get(b4)).getNumDrawable();
                                                                                                            if (rate2 > 0) {
                                                                                                                drawDrawable2(canvas, iCenterX, iCenterY, drawableRes16, drawableRes26);
                                                                                                            }
                                                                                                        } else {
                                                                                                            if (arraytype.equals("51")) {
                                                                                                                int rate3 = WatchApp.getUnreadSMS(this.mContext);
                                                                                                                if (rate3 > 99) {
                                                                                                                    rate3 = 99;
                                                                                                                }
                                                                                                                int b5 = rate3 % 10;
                                                                                                                Drawable drawableRes17 = ((Num) nums.get(rate3 / 10)).getNumDrawable();
                                                                                                                Drawable drawableRes27 = ((Num) nums.get(b5)).getNumDrawable();
                                                                                                                if (rate3 > 0) {
                                                                                                                    drawDrawable2(canvas, iCenterX, iCenterY, drawableRes17, drawableRes27);
                                                                                                                }
                                                                                                            } else {
                                                                                                                if (arraytype.equals("52")) {
                                                                                                                    drawBatteryCircle(this.mContext, canvas, this.centerX1 + iCenterX, this.centerY1 + iCenterY, this.mBatteryLevel, width, radius, colorarray, false);
                                                                                                                } else {
                                                                                                                    if (arraytype.equals("53")) {
                                                                                                                        if (this.mAnimationEnd) {
                                                                                                                            this.mStepCount = WatchApp.getSteps(this.mContext);
                                                                                                                        }
                                                                                                                        drawStepsPictureWithCircle2(this.mContext, canvas, this.centerX1 + iCenterX, this.centerY1 + iCenterY, this.mStepCount, width, radius, ((ClockInfo) this.parseClock.get(i)).getColorArray(), Integer.parseInt(direction));
                                                                                                                    } else {
                                                                                                                        if (arraytype.equals("54")) {
                                                                                                                            if (this.mAnimationEnd) {
                                                                                                                                this.mCalorCount = (int) Float.parseFloat(WatchApp.getCalories(this.mContext));
                                                                                                                            }
                                                                                                                            drawDrawable4(canvas, true, iCenterX, iCenterY, ((Num) nums.get(this.mCalorCount / 1000)).getNumDrawable(), ((Num) nums.get((this.mCalorCount % 1000) / 100)).getNumDrawable(), ((Num) nums.get(((this.mCalorCount % 1000) % 100) / 10)).getNumDrawable(), ((Num) nums.get((((this.mCalorCount % 1000) % 100) % 10) % 10)).getNumDrawable());
                                                                                                                        } else {
                                                                                                                            if (arraytype.equals("55")) {
                                                                                                                                int rate4 = WatchApp.getUnreadPhone(this.mContext) + WatchApp.getUnreadSMS(this.mContext);
                                                                                                                                int b6 = rate4 % 10;
                                                                                                                                Drawable drawableRes18 = ((Num) nums.get(rate4 / 10)).getNumDrawable();
                                                                                                                                Drawable drawableRes28 = ((Num) nums.get(b6)).getNumDrawable();
                                                                                                                                if (rate4 > 0) {
                                                                                                                                    drawDrawable2(canvas, iCenterX, iCenterY, drawableRes18, drawableRes28);
                                                                                                                                }
                                                                                                                            } else {
                                                                                                                                if (arraytype.equals("56")) {
                                                                                                                                    if (this.mAnimationEnd) {
                                                                                                                                        this.mStepCount = WatchApp.getSteps(this.mContext);
                                                                                                                                    }
                                                                                                                                    drawStepsCircle(this.mContext, canvas, this.centerX1 + iCenterX, this.centerY1 + iCenterY, this.mStepCount, WatchApp.getTargetSteps(this.mContext), width, radius, ((ClockInfo) this.parseClock.get(i)).getColorArray(), Integer.parseInt(direction), angle, startAngle, Integer.parseInt(mulrotate));
                                                                                                                                } else {
                                                                                                                                    if (arraytype.equals("57")) {
                                                                                                                                        if (this.mAnimationEnd) {
                                                                                                                                            this.mCalorCount = (int) Float.parseFloat(WatchApp.getCalories(this.mContext));
                                                                                                                                        }
                                                                                                                                        drawKcalCircle(this.mContext, canvas, this.centerX1 + iCenterX, this.centerY1 + iCenterY, (float) this.mCalorCount, (float) WatchApp.getTargetCalories(this.mContext), width, radius, ((ClockInfo) this.parseClock.get(i)).getColorArray(), Integer.parseInt(direction), angle, startAngle, Integer.parseInt(mulrotate));
                                                                                                                                    } else {
                                                                                                                                        if (arraytype.equals("58")) {
                                                                                                                                            drawPowerCircle(this.mContext, canvas, this.centerX1 + iCenterX, this.centerY1 + iCenterY, this.mBatteryLevel, width, radius, ((ClockInfo) this.parseClock.get(i)).getColorArray(), Integer.parseInt(direction), angle, startAngle, Integer.parseInt(mulrotate));
                                                                                                                                        } else {
                                                                                                                                            if (arraytype.equals("59")) {
                                                                                                                                                drawDisCircle(this.mContext, canvas, this.centerX1 + iCenterX, this.centerY1 + iCenterY, Float.parseFloat(WatchApp.getDistance(this.mContext)), WatchApp.getTargetDistance(this.mContext), width, radius, ((ClockInfo) this.parseClock.get(i)).getColorArray(), Integer.parseInt(direction), angle, startAngle, Integer.parseInt(mulrotate));
                                                                                                                                            } else {
                                                                                                                                                if (arraytype.equals("60")) {
                                                                                                                                                    int d2 = (int) (10.0f * Float.parseFloat(WatchApp.getDistance(this.mContext)));
                                                                                                                                                    drawDisDrawable4(canvas, true, iCenterX, iCenterY, ((Num) nums.get(d2 / 100)).getNumDrawable(), ((Num) nums.get((d2 % 100) / 10)).getNumDrawable(), ((Num) nums.get(10)).getNumDrawable(), ((Num) nums.get(((d2 % 100) % 10) % 10)).getNumDrawable());
                                                                                                                                                } else {
                                                                                                                                                    if (arraytype.equals("61")) {
                                                                                                                                                        int n3 = 100 / (nums.size() - 1);
                                                                                                                                                        Drawable drawableRes4 = ((Num) nums.get(0)).getNumDrawable();
                                                                                                                                                        int index = 0;
                                                                                                                                                        while (true) {
                                                                                                                                                            if (index < nums.size() - 1) {
                                                                                                                                                                if (index * n3 < this.mBatteryLevel && this.mBatteryLevel <= (index + 1) * n3) {
                                                                                                                                                                    drawableRes4 = ((Num) nums.get(index + 1)).getNumDrawable();
                                                                                                                                                                    break;
                                                                                                                                                                }
                                                                                                                                                                index++;
                                                                                                                                                            } else {
                                                                                                                                                                break;
                                                                                                                                                            }
                                                                                                                                                        }
                                                                                                                                                        if (this.mBatteryLevel < 5) {
                                                                                                                                                            drawableRes4 = ((Num) nums.get(0)).getNumDrawable();
                                                                                                                                                        }
                                                                                                                                                        if (this.mBatteryLevel == 100) {
                                                                                                                                                            drawableRes4 = ((Num) nums.get(nums.size() - 1)).getNumDrawable();
                                                                                                                                                        }
                                                                                                                                                        drawDial(canvas, iCenterX, iCenterY, drawableRes4);
                                                                                                                                                    } else {
                                                                                                                                                        if (arraytype.equals("62")) {
                                                                                                                                                            int anim = Integer.parseInt(color);
                                                                                                                                                            int mul = Integer.parseInt(mulrotate);
                                                                                                                                                            Drawable numDrawable = ((Num) nums.get(0)).getNumDrawable();
                                                                                                                                                            int index1 = ((Integer) this.listNum1.get(anim)).intValue();
                                                                                                                                                            int index2 = ((Integer) this.listNum2.get(anim)).intValue();
                                                                                                                                                            if (index1 > nums.size() - 1) {
                                                                                                                                                                index1 = 0;
                                                                                                                                                            }
                                                                                                                                                            if (index2 > mul) {
                                                                                                                                                                drawDial(canvas, iCenterX, iCenterY, ((Num) nums.get(index1)).getNumDrawable());
                                                                                                                                                                this.listNum1.set(anim, Integer.valueOf(index1 + 1));
                                                                                                                                                                index2 = 0;
                                                                                                                                                            }
                                                                                                                                                            this.listNum2.set(anim, Integer.valueOf(index2 + 1));
                                                                                                                                                        } else {
                                                                                                                                                            if (arraytype.equals("97")) {
                                                                                                                                                                drawPedometer(this.mContext, canvas, Integer.parseInt(startAngle), Integer.parseInt(direction), Integer.parseInt(textsize), WatchApp.getSteps(this.mContext));
                                                                                                                                                            } else {
                                                                                                                                                                if (arraytype.equals("98")) {
                                                                                                                                                                    drawHeartRate(this.mContext, canvas, Integer.parseInt(startAngle), Integer.parseInt(direction), Integer.parseInt(textsize), WatchApp.getRate(this.mContext));
                                                                                                                                                                } else {
                                                                                                                                                                    if (!arraytype.equals("99")) {
                                                                                                                                                                        if (arraytype.equals("101")) {
                                                                                                                                                                            int year = this.mCalendar.year;
                                                                                                                                                                            int b7 = (year % 1000) / 100;
                                                                                                                                                                            int c4 = ((year % 1000) % 100) / 10;
                                                                                                                                                                            int d3 = (((year % 1000) % 100) % 10) % 10;
                                                                                                                                                                            Drawable numDrawable2 = ((Num) nums.get(year / 1000)).getNumDrawable();
                                                                                                                                                                            Drawable numDrawable3 = ((Num) nums.get(b7)).getNumDrawable();
                                                                                                                                                                            Drawable drawable35 = ((Num) nums.get(c4)).getNumDrawable();
                                                                                                                                                                            Drawable drawable45 = ((Num) nums.get(d3)).getNumDrawable();
                                                                                                                                                                            Drawable drawable54 = ((Num) nums.get(10)).getNumDrawable();
                                                                                                                                                                            if (this.mMonth + 1 < 10) {
                                                                                                                                                                                drawable6 = ((Num) nums.get(0)).getNumDrawable();
                                                                                                                                                                                drawable7 = ((Num) nums.get(this.mMonth + 1)).getNumDrawable();
                                                                                                                                                                            } else {
                                                                                                                                                                                drawable6 = ((Num) nums.get((this.mMonth + 1) / 10)).getNumDrawable();
                                                                                                                                                                                drawable7 = ((Num) nums.get((this.mMonth + 1) % 10)).getNumDrawable();
                                                                                                                                                                            }
                                                                                                                                                                            Drawable drawable82 = ((Num) nums.get(10)).getNumDrawable();
                                                                                                                                                                            if (this.mDate < 10) {
                                                                                                                                                                                drawable9 = ((Num) nums.get(0)).getNumDrawable();
                                                                                                                                                                                drawable10 = ((Num) nums.get(this.mDate)).getNumDrawable();
                                                                                                                                                                            } else {
                                                                                                                                                                                drawable9 = ((Num) nums.get(this.mDate / 10)).getNumDrawable();
                                                                                                                                                                                drawable10 = ((Num) nums.get(this.mDate % 10)).getNumDrawable();
                                                                                                                                                                            }
                                                                                                                                                                            drawDrawable8(canvas, iCenterX, iCenterY, drawable35, drawable45, drawable54, drawable6, drawable7, drawable82, drawable9, drawable10);
                                                                                                                                                                        }
                                                                                                                                                                    } else if (WatchApp.getIsBatteryCharging(this.mContext)) {
                                                                                                                                                                        boolean isWhite = color.equals("0");
                                                                                                                                                                        drawChargingInfo(this.mContext, canvas, isWhite ? this.mDrawBattery : this.mDrawBatteryGray, this.centerX1 + iCenterX, this.centerY1 + iCenterY, isWhite ? -1 : -16777216, true);
                                                                                                                                                                    }
                                                                                                                                                                }
                                                                                                                                                            }
                                                                                                                                                        }
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            } else if (colorarray != null) {
                                                                                drawSpecialSecond(canvas, colorarray, this.minute, this.second);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
        if (scaled) {
            canvas.restore();
        }
        this.mChanged = false;
    }

    private void resetScreen(int availableWidth) {
        this.SCREEN_WIDE = availableWidth;
    }

    public void drawHourhand(Canvas canvas, String angle, float mHour2, int mulrotate, int direction, Drawable hourHand, boolean changed, int iCenterX, int iCenterY) {
        Float startAngle = Float.valueOf(angle);
        if (hourHand != null) {
            canvas.save();
            if (direction == 1) {
                canvas.rotate(startAngle.floatValue() + ((mHour2 / 12.0f) * 360.0f * ((float) mulrotate)), (float) (this.centerX1 + iCenterX), (float) (this.centerY1 + iCenterY));
            } else if (direction == 2) {
                canvas.rotate(startAngle.floatValue() - (((mHour2 / 12.0f) * 360.0f) * ((float) mulrotate)), (float) (this.centerX1 + iCenterX), (float) (this.centerY1 + iCenterY));
            }
            if (changed) {
                int w = hourHand.getIntrinsicWidth();
                int h = hourHand.getIntrinsicHeight();
                hourHand.setBounds((this.centerX1 + iCenterX) - (w / 2), (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w / 2), this.centerY1 + iCenterY + (h / 2));
            }
            hourHand.draw(canvas);
            canvas.restore();
        }
    }

    public void draw24Hourhand(Canvas canvas, String angle, float mHour2, Drawable hourHand, boolean changed, int iCenterX, int iCenterY) {
        Float startAngle = Float.valueOf(angle);
        if (hourHand != null) {
            canvas.save();
            canvas.rotate(startAngle.floatValue() + ((mHour2 / 24.0f) * 360.0f), (float) (this.centerX1 + iCenterX), (float) (this.centerY1 + iCenterY));
            if (changed) {
                int w = hourHand.getIntrinsicWidth();
                int h = hourHand.getIntrinsicHeight();
                hourHand.setBounds((this.centerX1 + iCenterX) - (w / 2), (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w / 2), this.centerY1 + iCenterY + (h / 2));
            }
            hourHand.draw(canvas);
            canvas.restore();
        }
    }

    public void drawMinuteHand(Canvas canvas, String angle, float mMinutes2, int mulrotate, int direction, Drawable minuteHand, boolean changed, int iCenterX, int iCenterY) {
        Float startAngle = Float.valueOf(angle);
        if (minuteHand != null) {
            canvas.save();
            if (direction == 1) {
                canvas.rotate(startAngle.floatValue() + ((mMinutes2 / 60.0f) * 360.0f * ((float) mulrotate)), (float) (this.centerX1 + iCenterX), (float) (this.centerY1 + iCenterY));
            } else if (direction == 2) {
                canvas.rotate(startAngle.floatValue() - (((mMinutes2 / 60.0f) * 360.0f) * ((float) mulrotate)), (float) (this.centerX1 + iCenterX), (float) (this.centerY1 + iCenterY));
            }
            if (changed) {
                int w = minuteHand.getIntrinsicWidth();
                int h = minuteHand.getIntrinsicHeight();
                minuteHand.setBounds((this.centerX1 + iCenterX) - (w / 2), (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w / 2), this.centerY1 + iCenterY + (h / 2));
            }
            minuteHand.draw(canvas);
            canvas.restore();
        }
    }

    public void drawSecondHand(Canvas canvas, String startAngle, int mulrotate, int direction, Drawable mSecondHand, boolean changed, int iCenterX, int iCenterY) {
        Float startAngleF = Float.valueOf(startAngle);
        long millis = this.mMilSecond;
        if (mSecondHand != null) {
            canvas.save();
            if (direction == 1) {
                canvas.rotate(startAngleF.floatValue() + ((((float) (((long) mulrotate) * millis)) * 6.0f) / 1000.0f), (float) (this.centerX1 + iCenterX), (float) (this.centerY1 + iCenterY));
            } else if (direction == 2) {
                canvas.rotate(startAngleF.floatValue() - (((((float) millis) * 6.0f) * ((float) mulrotate)) / 1000.0f), (float) (this.centerX1 + iCenterX), (float) (this.centerY1 + iCenterY));
            }
            if (changed) {
                int w = mSecondHand.getIntrinsicWidth();
                int h = mSecondHand.getIntrinsicHeight();
                mSecondHand.setBounds((this.centerX1 + iCenterX) - (w / 2), (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w / 2), this.centerY1 + iCenterY + (h / 2));
            }
            mSecondHand.draw(canvas);
            canvas.restore();
        }
    }

    public void drawBalanceHand(Canvas canvas, int mulrotate, Drawable mSecondHand, boolean changed, int iCenterX, int iCenterY) {
        long now = System.currentTimeMillis();
        long j = now % 60000;
        long millis2 = now % 2000;
        if (mSecondHand != null) {
            canvas.save();
            if (millis2 < 1000) {
                canvas.rotate(((((float) millis2) * 6.0f) * ((float) mulrotate)) / 1000.0f, (float) (this.centerX1 + iCenterX), (float) (this.centerY1 + iCenterY));
            } else {
                canvas.rotate(((((float) mulrotate) * 6000.0f) / 1000.0f) - (((((float) (millis2 - 1000)) * 6.0f) * ((float) mulrotate)) / 1000.0f), (float) (this.centerX1 + iCenterX), (float) (this.centerY1 + iCenterY));
            }
            if (changed) {
                int w = mSecondHand.getIntrinsicWidth();
                int h = mSecondHand.getIntrinsicHeight();
                mSecondHand.setBounds((this.centerX1 + iCenterX) - (w / 2), (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w / 2), this.centerY1 + iCenterY + (h / 2));
            }
            mSecondHand.draw(canvas);
            canvas.restore();
        }
    }

    public void drawMonthhand(Canvas canvas, String angle, int month, Drawable hourHand, boolean changed, int iCenterX, int iCenterY) {
        Float startAngle = Float.valueOf(angle);
        if (hourHand != null) {
            canvas.save();
            canvas.rotate(startAngle.floatValue() + ((((float) (month + 1)) / 12.0f) * 360.0f), (float) (this.centerX1 + iCenterX), (float) (this.centerY1 + iCenterY));
            if (changed) {
                int w = hourHand.getIntrinsicWidth();
                int h = hourHand.getIntrinsicHeight();
                hourHand.setBounds((this.centerX1 + iCenterX) - (w / 2), (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w / 2), this.centerY1 + iCenterY + (h / 2));
            }
            hourHand.draw(canvas);
            canvas.restore();
        }
    }

    public void drawMonthDayhand(Canvas canvas, String angle, int direction, int monthday, Drawable hourHand, boolean changed, int iCenterX, int iCenterY) {
        int monthday2 = monthday - 1;
        Float startAngle = Float.valueOf(angle);
        if (direction == 2) {
            monthday2 = -monthday2;
        }
        if (hourHand != null) {
            canvas.save();
            canvas.rotate(startAngle.floatValue() + ((((float) monthday2) / 31.0f) * 360.0f), (float) (this.centerX1 + iCenterX), (float) (this.centerY1 + iCenterY));
            if (changed) {
                int w = hourHand.getIntrinsicWidth();
                int h = hourHand.getIntrinsicHeight();
                hourHand.setBounds((this.centerX1 + iCenterX) - (w / 2), (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w / 2), this.centerY1 + iCenterY + (h / 2));
            }
            hourHand.draw(canvas);
            canvas.restore();
        }
    }

    public void drawWeekhand(Canvas canvas, String angle, int week, Drawable hourHand, boolean changed, int iCenterX, int iCenterY) {
        Float startAngle = Float.valueOf(angle);
        if (hourHand != null) {
            canvas.save();
            canvas.rotate(startAngle.floatValue() + ((((float) (week - 1)) / 7.0f) * 360.0f), (float) (this.centerX1 + iCenterX), (float) (this.centerY1 + iCenterY));
            if (changed) {
                int w = hourHand.getIntrinsicWidth();
                int h = hourHand.getIntrinsicHeight();
                hourHand.setBounds((this.centerX1 + iCenterX) - (w / 2), (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w / 2), this.centerY1 + iCenterY + (h / 2));
            }
            hourHand.draw(canvas);
            canvas.restore();
        }
    }

    public void drawBatteryhand(Canvas canvas, String angle, String startangle, int direction, int mulrotate, int batteryLevel, Drawable hourHand, boolean changed, int iCenterX, int iCenterY) {
        float degrees;
        Float startAngle = Float.valueOf(angle);
        if (hourHand != null) {
            canvas.save();
            if (direction == 2) {
                batteryLevel = -batteryLevel;
            }
            if (mulrotate >= 0) {
                degrees = ((((float) batteryLevel) / 100.0f) * 180.0f * ((float) mulrotate)) + startAngle.floatValue();
            } else {
                degrees = (((((float) batteryLevel) / 100.0f) * 180.0f) / ((float) Math.abs(mulrotate))) + startAngle.floatValue();
            }
            canvas.rotate(degrees, (float) (this.centerX1 + iCenterX), (float) (this.centerY1 + iCenterY));
            if (changed) {
                int w = hourHand.getIntrinsicWidth();
                int h = hourHand.getIntrinsicHeight();
                hourHand.setBounds((this.centerX1 + iCenterX) - (w / 2), (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w / 2), this.centerY1 + iCenterY + (h / 2));
            }
            hourHand.draw(canvas);
            canvas.restore();
        }
    }

    public void drawBatteryhand2(Canvas canvas, String angle, String startangle, int direction, int mulrotate, int batteryLevel, Drawable hourHand, boolean changed, int iCenterX, int iCenterY) {
        float degrees;
        Float startAnglef = Float.valueOf(startangle);
        Float anglef = Float.valueOf(angle);
        if (hourHand != null) {
            canvas.save();
            if (direction == 2) {
                batteryLevel = -batteryLevel;
            }
            if (mulrotate >= 0) {
                degrees = ((((float) batteryLevel) / 100.0f) * anglef.floatValue() * ((float) mulrotate)) + startAnglef.floatValue();
            } else {
                degrees = (((((float) batteryLevel) / 100.0f) * anglef.floatValue()) / ((float) Math.abs(mulrotate))) + startAnglef.floatValue();
            }
            canvas.rotate(degrees, (float) (this.centerX1 + iCenterX), (float) (this.centerY1 + iCenterY));
            if (changed) {
                int w = hourHand.getIntrinsicWidth();
                int h = hourHand.getIntrinsicHeight();
                hourHand.setBounds((this.centerX1 + iCenterX) - (w / 2), (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w / 2), this.centerY1 + iCenterY + (h / 2));
            }
            hourHand.draw(canvas);
            canvas.restore();
        }
    }

    public void drawRotateModeHand(Canvas canvas, String rotateMode, String angle, String startangle, int direction, int mulrotate, Drawable hand, boolean changed, int iCenterX, int iCenterY) {
        float degrees;
        float rate = 0.0f;
        Float startAnglef = Float.valueOf(startangle);
        Float anglef = Float.valueOf(angle);
        int suggestSteps = WatchApp.getTargetSteps(this.mContext);
        int step = WatchApp.getSteps(this.mContext);
        if (rotateMode.equals("1")) {
            if (this.mHour > 12.0f) {
                this.mHour -= 12.0f;
            }
            rate = (this.mHour / 12.0f) * anglef.floatValue();
        } else {
            if (rotateMode.equals("2")) {
                rate = (this.mMinutes / 60.0f) * anglef.floatValue();
            } else {
                if (rotateMode.equals("3")) {
                    rate = (((float) this.mMilSecond) * anglef.floatValue()) / 60000.0f;
                } else {
                    if (rotateMode.equals("4")) {
                        rate = (((float) (this.mMonth + 1)) / 12.0f) * anglef.floatValue();
                    } else {
                        if (rotateMode.equals("5")) {
                            rate = (((float) (this.mWeek - 1)) / 7.0f) * anglef.floatValue();
                        } else {
                            if (rotateMode.equals("6")) {
                                rate = (((float) ((int) WatchApp.getBatteryLevel(this.mContext))) / 100.0f) * anglef.floatValue();
                            } else {
                                if (rotateMode.equals("7")) {
                                    rate = (this.mHour / 24.0f) * anglef.floatValue();
                                } else {
                                    if (rotateMode.equals("8")) {
                                        rate = (((float) step) / ((float) suggestSteps)) * anglef.floatValue();
                                    } else {
                                        if (rotateMode.equals("11")) {
                                            rate = (((float) this.mDate) / 31.0f) * anglef.floatValue();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (hand != null) {
            canvas.save();
            if (direction == 2) {
                rate = -rate;
            }
            if (mulrotate >= 0) {
                degrees = (((float) mulrotate) * rate) + startAnglef.floatValue();
            } else {
                degrees = (rate / ((float) Math.abs(mulrotate))) + startAnglef.floatValue();
            }
            canvas.rotate(degrees, (float) (this.centerX1 + iCenterX), (float) (this.centerY1 + iCenterY));
            if (changed) {
                int w = hand.getIntrinsicWidth();
                int h = hand.getIntrinsicHeight();
                hand.setBounds((this.centerX1 + iCenterX) - (w / 2), (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w / 2), this.centerY1 + iCenterY + (h / 2));
            }
            hand.draw(canvas);
            canvas.restore();
        }
    }

    private void drawDial(Canvas canvas, int iCenterX, int iCenterY, Drawable dial) {
        if (dial != null) {
            int w = dial.getIntrinsicWidth();
            int h = dial.getIntrinsicHeight();
            if (this.mChanged) {
                dial.setBounds((this.centerX1 + iCenterX) - (w / 2), (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w / 2), this.centerY1 + iCenterY + (h / 2));
            }
            dial.draw(canvas);
        }
    }

    private void drawDrawable2(Canvas canvas, int iCenterX, int iCenterY, Drawable drawable1, Drawable drawable2) {
        if (drawable2 != null) {
            int w = drawable2.getIntrinsicWidth();
            int h = drawable2.getIntrinsicHeight();
            if (this.mChanged) {
                drawable1.setBounds((this.centerX1 + iCenterX) - w, (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX, this.centerY1 + iCenterY + (h / 2));
                drawable1.draw(canvas);
                drawable2.setBounds(this.centerX1 + iCenterX, (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + w, this.centerY1 + iCenterY + (h / 2));
                drawable2.draw(canvas);
            }
        }
    }

    private void drawDrawable3(Canvas canvas, int iCenterX, int iCenterY, Drawable drawable1, Drawable drawable2, Drawable drawable3) {
        int w = drawable1.getIntrinsicWidth();
        int h = drawable1.getIntrinsicHeight();
        int startX = (this.centerX1 + iCenterX) - ((w * 3) / 2);
        int startY = (this.centerY1 + iCenterY) - (h / 2);
        if (this.mChanged) {
            drawable1.setBounds(startX, startY, startX + w, startY + h);
            drawable1.draw(canvas);
            drawable2.setBounds(startX + w, startY, (w * 2) + startX, startY + h);
            drawable2.draw(canvas);
            drawable3.setBounds((w * 2) + startX, startY, (w * 3) + startX, startY + h);
            drawable3.draw(canvas);
        }
    }

    private void drawDrawable4(Canvas canvas, boolean minus, int iCenterX, int iCenterY, Drawable drawable1, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        if (drawable3 != null) {
            int w = drawable3.getIntrinsicWidth();
            int h = drawable3.getIntrinsicHeight();
            if (this.mChanged) {
                if (drawable1 != null) {
                    Drawable drawable = drawable1;
                    drawable.setBounds(((this.centerX1 + iCenterX) - w) - drawable1.getIntrinsicWidth(), (this.centerY1 + iCenterY) - (h / 2), (this.centerX1 + iCenterX) - w, this.centerY1 + iCenterY + (h / 2));
                    if (minus) {
                        drawable1.draw(canvas);
                    }
                }
                if (drawable2 != null) {
                    drawable2.setBounds((this.centerX1 + iCenterX) - w, (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX, this.centerY1 + iCenterY + (h / 2));
                    drawable2.draw(canvas);
                }
                if (drawable3 != null) {
                    drawable3.setBounds(this.centerX1 + iCenterX, (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + w, this.centerY1 + iCenterY + (h / 2));
                    drawable3.draw(canvas);
                }
                if (drawable4 != null) {
                    int w4 = drawable4.getIntrinsicWidth();
                    Drawable drawable5 = drawable4;
                    drawable5.setBounds(this.centerX1 + iCenterX + w, ((this.centerY1 + iCenterY) + (h / 2)) - drawable4.getIntrinsicHeight(), this.centerX1 + iCenterX + w + w4, this.centerY1 + iCenterY + (h / 2));
                    drawable4.draw(canvas);
                }
            }
        }
    }

    private void drawDisDrawable4(Canvas canvas, boolean minus, int iCenterX, int iCenterY, Drawable drawable1, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        if (drawable4 != null) {
            int w = drawable4.getIntrinsicWidth();
            int h = drawable4.getIntrinsicHeight();
            int w3 = drawable3.getIntrinsicWidth();
            int intrinsicHeight = drawable3.getIntrinsicHeight();
            if (this.mChanged) {
                if (drawable1 != null) {
                    int intrinsicWidth = drawable1.getIntrinsicWidth();
                    drawable1.setBounds((this.centerX1 + iCenterX) - (w * 2), (this.centerY1 + iCenterY) - (h / 2), (this.centerX1 + iCenterX) - w, this.centerY1 + iCenterY + (h / 2));
                    if (minus) {
                        drawable1.draw(canvas);
                    }
                }
                if (drawable2 != null) {
                    drawable2.setBounds((this.centerX1 + iCenterX) - w, (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX, this.centerY1 + iCenterY + (h / 2));
                    drawable2.draw(canvas);
                }
                if (drawable3 != null) {
                    drawable3.setBounds(this.centerX1 + iCenterX, (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + w3, this.centerY1 + iCenterY + (h / 2));
                    drawable3.draw(canvas);
                }
                if (drawable4 != null) {
                    int intrinsicWidth2 = drawable4.getIntrinsicWidth();
                    Drawable drawable = drawable4;
                    drawable.setBounds(this.centerX1 + iCenterX + w3, ((this.centerY1 + iCenterY) + (h / 2)) - drawable4.getIntrinsicHeight(), this.centerX1 + iCenterX + w + w3, this.centerY1 + iCenterY + (h / 2));
                    drawable4.draw(canvas);
                }
            }
        }
    }

    private void drawDrawable5(Canvas canvas, boolean ispoint, int iCenterX, int iCenterY, Drawable drawable1, Drawable drawable2, Drawable drawable3, Drawable drawable4, Drawable drawable5) {
        if (drawable1 != null) {
            int w = drawable1.getIntrinsicWidth();
            int h = drawable1.getIntrinsicHeight();
            int w2 = drawable3.getIntrinsicWidth();
            int intrinsicHeight = drawable3.getIntrinsicHeight();
            if (this.mChanged) {
                drawable1.setBounds(((this.centerX1 + iCenterX) - (w2 / 2)) - (w * 2), (this.centerY1 + iCenterY) - (h / 2), ((this.centerX1 + iCenterX) - (w2 / 2)) - w, this.centerY1 + iCenterY + (h / 2));
                drawable1.draw(canvas);
                drawable2.setBounds(((this.centerX1 + iCenterX) - (w2 / 2)) - w, (this.centerY1 + iCenterY) - (h / 2), (this.centerX1 + iCenterX) - (w2 / 2), this.centerY1 + iCenterY + (h / 2));
                drawable2.draw(canvas);
                drawable3.setBounds((this.centerX1 + iCenterX) - (w2 / 2), (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w2 / 2), this.centerY1 + iCenterY + (h / 2));
                if (ispoint) {
                    drawable3.draw(canvas);
                } else if (this.mCalendar.second % 2 == 0) {
                    drawable3.draw(canvas);
                }
                drawable4.setBounds(this.centerX1 + iCenterX + (w2 / 2), (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w2 / 2) + w, this.centerY1 + iCenterY + (h / 2));
                drawable4.draw(canvas);
                drawable5.setBounds(this.centerX1 + iCenterX + (w2 / 2) + w, (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w2 / 2) + (w * 2), this.centerY1 + iCenterY + (h / 2));
                drawable5.draw(canvas);
            }
        }
    }

    private void drawDrawable6(Canvas canvas, boolean ispoint, int iCenterX, int iCenterY, Drawable drawable1, Drawable drawable2, Drawable drawable3, Drawable drawable4, Drawable drawable5, Drawable drawable6) {
        if (drawable1 != null) {
            int w = drawable1.getIntrinsicWidth();
            int h = drawable1.getIntrinsicHeight();
            int w2 = drawable3.getIntrinsicWidth();
            int intrinsicHeight = drawable3.getIntrinsicHeight();
            int w6 = 0;
            if (drawable6 != null) {
                w6 = drawable6.getIntrinsicWidth();
            }
            int startX = (this.centerX1 + iCenterX) - ((((w * 4) + w2) + w6) / 2);
            int startY = (this.centerY1 + iCenterY) - (h / 2);
            if (this.mChanged) {
                drawable1.setBounds(startX, startY, startX + w, startY + h);
                drawable1.draw(canvas);
                drawable2.setBounds(startX + w, startY, (w * 2) + startX, startY + h);
                drawable2.draw(canvas);
                drawable3.setBounds((w * 2) + startX, startY, (w * 2) + startX + w2, startY + h);
                if (ispoint) {
                    drawable3.draw(canvas);
                } else if (this.mCalendar.second % 2 == 0) {
                    drawable3.draw(canvas);
                }
                drawable4.setBounds((w * 2) + startX + w2, startY, (w * 3) + startX + w2, startY + h);
                drawable4.draw(canvas);
                drawable5.setBounds((w * 3) + startX + w2, startY, (w * 4) + startX + w2, startY + h);
                drawable5.draw(canvas);
                if (drawable6 != null) {
                    drawable6.setBounds((w * 4) + startX + w2, startY, (w * 4) + startX + w2 + w6, startY + h);
                    drawable6.draw(canvas);
                }
            }
        }
    }

    private void drawDrawable8(Canvas canvas, int iCenterX, int iCenterY, Drawable drawable3, Drawable drawable4, Drawable drawable5, Drawable drawable6, Drawable drawable7, Drawable drawable8, Drawable drawable9, Drawable drawable10) {
        if (drawable3 != null) {
            int w = drawable3.getIntrinsicWidth();
            int h = drawable3.getIntrinsicHeight();
            int w2 = drawable5.getIntrinsicWidth();
            int intrinsicHeight = drawable5.getIntrinsicHeight();
            if (this.mChanged) {
                drawable3.setBounds(((this.centerX1 + iCenterX) - (w * 3)) - w2, (this.centerY1 + iCenterY) - (h / 2), ((this.centerX1 + iCenterX) - (w * 2)) - w2, this.centerY1 + iCenterY + (h / 2));
                drawable3.draw(canvas);
                drawable4.setBounds(((this.centerX1 + iCenterX) - (w * 2)) - w2, (this.centerY1 + iCenterY) - (h / 2), ((this.centerX1 + iCenterX) - w) - w2, this.centerY1 + iCenterY + (h / 2));
                drawable4.draw(canvas);
                drawable5.setBounds(((this.centerX1 + iCenterX) - w) - w2, (this.centerY1 + iCenterY) - (h / 2), (this.centerX1 + iCenterX) - w, this.centerY1 + iCenterY + (h / 2));
                drawable5.draw(canvas);
                drawable6.setBounds((this.centerX1 + iCenterX) - w, (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX, this.centerY1 + iCenterY + (h / 2));
                drawable6.draw(canvas);
                drawable7.setBounds(this.centerX1 + iCenterX, (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + w, this.centerY1 + iCenterY + (h / 2));
                drawable7.draw(canvas);
                drawable8.setBounds(this.centerX1 + iCenterX + w, (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + w + w2, this.centerY1 + iCenterY + (h / 2));
                drawable8.draw(canvas);
                drawable9.setBounds(this.centerX1 + iCenterX + w + w2, (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w * 2) + w2, this.centerY1 + iCenterY + (h / 2));
                drawable9.draw(canvas);
                drawable10.setBounds(this.centerX1 + iCenterX + (w * 2) + w2, (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w * 3) + w2, this.centerY1 + iCenterY + (h / 2));
                drawable10.draw(canvas);
            }
        }
    }

    private void drawDrawable10(Canvas canvas, int iCenterX, int iCenterY, Drawable drawable1, Drawable drawable2, Drawable drawable3, Drawable drawable4, Drawable drawable5, Drawable drawable6, Drawable drawable7, Drawable drawable8, Drawable drawable9, Drawable drawable10) {
        if (drawable1 != null) {
            int w = drawable1.getIntrinsicWidth();
            int h = drawable1.getIntrinsicHeight();
            if (this.mChanged) {
                drawable1.setBounds((this.centerX1 + iCenterX) - (w * 5), (this.centerY1 + iCenterY) - (h / 2), (this.centerX1 + iCenterX) - (w * 4), this.centerY1 + iCenterY + (h / 2));
                drawable1.draw(canvas);
                drawable2.setBounds((this.centerX1 + iCenterX) - (w * 4), (this.centerY1 + iCenterY) - (h / 2), (this.centerX1 + iCenterX) - (w * 3), this.centerY1 + iCenterY + (h / 2));
                drawable2.draw(canvas);
                drawable3.setBounds((this.centerX1 + iCenterX) - (w * 3), (this.centerY1 + iCenterY) - (h / 2), (this.centerX1 + iCenterX) - (w * 2), this.centerY1 + iCenterY + (h / 2));
                drawable3.draw(canvas);
                drawable4.setBounds((this.centerX1 + iCenterX) - (w * 2), (this.centerY1 + iCenterY) - (h / 2), (this.centerX1 + iCenterX) - w, this.centerY1 + iCenterY + (h / 2));
                drawable4.draw(canvas);
                drawable5.setBounds((this.centerX1 + iCenterX) - w, (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX, this.centerY1 + iCenterY + (h / 2));
                drawable5.draw(canvas);
                drawable6.setBounds(this.centerX1 + iCenterX, (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + w, this.centerY1 + iCenterY + (h / 2));
                drawable6.draw(canvas);
                drawable7.setBounds(this.centerX1 + iCenterX + w, (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w * 2), this.centerY1 + iCenterY + (h / 2));
                drawable7.draw(canvas);
                drawable8.setBounds(this.centerX1 + iCenterX + (w * 2), (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w * 3), this.centerY1 + iCenterY + (h / 2));
                drawable8.draw(canvas);
                drawable9.setBounds(this.centerX1 + iCenterX + (w * 3), (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w * 4), this.centerY1 + iCenterY + (h / 2));
                drawable9.draw(canvas);
                drawable10.setBounds(this.centerX1 + iCenterX + (w * 4), (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w * 5), this.centerY1 + iCenterY + (h / 2));
                drawable10.draw(canvas);
            }
        }
    }

    private Resources getResource(Context context, String themePackage) {
        if (themePackage == null) {
            return null;
        }
        Resources themeResources = null;
        try {
            themeResources = context.getPackageManager().getResourcesForApplication(themePackage.toString());
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return themeResources;
    }

    private static String get_cur_theme_package(Context context) {
        return ((installedClock) WatchApp.getInstalledClocks().get(WatchApp.getClockIndex(context) - ClockUtil.mClockList.length)).pkg;
    }

    private Drawable getDrawableRes(Resources r2, String pkg2, String id_name) {
        if (this.mClockskinPath == null) {
            Drawable ret = null;
            int ID = r2.getIdentifier(id_name.split("\\.")[0], "drawable", pkg2);
            if (ID != 0) {
                ret = r2.getDrawable(ID);
            }
            return ret;
        }
        try {
            return getImageDrawable(id_name);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Drawable getImageDrawable(String path) throws IOException {
        String filepath = this.mClockskinPath + File.separator + path;
        Log.d("xiaocai", "imageFilepath:" + filepath);
        if (!new File(filepath).exists()) {
            return null;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        ((FragmentActivity) this.mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        BitmapDrawable bd = new BitmapDrawable(new Resources(this.mContext.getAssets(), metrics, null), BitmapFactory.decodeFile(filepath));
        this.maxWidth = Math.max(this.maxWidth, bd.getIntrinsicWidth());
        if (!(this.maxWidth == 640 || this.maxWidth == 454 || this.maxWidth <= 400)) {
            this.maxWidth = 400;
        }
        return bd;
    }

    public XmlPullParser getXmlParser(String xmlName) throws XmlPullParserException, IOException {
        String filepath = this.mClockskinPath + File.separator + xmlName + ".xml";
        if (!new File(filepath).exists()) {
            return null;
        }
        InputStream slideInputStream = new FileInputStream(filepath);
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(slideInputStream, "UTF-8");
        return parser;
    }

    /* access modifiers changed from: 0000 */
    public void drawBatteryPictureWithCircle(Canvas canvas, List<Drawable> drawables, int centerX, int centerY, int batteryLevel, String colorArray, int radius, boolean isAdjustResolution) {
        int res1 = batteryLevel / 100;
        int res2 = (batteryLevel / 10) % 10;
        int res3 = batteryLevel % 10;
        if (batteryLevel < 10) {
            res2 = 10;
            res1 = 10;
        } else if (batteryLevel < 100) {
            res1 = 10;
        }
        int d_1_width = ((Drawable) drawables.get(res1)).getIntrinsicWidth();
        int d_2_width = ((Drawable) drawables.get(res2)).getIntrinsicWidth();
        int d_3_width = ((Drawable) drawables.get(res3)).getIntrinsicWidth();
        int degree_width = ((Drawable) drawables.get(11)).getIntrinsicWidth();
        int height = ((Drawable) drawables.get(res3)).getIntrinsicHeight();
        int left = centerX - ((((d_1_width + d_2_width) + d_3_width) + degree_width) / 2);
        int top = centerY - (height / 2);
        int bottom = centerY + (height / 2);
        int b_1_right = left + d_1_width;
        int b_2_right = b_1_right + d_2_width;
        int b_3_right = b_2_right + d_3_width;
        ((Drawable) drawables.get(res1)).setBounds(left, top, b_1_right, bottom);
        ((Drawable) drawables.get(res1)).draw(canvas);
        ((Drawable) drawables.get(res2)).setBounds(b_1_right, top, b_2_right, bottom);
        ((Drawable) drawables.get(res2)).draw(canvas);
        ((Drawable) drawables.get(res3)).setBounds(b_2_right, top, b_3_right, bottom);
        ((Drawable) drawables.get(res3)).draw(canvas);
        ((Drawable) drawables.get(11)).setBounds(b_3_right, top, b_3_right + degree_width, bottom);
        ((Drawable) drawables.get(11)).draw(canvas);
        if (colorArray.contains(",")) {
            int dark_color = -16777216 | Integer.valueOf(colorArray.split(",")[0].replace("#", ""), 16).intValue();
            int bright_color = -16777216 | Integer.valueOf(colorArray.split(",")[1].replace("#", ""), 16).intValue();
            this.mPaint.reset();
            this.mPaint.setAntiAlias(true);
            this.mPaint.setStrokeWidth(15.0f);
            this.mPaint.setStyle(Style.STROKE);
            canvas.save();
            canvas.translate((float) centerX, (float) centerY);
            canvas.scale(0.25f, 0.25f);
            canvas.rotate(180.0f);
            for (int i = 0; i < 20; i++) {
                if (i < batteryLevel / 5) {
                    this.mPaint.setColor(bright_color);
                    Canvas canvas2 = canvas;
                    canvas.drawLine(7.0f, (float) radius, 7.0f, (float) (radius + 25), this.mPaint);
                    canvas.rotate(18.0f, 0.0f, 0.0f);
                } else {
                    this.mPaint.setColor(dark_color);
                    Canvas canvas3 = canvas;
                    canvas.drawLine(7.0f, (float) radius, 7.0f, (float) (radius + 25), this.mPaint);
                    canvas.rotate(18.0f, 0.0f, 0.0f);
                }
            }
            canvas.restore();
        }
    }

    /* access modifiers changed from: 0000 */
    public void drawStepsPictureWithCircle(Context context, Canvas canvas, Float radius, List<Drawable> drawables, int centerX, int centerY, int stepCount, String colorArray, boolean isAdjustResolution) {
        if (stepCount < 0) {
            stepCount = 0;
        }
        if (stepCount > 99999) {
            stepCount = 99999;
        }
        int res1 = stepCount / 10000;
        int res2 = (stepCount / 1000) % 10;
        int res3 = (stepCount / 100) % 10;
        int res4 = (stepCount / 10) % 10;
        int res5 = stepCount % 10;
        if (stepCount < 10) {
            res4 = 10;
            res3 = 10;
            res2 = 10;
            res1 = 10;
        } else if (stepCount < 100) {
            res3 = 10;
            res2 = 10;
            res1 = 10;
        } else if (stepCount < 1000) {
            res2 = 10;
            res1 = 10;
        } else if (stepCount < 10000) {
            res1 = 10;
        }
        int d_1_width = ((Drawable) drawables.get(res1)).getIntrinsicWidth();
        int d_2_width = ((Drawable) drawables.get(res2)).getIntrinsicWidth();
        int d_3_width = ((Drawable) drawables.get(res3)).getIntrinsicWidth();
        int d_4_width = ((Drawable) drawables.get(res4)).getIntrinsicWidth();
        int d_5_width = ((Drawable) drawables.get(res5)).getIntrinsicWidth();
        int height = ((Drawable) drawables.get(res5)).getIntrinsicHeight();
        int top = centerY - (height / 2);
        int bottom = centerY + (height / 2);
        int d_1_left = centerX - (((((d_1_width + d_2_width) + d_3_width) + d_4_width) + d_5_width) / 2);
        int d_1_right = d_1_left + d_1_width;
        int d_2_right = d_1_right + d_2_width;
        int d_3_right = d_2_right + d_3_width;
        int d_4_right = d_3_right + d_4_width;
        int d_5_right = d_4_right + d_5_width;
        ((Drawable) drawables.get(res1)).setBounds(d_1_left, top, d_1_right, bottom);
        ((Drawable) drawables.get(res1)).draw(canvas);
        ((Drawable) drawables.get(res2)).setBounds(d_1_right, top, d_2_right, bottom);
        ((Drawable) drawables.get(res2)).draw(canvas);
        ((Drawable) drawables.get(res3)).setBounds(d_2_right, top, d_3_right, bottom);
        ((Drawable) drawables.get(res3)).draw(canvas);
        ((Drawable) drawables.get(res4)).setBounds(d_3_right, top, d_4_right, bottom);
        ((Drawable) drawables.get(res4)).draw(canvas);
        ((Drawable) drawables.get(res5)).setBounds(d_4_right, top, d_5_right, bottom);
        ((Drawable) drawables.get(res5)).draw(canvas);
        if (colorArray.contains(",")) {
            int dark_color = -16777216 | Integer.valueOf(colorArray.split(",")[0].replace("#", ""), 16).intValue();
            int bright_color = -16777216 | Integer.valueOf(colorArray.split(",")[1].replace("#", ""), 16).intValue();
            this.mPaint.reset();
            this.mPaint.setAntiAlias(true);
            this.mPaint.setStrokeWidth(10.0f);
            this.mPaint.setStyle(Style.STROKE);
            this.mPaint.setAlpha(255);
            canvas.save();
            RectF oval = new RectF();
            oval.set(((float) centerX) - radius.floatValue(), ((float) centerY) - radius.floatValue(), radius.floatValue() + ((float) centerX), radius.floatValue() + ((float) centerY));
            int suggestSteps = WatchApp.getTargetSteps(context);
            this.mPaint.setColor(dark_color);
            canvas.drawCircle((float) centerX, (float) centerY, radius.floatValue(), this.mPaint);
            this.mPaint.setColor(bright_color);
            if (stepCount > suggestSteps) {
                canvas.drawCircle((float) centerX, (float) centerY, radius.floatValue(), this.mPaint);
            } else {
                canvas.drawArc(oval, 270.0f, (((float) stepCount) / ((float) suggestSteps)) * 360.0f, false, this.mPaint);
            }
            canvas.restore();
        }
    }

    /* access modifiers changed from: 0000 */
    public void drawStepsPictureWithCircle2(Context context, Canvas canvas, int centerX, int centerY, int stepCount, String width, String radius, String colorArray, int direction) {
        int widthInt = Integer.parseInt(width);
        int radiusInt = Integer.parseInt(radius);
        if (colorArray.contains(",")) {
            int dark_color = -16777216 | Integer.valueOf(colorArray.split(",")[0].replace("#", ""), 16).intValue();
            int bright_color = -16777216 | Integer.valueOf(colorArray.split(",")[1].replace("#", ""), 16).intValue();
            this.mPaint.reset();
            this.mPaint.setAntiAlias(true);
            this.mPaint.setStrokeWidth((float) widthInt);
            this.mPaint.setStyle(Style.STROKE);
            this.mPaint.setAlpha(255);
            canvas.save();
            RectF oval = new RectF();
            oval.set((float) (centerX - radiusInt), (float) (centerY - radiusInt), (float) (radiusInt + centerX), (float) (radiusInt + centerY));
            int suggestSteps = WatchApp.getTargetSteps(context);
            this.mPaint.setColor(dark_color);
            this.mPaint.setColor(bright_color);
            if (stepCount > suggestSteps) {
                canvas.drawCircle((float) centerX, (float) centerY, (float) radiusInt, this.mPaint);
            } else {
                if (direction == 2) {
                    stepCount = -stepCount;
                }
                canvas.drawArc(oval, 270.0f, (((float) stepCount) / ((float) suggestSteps)) * 360.0f, false, this.mPaint);
            }
            canvas.restore();
        }
    }

    /* access modifiers changed from: 0000 */
    public void drawStepsCircle(Context context, Canvas canvas, int centerX, int centerY, int stepCount, int targetSteps, String width, String radius, String colorArray, int direction, String angle, String startAngle, int mulrotate) {
        int widthInt = Integer.parseInt(width);
        int radiusInt = Integer.parseInt(radius);
        float angle2 = Float.parseFloat(angle);
        float startAngle2 = Float.parseFloat(startAngle);
        if (colorArray.contains(",")) {
            int dark_color = -16777216 | Integer.valueOf(colorArray.split(",")[0].replace("#", ""), 16).intValue();
            int bright_color = -16777216 | Integer.valueOf(colorArray.split(",")[1].replace("#", ""), 16).intValue();
            this.mPaint.reset();
            this.mPaint.setAntiAlias(true);
            this.mPaint.setStrokeWidth((float) widthInt);
            this.mPaint.setStyle(Style.STROKE);
            this.mPaint.setAlpha(255);
            if (mulrotate != 1) {
                this.mPaint.setStrokeCap(Cap.ROUND);
            }
            canvas.save();
            RectF oval = new RectF();
            oval.set((float) (centerX - radiusInt), (float) (centerY - radiusInt), (float) (radiusInt + centerX), (float) (radiusInt + centerY));
            this.mPaint.setColor(dark_color);
            this.mPaint.setColor(bright_color);
            if (stepCount > targetSteps) {
                canvas.drawArc(oval, startAngle2, angle2, false, this.mPaint);
            } else {
                if (direction == 2) {
                    stepCount = -stepCount;
                }
                canvas.drawArc(oval, startAngle2, (((float) stepCount) / ((float) targetSteps)) * angle2, false, this.mPaint);
            }
            canvas.restore();
        }
    }

    /* access modifiers changed from: 0000 */
    public void drawKcalCircle(Context context, Canvas canvas, int centerX, int centerY, float kcal, float targetKcal, String width, String radius, String colorArray, int direction, String angle, String startAngle, int mulrotate) {
        int widthInt = Integer.parseInt(width);
        int radiusInt = Integer.parseInt(radius);
        float angle2 = Float.parseFloat(angle);
        float startAngle2 = Float.parseFloat(startAngle);
        if (colorArray.contains(",")) {
            int dark_color = -16777216 | Integer.valueOf(colorArray.split(",")[0].replace("#", ""), 16).intValue();
            int bright_color = -16777216 | Integer.valueOf(colorArray.split(",")[1].replace("#", ""), 16).intValue();
            this.mPaint.reset();
            this.mPaint.setAntiAlias(true);
            this.mPaint.setStrokeWidth((float) widthInt);
            this.mPaint.setStyle(Style.STROKE);
            this.mPaint.setAlpha(255);
            if (mulrotate != 1) {
                this.mPaint.setStrokeCap(Cap.ROUND);
            }
            canvas.save();
            RectF oval = new RectF();
            oval.set((float) (centerX - radiusInt), (float) (centerY - radiusInt), (float) (radiusInt + centerX), (float) (radiusInt + centerY));
            this.mPaint.setColor(dark_color);
            this.mPaint.setColor(bright_color);
            if (kcal > targetKcal) {
                canvas.drawArc(oval, startAngle2, angle2, false, this.mPaint);
            } else {
                if (direction == 2) {
                    kcal = -kcal;
                }
                canvas.drawArc(oval, startAngle2, (kcal / targetKcal) * angle2, false, this.mPaint);
            }
            canvas.restore();
        }
    }

    /* access modifiers changed from: 0000 */
    public void drawDisCircle(Context context, Canvas canvas, int centerX, int centerY, float dis, float targetDis, String width, String radius, String colorArray, int direction, String angle, String startAngle, int mulrotate) {
        int widthInt = Integer.parseInt(width);
        int radiusInt = Integer.parseInt(radius);
        float angle2 = Float.parseFloat(angle);
        float startAngle2 = Float.parseFloat(startAngle);
        if (colorArray.contains(",")) {
            int dark_color = -16777216 | Integer.valueOf(colorArray.split(",")[0].replace("#", ""), 16).intValue();
            int bright_color = -16777216 | Integer.valueOf(colorArray.split(",")[1].replace("#", ""), 16).intValue();
            this.mPaint.reset();
            this.mPaint.setAntiAlias(true);
            this.mPaint.setStrokeWidth((float) widthInt);
            this.mPaint.setStyle(Style.STROKE);
            this.mPaint.setAlpha(255);
            if (mulrotate != 1) {
                this.mPaint.setStrokeCap(Cap.ROUND);
            }
            canvas.save();
            RectF oval = new RectF();
            oval.set((float) (centerX - radiusInt), (float) (centerY - radiusInt), (float) (radiusInt + centerX), (float) (radiusInt + centerY));
            this.mPaint.setColor(dark_color);
            this.mPaint.setColor(bright_color);
            if (dis > targetDis) {
                canvas.drawArc(oval, startAngle2, angle2, false, this.mPaint);
            } else {
                if (direction == 2) {
                    dis = -dis;
                }
                canvas.drawArc(oval, startAngle2, (dis / targetDis) * angle2, false, this.mPaint);
            }
            canvas.restore();
        }
    }

    /* access modifiers changed from: 0000 */
    public void drawPowerCircle(Context context, Canvas canvas, int centerX, int centerY, int level, String width, String radius, String colorArray, int direction, String angle, String startAngle, int mulrotate) {
        int widthInt = Integer.parseInt(width);
        int radiusInt = Integer.parseInt(radius);
        float angle2 = Float.parseFloat(angle);
        float startAngle2 = Float.parseFloat(startAngle);
        if (colorArray.contains(",")) {
            int dark_color = -16777216 | Integer.valueOf(colorArray.split(",")[0].replace("#", ""), 16).intValue();
            int bright_color = -16777216 | Integer.valueOf(colorArray.split(",")[1].replace("#", ""), 16).intValue();
            this.mPaint.reset();
            this.mPaint.setAntiAlias(true);
            this.mPaint.setStrokeWidth((float) widthInt);
            this.mPaint.setStyle(Style.STROKE);
            this.mPaint.setAlpha(255);
            if (mulrotate != 1) {
                this.mPaint.setStrokeCap(Cap.ROUND);
            }
            canvas.save();
            RectF oval = new RectF();
            oval.set((float) (centerX - radiusInt), (float) (centerY - radiusInt), (float) (radiusInt + centerX), (float) (radiusInt + centerY));
            this.mPaint.setColor(dark_color);
            this.mPaint.setColor(bright_color);
            if (level > 100) {
                level = 100;
            }
            if (level == 100) {
                canvas.drawArc(oval, startAngle2, angle2, false, this.mPaint);
            } else {
                if (direction == 2) {
                    level = -level;
                }
                canvas.drawArc(oval, startAngle2, (((float) level) / 100.0f) * angle2, false, this.mPaint);
            }
            canvas.restore();
        }
    }

    /* access modifiers changed from: 0000 */
    public void drawBatteryCircle(Context context, Canvas canvas, int centerX, int centerY, int level, String width, String radius, String colorArray, boolean isAdjustResolution) {
        if (level < 0) {
            level = 0;
        }
        int widthInt = Integer.parseInt(width);
        int radiusInt = Integer.parseInt(radius);
        if (colorArray.contains(",")) {
            int dark_color = -16777216 | Integer.valueOf(colorArray.split(",")[0].replace("#", ""), 16).intValue();
            int bright_color = -16777216 | Integer.valueOf(colorArray.split(",")[1].replace("#", ""), 16).intValue();
            this.mPaint.reset();
            this.mPaint.setAntiAlias(true);
            this.mPaint.setStrokeWidth((float) widthInt);
            this.mPaint.setStyle(Style.STROKE);
            this.mPaint.setAlpha(255);
            canvas.save();
            RectF oval = new RectF();
            oval.set((float) (centerX - radiusInt), (float) (centerY - radiusInt), (float) (radiusInt + centerX), (float) (radiusInt + centerY));
            this.mPaint.setColor(dark_color);
            canvas.drawCircle((float) centerX, (float) centerY, (float) radiusInt, this.mPaint);
            this.mPaint.setColor(bright_color);
            if (level > 100) {
                canvas.drawCircle((float) centerX, (float) centerY, (float) radiusInt, this.mPaint);
            } else {
                canvas.drawArc(oval, -90.0f, (((float) level) / 100.0f) * 360.0f, false, this.mPaint);
            }
            canvas.restore();
        }
    }

    /* access modifiers changed from: 0000 */
    public void drawPedometer(Context context, Canvas canvas, int startAngle, int direction, int textSize, int stepCount) {
        canvas.save();
        canvas.translate((float) (this.centerX1 - (this.maxWidth / 2)), (float) (this.centerY1 - (this.maxWidth / 2)));
        Path path = new Path();
        RectF rect = new RectF(15.0f, 15.0f, ((float) this.maxWidth) - 15.0f, ((float) this.maxWidth) - 15.0f);
        this.mPaint.reset();
        this.mPaint.setTextSize((float) textSize);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(-3355444);
        if (direction == 2) {
            path.addArc(rect, (float) startAngle, 33.0f);
        } else {
            path.addArc(rect, (float) startAngle, 33.0f);
        }
        canvas.drawTextOnPath("Pedometer", path, 0.0f, 0.0f, this.mPaint);
        this.mPaint.setColor(-13972358);
        path.reset();
        if (direction == 2) {
            path.addArc(rect, (float) (startAngle - 35), 50.0f);
        } else {
            path.addArc(rect, (float) (startAngle + 35), 50.0f);
        }
        if (stepCount < 0) {
            stepCount = 0;
        }
        canvas.drawTextOnPath(stepCount + " step", path, 0.0f, 0.0f, this.mPaint);
        canvas.restore();
    }

    /* access modifiers changed from: 0000 */
    public void drawHeartRate(Context context, Canvas canvas, int startAngle, int direction, int textSize, int lastHeartRate) {
        canvas.translate((float) (this.centerX1 - (this.maxWidth / 2)), (float) (this.centerY1 - (this.maxWidth / 2)));
        Path path = new Path();
        RectF rect = new RectF(15.0f, 15.0f, ((float) this.maxWidth) - 15.0f, ((float) this.maxWidth) - 15.0f);
        if (direction == 2) {
            path.addArc(rect, (float) startAngle, 30.0f);
        } else {
            path.addArc(rect, (float) startAngle, 30.0f);
        }
        this.mPaint.reset();
        this.mPaint.setTextSize((float) textSize);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(-3355444);
        canvas.drawTextOnPath("Heart rate", path, 0.0f, 0.0f, this.mPaint);
        this.mPaint.setColor(-2414295);
        path.reset();
        if (direction == 2) {
            path.addArc(rect, (float) (startAngle - 33), 25.0f);
        } else {
            path.addArc(rect, (float) (startAngle + 33), 25.0f);
        }
        canvas.drawTextOnPath(lastHeartRate + "bpm", path, 0.0f, 0.0f, this.mPaint);
    }

    /* access modifiers changed from: 0000 */
    public void drawChargingInfo(Context context, Canvas canvas, Drawable mDrawBattery2, int centerX, int centerY, int color, boolean isAdjustResolution) {
        int width = mDrawBattery2.getIntrinsicWidth();
        int height = mDrawBattery2.getIntrinsicHeight();
        mDrawBattery2.setBounds(centerX - (width / 2), centerY - (height / 2), (width / 2) + centerX, (height / 2) + centerY);
        mDrawBattery2.draw(canvas);
        String batteryVol = this.mBatteryLevel + "%";
        this.mPaint.reset();
        this.mPaint.setTextSize(20.0f);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(color);
        canvas.drawText(batteryVol, (float) (mDrawBattery2.getBounds().right + 5), (float) (mDrawBattery2.getBounds().bottom - 8), this.mPaint);
    }

    /* access modifiers changed from: 0000 */
    public void drawSpecialSecond(Canvas canvas, String colorsArray, int t_minute, int t_second) {
        int RADIUS = this.maxWidth / 2;
        canvas.save();
        canvas.translate((float) this.centerX1, (float) this.centerY1);
        if (colorsArray.contains(",")) {
            int bright_color = -16777216 | Integer.valueOf(colorsArray.split(",")[0], 16).intValue();
            int dark_color = -16777216 | Integer.valueOf(colorsArray.split(",")[1], 16).intValue();
            this.mPaint.reset();
            this.mPaint.setAntiAlias(true);
            this.mPaint.setStrokeWidth(10.0f);
            this.mPaint.setStyle(Style.STROKE);
            this.mPaint.setAlpha(255);
            float y = (float) ((-RADIUS) + 5);
            for (int i = 0; i < 60; i++) {
                if (t_minute % 2 == 0) {
                    if (i < t_second) {
                        this.mPaint.setColor(bright_color);
                        canvas.drawLine(5.0f, y, 5.0f, y + 15.0f, this.mPaint);
                        canvas.rotate(6.0f, 0.0f, 0.0f);
                    } else {
                        this.mPaint.setColor(dark_color);
                        canvas.drawLine(5.0f, y, 5.0f, y + 15.0f, this.mPaint);
                        canvas.rotate(6.0f, 0.0f, 0.0f);
                    }
                } else if (i >= t_second) {
                    this.mPaint.setColor(bright_color);
                    canvas.drawLine(5.0f, y, 5.0f, y + 15.0f, this.mPaint);
                    canvas.rotate(6.0f, 0.0f, 0.0f);
                } else {
                    this.mPaint.setColor(dark_color);
                    canvas.drawLine(5.0f, y, 5.0f, y + 15.0f, this.mPaint);
                    canvas.rotate(6.0f, 0.0f, 0.0f);
                }
            }
        }
        canvas.restore();
    }

    /* access modifiers changed from: 0000 */
    public void drawClockQuietPircture(Canvas canvas, Drawable mDrawCircle, int centerX, int centerY, boolean isAdjustResolution) {
        int width = mDrawCircle.getIntrinsicWidth();
        int heigh = mDrawCircle.getIntrinsicHeight();
        mDrawCircle.setBounds(centerX - (width / 2), centerY - (heigh / 2), (width / 2) + centerX, (heigh / 2) + centerY);
        mDrawCircle.draw(canvas);
    }

    public void drawSecondHandShadow(Canvas canvas, String startAngle, int mulrotate, int direction, Drawable secondHand, boolean changed, int iCenterX, int iCenterY) {
        float shadowAngle;
        Float startAngleF = Float.valueOf(startAngle);
        long millis = this.mMilSecond;
        if (secondHand != null) {
            canvas.save();
            if (direction == 2) {
                millis = -millis;
            }
            float angle = startAngleF.floatValue() + (((((float) millis) * 6.0f) * ((float) mulrotate)) / 1000.0f);
            if (angle >= 0.0f && angle < 90.0f) {
                shadowAngle = 3.5f * (angle / 90.0f);
            } else if (angle < 90.0f || angle >= 270.0f) {
                shadowAngle = 3.5f * ((angle - 360.0f) / 90.0f);
            } else {
                shadowAngle = 3.5f * ((180.0f - angle) / 90.0f);
            }
            canvas.rotate(angle + shadowAngle, (float) (this.centerX1 + iCenterX), (float) (this.centerY1 + iCenterY));
            if (changed) {
                int w = secondHand.getIntrinsicWidth();
                int h = secondHand.getIntrinsicHeight();
                secondHand.setBounds((this.centerX1 + iCenterX) - (w / 2), (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w / 2), this.centerY1 + iCenterY + (h / 2));
            }
            secondHand.draw(canvas);
            canvas.restore();
        }
    }

    public void drawHourhandShadow(Canvas canvas, String angle, float hour2, Drawable hourHand, boolean changed, int iCenterX, int iCenterY) {
        float shadowAngle;
        Float startAngle = Float.valueOf(angle);
        if (hourHand != null) {
            canvas.save();
            float angleF = startAngle.floatValue() + (((hour2 % 12.0f) / 12.0f) * 360.0f);
            if (angleF >= 0.0f && angleF < 90.0f) {
                shadowAngle = 3.5f * (angleF / 90.0f);
            } else if (angleF < 90.0f || angleF >= 270.0f) {
                shadowAngle = 3.5f * ((angleF - 360.0f) / 90.0f);
            } else {
                shadowAngle = 3.5f * ((180.0f - angleF) / 90.0f);
            }
            canvas.rotate(angleF + shadowAngle, (float) (this.centerX1 + iCenterX), (float) (this.centerY1 + iCenterY));
            if (changed) {
                int w = hourHand.getIntrinsicWidth();
                int h = hourHand.getIntrinsicHeight();
                hourHand.setBounds((this.centerX1 + iCenterX) - (w / 2), (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w / 2), this.centerY1 + iCenterY + (h / 2));
            }
            hourHand.draw(canvas);
            canvas.restore();
        }
    }

    public void drawMinuteHandShadow(Canvas canvas, String angle, float minutes, Drawable minuteHand, boolean changed, int iCenterX, int iCenterY) {
        float shadowAngle;
        Float startAngle = Float.valueOf(angle);
        if (minuteHand != null) {
            canvas.save();
            float angleF = startAngle.floatValue() + ((minutes / 60.0f) * 360.0f);
            if (angleF >= 0.0f && angleF < 90.0f) {
                shadowAngle = 3.5f * (angleF / 90.0f);
            } else if (angleF < 90.0f || angleF >= 270.0f) {
                shadowAngle = 3.5f * ((angleF - 360.0f) / 90.0f);
            } else {
                shadowAngle = 3.5f * ((180.0f - angleF) / 90.0f);
            }
            canvas.rotate(angleF + shadowAngle, (float) (this.centerX1 + iCenterX), (float) (this.centerY1 + iCenterY));
            if (changed) {
                int w = minuteHand.getIntrinsicWidth();
                int h = minuteHand.getIntrinsicHeight();
                minuteHand.setBounds((this.centerX1 + iCenterX) - (w / 2), (this.centerY1 + iCenterY) - (h / 2), this.centerX1 + iCenterX + (w / 2), this.centerY1 + iCenterY + (h / 2));
            }
            minuteHand.draw(canvas);
            canvas.restore();
        }
    }

    private int getMoonPhaseType() {
        boolean isDayChange = false;
        if (this.moonPhaseCalendar == null) {
            this.moonPhaseCalendar = Calendar.getInstance(Locale.getDefault());
            this.moonPhaseCalendar.setTimeInMillis(System.currentTimeMillis());
            this.moonPhaseCalendar.setTimeZone(TimeZone.getDefault());
            isDayChange = true;
        }
        Calendar when = Calendar.getInstance(Locale.getDefault());
        when.setTimeInMillis(System.currentTimeMillis());
        when.setTimeZone(TimeZone.getDefault());
        if (!(when.get(Calendar.MONTH) == this.moonPhaseCalendar.get(Calendar.MONTH) && when.get(Calendar.DATE) == this.moonPhaseCalendar.get(Calendar.DATE))) {
            isDayChange = true;
            this.moonPhaseCalendar = when;
        }
        if (this.moonPhaseType == -1 || isDayChange) {
            this.moonPhaseType = new MoonPhase(this.mContext).searchMoonPhase();
        }
        return this.moonPhaseType;
    }
}