package com.bid.launcherwatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.provider.CallLog.Calls;
import android.provider.Settings.System;
import android.service.wallpaper.IWallpaperConnection;
import android.service.wallpaper.IWallpaperConnection.Stub;
import android.service.wallpaper.IWallpaperEngine;
import android.service.wallpaper.IWallpaperService;
import android.service.wallpaper.WallpaperService;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.bid.launcherwatch.ClockUtil.ClockSetWallpaper;
import com.bid.launcherwatch.ClockUtil.bgSettingClockSet;
import com.bid.launcherwatch.online.ClockSkinDBHelper;
import com.bid.launcherwatch.online.OnlineClockSkinLocalNode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WatchApp extends Application {
    private static int DayRainProbability = 0;
    private static int WeatherIcon = 0;
    private static int WeatherTemp = 0;
    private static boolean can_slide_in_viewpager = false;
    public static boolean isFristLaunch = true;
    private static boolean mBatteryCharging = false;
    private static float mBatteryLever = 0.0f;
    public static boolean mBatterySaver = false;
    static View mClockView = null;
    public static ArrayList<String> mClocksSkinPath = new ArrayList<>();
    private static SQLiteDatabase mDb;
    private static ClockSkinDBHelper mDbHelper;
    private static Dialog mDialog;
    private static int mHeartRate = 0;
    private static String mIndexKey = "";
    private static boolean mIsClockView = false;
    private static boolean mIsMainMenu = false;
    private static boolean mIsTopActivity = false;
    private static int mSteps = 0;
    private static int mStepsTarget = 8000;
    private static int mUnreadPhone = 0;
    private static int mUnreadSMS = 0;
    private static WallpaperConnection mWallpaperConnection;
    private static int mclockIndex = 0;
    public static ArrayList<installedClock> minstalledClocks = new ArrayList<>();
    private static WatchApp sWatchApp;
    private static  int MY_PERMISSIONS_REQUEST_READ_CALL_LOG=1;
    private static  int MY_PERMISSIONS_REQUEST_READ_SMS=1;
    static class WallpaperConnection extends IWallpaperConnection.Stub implements ServiceConnection {
        View mClockHost;
        boolean mConnected;
        Context mContext;
        IWallpaperEngine mEngine;
        final Intent mIntent;
        IWallpaperService mService;
        MyWallpaperService myWallpaperService;
        WallpaperConnection(Context context, Intent intent, View clockHost) {
            this.mContext = context;
            this.mIntent = intent;
            this.mClockHost = clockHost;
        }

        private boolean connect() {
            synchronized (this) {
                if (!this.mContext.bindService(this.mIntent, this, Context.BIND_AUTO_CREATE)) {
                    return false;
                }
                this.mConnected = true;
                Log.d("xiaocai_clockFragment", "WallpaperConnection connect");
                return true;

            }
        }

        public void disconnect() {
            synchronized (this) {
                this.mConnected = false;
                if (this.mEngine != null) {
                    try {
                        this.mEngine.destroy();
                    } catch (RemoteException e) {
                    }
                    this.mEngine = null;
                }
                this.mContext.unbindService(this);
                this.mService = null;
            }
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            this.mService = IWallpaperService.Stub.asInterface(service);
            try {
                View view = this.mClockHost;
                View root = view.getRootView();
                this.mService.attach(this, view.getWindowToken(), 1004, true, root.getWidth(), root.getHeight(), new Rect(0, 0, 0, 0));
            } catch (RemoteException e) {
                Log.d("xiaocai_clockFragment", "Failed attaching wallpaper; clearing");
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            this.mService = null;
            this.mEngine = null;
        }

        public void attachEngine(IWallpaperEngine engine) {
            synchronized (this) {
                if (this.mConnected) {
                    this.mEngine = engine;
                    try {
                        engine.setVisibility(true);
                    } catch (RemoteException e) {
                    }
                } else {
                    try {
                        engine.destroy();
                    } catch (RemoteException e2) {
                    }
                }
            }
        }

        public ParcelFileDescriptor setWallpaper(String name) {
            return null;
        }

        public void engineShown(IWallpaperEngine engine) throws RemoteException {
        }
    }

    public static class installedClock {
        String configurationAction;
        String filePath;
        Drawable img_preview;
        int img_preview_id;
        String pkg = "";
        String previewPath;
        ResolveInfo resolveInfo;
        String serviceName = "";
        String title_name;
        String type = "insideClock";
    }

    public void onCreate() {
        sWatchApp = this;
        super.onCreate();
//        Intent wallpaperService=new Intent("android.service.wallpaper.WallpaperService");
//        wallpaperService.setClassName("com.bid.launcherwatch","com.bid.launcherwatch.MyWallpaperService");
//        this.startService(wallpaperService);
        //Log.e("wall", "connect3");
        Log.d("WatchApp", "onCreate, WatchApp init ");
    }

    public static void setIsCanSlide(boolean b) {
        can_slide_in_viewpager = b;
    }

    public static boolean getIsCanSlide() {
        return can_slide_in_viewpager;
    }

    public static void setMainMenuStatus(boolean b) {
        mIsMainMenu = b;
    }

    public static boolean getMainMenuStatus() {
        return mIsMainMenu;
    }

    public static void setClockViewStatus(boolean status) {
        mIsClockView = status;
    }

    public static boolean getClockViewStatus() {
        return mIsClockView;
    }

    public static void setTopActivityStatus(boolean status) {
        mIsTopActivity = status;
    }

    public static boolean getTopActivityStatus() {
        return mIsTopActivity;
    }

    public static void updateInstalledClocks(Context context) {
        if (minstalledClocks.size() > 0) {
            for (installedClock clock : minstalledClocks) {
                Drawable drawable = clock.img_preview;
                if (drawable != null && (drawable instanceof BitmapDrawable)) {
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                }
            }
        }
        minstalledClocks.clear();
        mClocksSkinPath.clear();
        File f = new File((File.separator + "sdcard" + File.separator + "media") + File.separator + "InsideClockSkin" + File.separator);
        if (f.exists()) {
            search(f);
        }
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "clockskin" + File.separator);
        if (!file.exists()) {
            file.mkdir();
        }
        if (file.exists()) {
            search(file);
        }
        for (int i = 0; i < ClockUtil.mClockList.length; i++) {
            installedClock data_installed = new installedClock();
            data_installed.title_name = context.getResources().getString(ClockUtil.mClockList[i].mTitleId);
            data_installed.img_preview_id = ClockUtil.mClockList[i].mThumbImageId;
            data_installed.type = "insideClock";
            if (ClockUtil.mClockList[i] instanceof bgSettingClockSet) {
                data_installed.type = "bgsettingclock";
            } else if (ClockUtil.mClockList[i] instanceof ClockSetWallpaper) {
                data_installed.pkg = ((ClockSetWallpaper) ClockUtil.mClockList[i]).PackageName;
                data_installed.serviceName = ((ClockSetWallpaper) ClockUtil.mClockList[i]).ServiceName;
            }
            data_installed.filePath = data_installed.type + i;
            minstalledClocks.add(data_installed);
        }
        getAndroidWearClock(context);
        Intent intent = new Intent("ss.watch.clock");
        intent.addCategory("android.intent.category.DEFAULT");
        List<ResolveInfo> clocks = context.getPackageManager().queryIntentActivities(intent, 0);
        for (int i2 = 0; i2 < clocks.size(); i2++) {
            String appPackageName = ((ResolveInfo) clocks.get(i2)).activityInfo.packageName.toString();
            installedClock data_installed2 = new installedClock();
            data_installed2.type = "apkclockskin";
            data_installed2.pkg = appPackageName;
            data_installed2.resolveInfo = (ResolveInfo) clocks.get(i2);
            data_installed2.filePath = data_installed2.type + i2;
            minstalledClocks.add(data_installed2);
        }
        for (int i3 = 0; i3 < mClocksSkinPath.size(); i3++) {
            installedClock data_installed3 = new installedClock();
            data_installed3.type = "sdclock";
            data_installed3.pkg = "sdclock";
            data_installed3.filePath = ((String) mClocksSkinPath.get(i3)).substring(0, ((String) mClocksSkinPath.get(i3)).lastIndexOf("/"));
            data_installed3.previewPath = (String) mClocksSkinPath.get(i3);
            minstalledClocks.add(data_installed3);
        }
        ClockSkinDBHelper clockSkinDBHelper = new ClockSkinDBHelper(context, "clockskin.db", null, 1);
        mDbHelper = clockSkinDBHelper;
        try {
            mDb = mDbHelper.getReadableDatabase();
            Cursor cur = mDb.rawQuery("select * from clockskin_install_list", null);
            Log.d("WatchApp", "cur " + cur);
            if (cur != null) {
                cur.moveToFirst();
                while (!cur.isAfterLast()) {
                    OnlineClockSkinLocalNode onlineClockSkinLocalNode = new OnlineClockSkinLocalNode(context, cur);
                    Log.d("WatchApp", "node " + onlineClockSkinLocalNode.getName());
                    installedClock data_installed4 = new installedClock();
                    data_installed4.title_name = "downloadclock";
                    data_installed4.type = "downloadclock";
                    data_installed4.pkg = onlineClockSkinLocalNode.getPackageName();
                    data_installed4.filePath = "/data/data/com.bid.launcherwatch//WiiwearClockSkin/" + onlineClockSkinLocalNode.getPackageName();
                    data_installed4.previewPath = "/data/data/com.bid.launcherwatch//WiiwearClockSkin/preview/" + onlineClockSkinLocalNode.getPackageName() + ".png";
                    data_installed4.img_preview = null;
                    minstalledClocks.add(data_installed4);
                    cur.moveToNext();
                }
                cur.close();
            }
        } catch (SQLiteException e) {
            Log.e("xiaocai_SQ", "SQLiteException = " + e);
        } finally {
            mDb.close();
        }
        if (ClockUtil.mCustomDialClock != null) {
            installedClock data_installed5 = new installedClock();
            data_installed5.title_name = context.getResources().getString(ClockUtil.mCustomDialClock.mTitleId);
            data_installed5.img_preview_id = ClockUtil.mCustomDialClock.mThumbImageId;
            data_installed5.type = "bgsettingclock";
            data_installed5.filePath = data_installed5.type;
            minstalledClocks.add(data_installed5);
        }
    }

    public static void setClockFace(Context context, FrameLayout mClockHost, int index) {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        if (mWallpaperConnection != null && mWallpaperConnection.mConnected) {
            mWallpaperConnection.disconnect();
            mWallpaperConnection = null;
            Log.e("wall", "disconnect");
        }
        if (!((installedClock) getInstalledClocks().get(index)).serviceName.equals("")) {
            Log.e("wall", "connect1");
            if (mWallpaperConnection == null) {
                installedClock clockskin = (installedClock) getInstalledClocks().get(index);
                Log.e("wall", "connect2");
                Intent mWallpaperIntent = new Intent(WallpaperService.SERVICE_INTERFACE);
                //Log.d("TTTTTTTTTTTTTTTT:",clockskin.pkg  +" "+clockskin.serviceName);

                //mWallpaperIntent.setPackage("com.bid.launcherwatch");
//                Intent mWallpaperIntent=new Intent("android.service.wallpaper.WallpaperService");
//                mWallpaperIntent.setPackage("com.bid.launcherwatch");
//                //mWallpaperIntent.setPackage("com.bid.launcherwatch");
//                //mWallpaperIntent.setClassName(clockskin.pkg,clockskin.serviceName);
                mWallpaperIntent.setComponent(new ComponentName(clockskin.pkg, clockskin.serviceName));
//                //mWallpaperIntent.putExtra("package",clockskin.pkg);
//                //mWallpaperIntent.putExtra("service",clockskin.serviceName);
//                //context.startService(mWallpaperIntent);
                mWallpaperConnection = new WallpaperConnection(context,mWallpaperIntent,mClockHost);
                if (!mWallpaperConnection.connect()) {
                    mWallpaperConnection = null;
                }
//                //context.bindService(mWallpaperIntent,mWallpaperConnection,Context.BIND_AUTO_CREATE);
                Log.e("wall", "connect");

//                Intent in = new Intent();
//                in.setAction("broadcast.to.service.class");
//                in.putExtra("package", clockskin.pkg);
//                in.putExtra("service",clockskin.serviceName);
                //in.p("classConnection",mWallpaperConnection);
                //Log.e("SSSSSSSSSSSSSSSSSS:",clockskin.pkg +"  "+clockskin.serviceName);
                //LocalBroadcastManager.getInstance(context).sendBroadcast(in);
            }
            if (((installedClock) getInstalledClocks().get(index)).type.equals("liveClockSkin")) {
                mClockHost.removeAllViews();
                mClockView = null;
                return;
            }
        }
        try {
            ArrayList<installedClock> installedClocks = getInstalledClocks();
            LayoutInflater inflater = LayoutInflater.from(context);
            LayoutParams params = new LayoutParams(-1, -1);
            if (mClockHost != null) {
                if (index < ClockUtil.mClockList.length) {
                    mClockView = inflater.inflate(ClockUtil.mClockList[index].mViewId, null);
                } else if (((installedClock) installedClocks.get(index)).type.equals("bgsettingclock")) {
                    mClockView = inflater.inflate(ClockUtil.mCustomDialClock.mViewId, null);
                } else {
                    String clockSkinFullPath = ((installedClock) installedClocks.get(index)).filePath;
                    if (clockSkinFullPath == null) {
                        mClockView = new MyAnalogClock3(context, null);
                    } else {
                        String substring = clockSkinFullPath.substring(0, clockSkinFullPath.lastIndexOf("/"));
                        mClockView = new MyAnalogClock3(context, clockSkinFullPath);
                    }
                }
                mClockHost.removeAllViews();
                mClockHost.addView(mClockView, params);
            }
        } catch (Exception e) {
            setClockIndex(context, 0);
            throw e;
        }
    }

    public static void getAndroidWearClock(Context mContext) {
        Resources resources = mContext.getResources();
        PackageManager mPackageManager = mContext.getPackageManager();
        for (ResolveInfo resolveInfo : mPackageManager.queryIntentServices(new Intent("android.service.wallpaper.WallpaperService").addCategory("com.google.android.wearable.watchface.category.WATCH_FACE"), PackageManager.GET_META_DATA)) {
            Drawable drawable = mPackageManager.getDrawable(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.metaData.getInt("com.google.android.wearable.watchface.preview_circular"), resolveInfo.serviceInfo.applicationInfo);
            installedClock data_installed = new installedClock();
            data_installed.pkg = resolveInfo.serviceInfo.packageName;
            data_installed.serviceName = resolveInfo.serviceInfo.name;
            data_installed.resolveInfo = resolveInfo;
            data_installed.type = "liveClockSkin";
            data_installed.configurationAction = resolveInfo.serviceInfo.metaData.getString("com.google.android.wearable.watchface.wearableConfigurationAction");
            data_installed.filePath = data_installed.serviceName;
            minstalledClocks.add(data_installed);
        }
    }

    public static ArrayList<installedClock> getInstalledClocks() {
        return minstalledClocks;
    }

    public static int getAllClockCount() {
        return minstalledClocks.size();
    }

    public static int getBTClockfaceIndex(String clockfacePath) {
        int index = -1;
        if (minstalledClocks.size() > 0) {
            for (installedClock clock : minstalledClocks) {
                index++;
                if (clock.type.equals("sdclock") && clock.filePath.contains(".btclockface") && clock.filePath.substring(clock.filePath.lastIndexOf("/") + 1).equals(clockfacePath)) {
                    return index;
                }
            }
        }
        return 0;
    }

    public static int getDownloadClockfaceIndex(String pkg) {
        int index = -1;
        if (minstalledClocks.size() > 0) {
            for (installedClock clock : minstalledClocks) {
                index++;
                if (clock.type.equals("downloadclock") && clock.pkg.equals(pkg)) {
                    return index;
                }
            }
        }
        return 0;
    }

    public static void setClockIndex(Context context, int index) {
        mclockIndex = index;
        try {
            installedClock clock = (installedClock) getInstalledClocks().get(index);
            if (clock != null) {
                mIndexKey = clock.filePath;
            }
        } catch (Exception e) {
            mclockIndex = 0;
            mIndexKey = "";
        }
        Editor mEditor = context.getSharedPreferences("clockview_settings", 0).edit();
        mEditor.putInt("clockview_index", index);
        mEditor.putString("clockview_indexkey", mIndexKey);
        mEditor.commit();
    }

    public static int getClockIndex(Context context) {
        mclockIndex = context.getSharedPreferences("clockview_settings", 0).getInt("clockview_index", 0);
        return mclockIndex;
    }

    public static String getClockPath(Context context) {
        return context.getSharedPreferences("clockview_settings", 0).getString("clockview_indexkey", "");
    }

    public static void setBatteryLevel(Context context, float level) {
        mBatteryLever = level;
    }

    public static float getBatteryLevel(Context context) {
        return mBatteryLever;
    }

    public static void setBatteryCharging(Context context, boolean charging) {
        mBatteryCharging = charging;
    }

    public static boolean getIsBatteryCharging(Context context) {
        return mBatteryCharging;
    }

    public static void setUnreadPhone(Context context, int unreadPhone) {
        mUnreadPhone = unreadPhone;
    }

    public static int getUnreadPhone(Context context) {
        return mUnreadPhone;
    }

    public static void setUnreadSMS(Context context, int unreadSms) {
        mUnreadSMS = unreadSms;
    }

    public static int getUnreadSMS(Context context) {
        return mUnreadSMS;
    }

    public static int getSteps(Context context) {
        mSteps = System.getInt(context.getContentResolver(), "today_steps", 0);
        return mSteps;
    }

    public static int getTargetSteps(Context context) {
        mStepsTarget = System.getInt(context.getContentResolver(), "step_target_count", 8000);
        return mStepsTarget;
    }

    public static int getRate(Context context) {
        mHeartRate = System.getInt(context.getContentResolver(), "heart_rate", 0);
        return mHeartRate;
    }

    public static String getDistance(Context context) {
        String s = System.getString(context.getContentResolver(), "today_distance");
        return s == null ? "0.0" : s;
    }

    public static float getTargetDistance(Context context) {
        return System.getFloat(context.getContentResolver(), "today_distancetarget", 5.0f);
    }

    public static String getCalories(Context context) {
        String s = System.getString(context.getContentResolver(), "today_calories");
        return s == null ? "0" : s;
    }

    public static int getTargetCalories(Context context) {
        return System.getInt(context.getContentResolver(), "today_caloriestarget", 300);
    }

    public static int getWeatherTemp(Context context) {
        WeatherTemp = System.getInt(context.getContentResolver(), "WeatherTemp", 23);
        return WeatherTemp;
    }

    public static int getWeatherIcon(Context context) {
        WeatherIcon = System.getInt(context.getContentResolver(), "WeatherIcon", 10);
        return WeatherIcon;
    }

    public static void search(File fileold) {
        try {
            File[] files = fileold.listFiles();
            if (files.length > 0) {
                for (int j = 0; j < files.length; j++) {
                    if (files[j].isDirectory()) {
                        search(files[j]);
                    } else if (files[j].getName().indexOf("img_clock_preview.png") > -1 || files[j].getName().indexOf("clock_skin_model.png") > -1) {
                        mClocksSkinPath.add(files[j].getPath());
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public static void reloadUnreadPhoneAndSMS(Context context) {
        setUnreadPhone(context, getPhoneCounts(context));
        setUnreadSMS(context, getSmsCounts(context));
    }

    public static int getPhoneCounts(Context context) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.READ_CALL_LOG},
                    MY_PERMISSIONS_REQUEST_READ_CALL_LOG);

        }
        else
        {
            Cursor cursor = context.getContentResolver().query(Calls.CONTENT_URI, new String[]{"type"}, " type=? and new=?", new String[]{"3", "1"}, "date desc");
            if (cursor == null) {
                return 0;
            }
            int result = cursor.getCount();
            cursor.close();
            return result;
        }
        return 0;

    }

    public static int getSmsCounts(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.READ_SMS},
                    MY_PERMISSIONS_REQUEST_READ_SMS);

        }
        else
        {
            Cursor csr = context.getContentResolver().query(Uri.parse("content://sms"), null, "type = 1 and read = 0", null, null);
            if (csr == null) {
                return 0;
            }
            int result = csr.getCount();
            csr.close();
            return result;
        }
        return 0;

    }

    public static int clearAppCache(Context context) {
        if (isServiceRunning(context)) {
            return 2;
        }
        try {
            File file_cache = context.getCacheDir();
            if (file_cache.exists()) {
                File[] files = file_cache.listFiles();
                for (File delete : files) {
                    delete.delete();
                }
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static boolean isServiceRunning(Context context) {
        for (RunningServiceInfo service : ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE)) {
            if (!"com.wiiteck.clockpreviewer.online.ClockSkinDownloadService".equals(service.service.getClassName())) {
                if ("com.bid.launcherwatch.online.ClockSkinDownloadService".equals(service.service.getClassName())) {
                }
            }
            return true;
        }
        return false;
    }

    public static void setLiveWatchFaceVisibility(boolean visibility) {
        if (mWallpaperConnection != null && mWallpaperConnection.mEngine != null) {
            try {
                mWallpaperConnection.mEngine.setVisibility(visibility);
            } catch (RemoteException e) {
            }
        }
    }

    public static boolean dispatchTouchEvent(Context context, MotionEvent ev) {
        if (!(mWallpaperConnection == null || mWallpaperConnection.mEngine == null)) {
            try {
                mWallpaperConnection.mEngine.dispatchPointer(MotionEvent.obtainNoHistory(ev));
            } catch (RemoteException e) {
            }
        }
        if (ev.getAction() == 0) {
            ((FragmentActivity) context).onUserInteraction();
        }
        boolean handled = ((FragmentActivity) context).getWindow().superDispatchTouchEvent(ev);
        if (!handled) {
            handled = ((FragmentActivity) context).onTouchEvent(ev);
        }
        if (!(handled || mWallpaperConnection == null || mWallpaperConnection.mEngine == null)) {
            int action = ev.getActionMasked();
            if (action == 1) {
                try {
                    mWallpaperConnection.mEngine.dispatchWallpaperCommand("android.wallpaper.tap", (int) ev.getX(), (int) ev.getY(), 0, null);
                } catch (RemoteException e2) {
                }
            } else if (action == 6) {
                int pointerIndex = ev.getActionIndex();
                try {
                    mWallpaperConnection.mEngine.dispatchWallpaperCommand("android.wallpaper.secondaryTap", (int) ev.getX(pointerIndex), (int) ev.getY(pointerIndex), 0, null);
                }catch (Exception e) {
                    Log.d("Catch:",e.getMessage());
                }
            }
        }
        return handled;
    }

    public static void onTouch(MotionEvent ev) {
        if (ev.getAction() == 1) {
            float x = ev.getX();
            float y = ev.getY();
            if (mClockView == null) {
                return;
            }
            if (mClockView instanceof RelativeLayout) {
                View cv = mClockView.findViewById(R.id.watchtable);
                if (cv != null && (cv instanceof WiiteWatchFace)) {
                    ((WiiteWatchFace) cv).onTouch(x, y);
                }
            } else if (mClockView instanceof WiiteWatchFace) {
                ((WiiteWatchFace) mClockView).onTouch(x, y);
            }
        }
    }
}
