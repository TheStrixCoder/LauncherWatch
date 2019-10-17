package com.bid.launcherwatch;

import android.Manifest;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemProperties;
import android.os.Vibrator;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.bid.launcherwatch.CustomDigitalClockSave.Callback;
import com.bid.launcherwatch.WatchApp.installedClock;
import com.bid.launcherwatch.online.ClockSkinDBHelper;
import com.bid.launcherwatch.util.ShellUtils;
import java.io.File;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import com.bid.launcherwatch.util.ShellUtils;

public class MainActivity extends FragmentActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private final int DELAYED_TIME = 6000;
    private final int EXIT_BATTERY_SAVER_MODE = 1002;
    private final int SCRREN_OFF_DELAYED = 1001;
    private final int TO_BATTERY_SAVER_MODE = 1003;
    private boolean firstEnter = true;
    private WatchAppWidgetHost mAppWidgetHost = null;
    private AppWidgetManager mAppWidgetManager = null;
    private final BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean mCharged = true;
            String action = intent.getAction();
            Log.d(MainActivity.TAG, "onReceive: action = " + action);
            if (action.equals("android.intent.action.BATTERY_CHANGED")) {
                int status = intent.getIntExtra("status", 1);
                if (status != 5) {
                    mCharged = false;
                }
                boolean isCharging = mCharged || status == 2;
                int level = (int) ((((float) intent.getIntExtra("level", 0)) * 100.0f) / ((float) intent.getIntExtra("scale", 100)));
                int triggerLevel = Settings.Global.getInt(MainActivity.this.getContentResolver(), "low_power_trigger_level", 5);
                WatchApp.setBatteryLevel(context, (float) level);
                WatchApp.setBatteryCharging(MainActivity.this, isCharging);
                Log.i("battery_change", "  isCharging = " + isCharging);
                Log.i("battery_change", "  isPowerSaveMode = " + WatchApp.mBatterySaver + "  level = " + level + "  triggerLevel = " + triggerLevel);
            }
        }
    };
    FrameLayout mClockHost;
    private CustomDigitalClockSave mClockSaveView;
    private float mConfigurationFontScale;
    ExecutorService mExecutorService;
    Handler mHander = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1001:
                    ScreenControl.getInstance(MainActivity.this).ScreenOff();
                    return;
                case 1002:
                    MainActivity.this.refreshActivity(false);
                    Log.i(MainActivity.TAG, "handleMessage   setPowerSaveMode(false)");
                    return;
                case 1003:
                    MainActivity.this.refreshActivity(true);
                    return;
                default:
                    return;
            }
        }
    };

    private boolean mHasFocus = false;
    /* access modifiers changed from: private */
    public IdleFragment mIdleFragment;
    /* access modifiers changed from: private */
    public MainVerticalViewPager mPager;
    private PagerAdapter mPagerAdapter;
    /* access modifiers changed from: private */
    public PowerManager mPowerManager;
    private final BroadcastReceiver mPowerSaveReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(MainActivity.TAG, "mPowerSaveReceiver action = " + action);
            if (action.equals("com.android.SSaction.powerSaveMode_change")) {
                WatchApp.mBatterySaver = MainActivity.this.mPowerManager.isPowerSaveMode();
                MainActivity.this.refreshActivity(WatchApp.mBatterySaver);
            }
        }
    };

    private final BroadcastReceiver mPowerSaver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (MainActivity.this.mPowerManager.isPowerSaveMode()) {
                MainActivity.this.mHander.sendEmptyMessage(1003);
                return;
            }
            MainActivity.this.mHander.sendEmptyMessage(1002);
            MainActivity.this.mHander.removeMessages(1001);
        }
    };

    private Dialog mProgressDialog = null;
    /* access modifiers changed from: private */
    public QuickSettingsFragment mQuickSettingsFragment = new QuickSettingsFragment();
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(MainActivity.TAG, "guoxiaolong: action = " + action);
            String packageName = intent.getData().getSchemeSpecificPart();
            Log.d(MainActivity.TAG, "guoxiaolong: packageName1 = " + packageName);
            if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
                Iterator resolveInfo$iterator = MainActivity.this.getPackageManager().queryIntentServices(new Intent("android.service.wallpaper.WallpaperService").addCategory("com.google.android.wearable.watchface.category.WATCH_FACE"), PackageManager.GET_META_DATA).iterator();
                while (true) {
                    if (!resolveInfo$iterator.hasNext()) {
                        break;
                    }
                    ResolveInfo resolveInfo = (ResolveInfo) resolveInfo$iterator.next();
                    Log.d(MainActivity.TAG, "guoxiaolong: packageName2 = " + resolveInfo.serviceInfo.packageName);
                    if (packageName != null && packageName.equals(resolveInfo.serviceInfo.packageName)) {
                        Intent it = new Intent("com.update.installclock");
                        it.putExtra("action_str", "updateclock");
                        MainActivity.this.sendBroadcast(it);
                        break;
                    }
                }
                if (MainActivity.this.mIdleFragment != null) {
                    MainActivity.this.mIdleFragment.refreshApplist();
                }
            } else if ("android.intent.action.PACKAGE_REMOVED".equals(action)) {
                Iterator clock$iterator = WatchApp.getInstalledClocks().iterator();
                while (true) {
                    if (clock$iterator.hasNext()) {
                        if (((installedClock) clock$iterator.next()).pkg.equals(packageName)) {
                            Intent it2 = new Intent("com.update.installclock");
                            it2.putExtra("action_str", "updateclock");
                            MainActivity.this.sendBroadcast(it2);
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (MainActivity.this.mIdleFragment != null) {
                    MainActivity.this.mIdleFragment.refreshApplist();
                }
            }
        }
    };

    private final BroadcastReceiver mScreenUpdateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(MainActivity.TAG, "onReceive: action = " + action);
            if ("com.bid.launcherwatch.NOTIFICATION_LISTENER.POSTED".equals(action)) {
                if (MainActivity.this.mPager != null) {
                }
            } else if ("com.bid.launcherwatch.NOTIFICATION_LISTENER.REMOVED".equals(action)) {
                if (NotificationFragment.getInstance() != null && NotificationFragment.getInstance().getPageCount() == 0) {
                    Log.d(MainActivity.TAG, "Notification removed, pageCount=" + NotificationFragment.getInstance().getPageCount());
                    MainActivity.this.sendBroadcast(new Intent("com.bid.launcherwatch.NOTIFICATION_LISTENER.HIDE_INDICATOR"));
                }
            } else if ("com.bid.launcherwatch.NOTIFICATION_LISTENER.REFRESH".equals(action)) {
                if (MainActivity.this.mPager != null) {
                    MainActivity.this.mPager.invalidate();
                }
            } else if ("android.intent.action.SCREEN_OFF".equals(action)) {
                if (MainActivity.this.mPager != null) {
                    MainActivity.this.mPager.setCurrentItem(1, false);
                }
                if (MainActivity.this.mIdleFragment != null) {
                    MainActivity.this.mIdleFragment.backToClock();
                }
            } else if ("android.intent.action.SCREEN_ON".equals(action)) {
                if (MainActivity.this.mPowerManager.isPowerSaveMode()) {
                }
            } else if (action.equals("ipc_handphone_state_change")) {
                boolean isConnected = 3 == intent.getIntExtra("state", 0);
                MainActivity.this.mQuickSettingsFragment.mQuickSettingsSubFragment.setPhoneConnectedState(isConnected);
                String str = context.getResources().getString(R.string.phone_disconnected_toast).toString();
                if (!isConnected) {
                }
            }
        }
    };

    public SportsFragment mSportsFragment = new SportsFragment();
    private TimeZoneLocalChangedReciever mTZLocalReciever = new TimeZoneLocalChangedReciever();
    private final BroadcastReceiver mUnreadLoader = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            if ("com.mediatek.action.UNREAD_CHANGED".equals(intent.getAction())) {
                ComponentName componentName = (ComponentName) intent.getComponent();
                int unreadNum = intent.getIntExtra("com.mediatek.intent.extra.UNREAD_NUMBER", -1);
                if (componentName != null && unreadNum != -1) {
                    Log.d("xiaocai_unread", "Receive unread broadcast: componentName = " + componentName + ", unreadNum = " + unreadNum);
                    ComponentName componentMms = new ComponentName("com.android.mms", "com.android.mms.ui.BootActivity");
                    ComponentName componentPhone = new ComponentName("com.android.dialer", "com.android.dialer.DialtactsActivity");
                    if (componentMms.equals(componentName)) {
                        WatchApp.setUnreadSMS(context, unreadNum);
                        if (MainActivity.this.mIdleFragment != null) {
                            MainActivity.this.mIdleFragment.refreshApplist();
                        }
                    } else if (componentPhone.equals(componentName)) {
                        WatchApp.setUnreadPhone(context, unreadNum);
                        if (MainActivity.this.mIdleFragment != null) {
                            MainActivity.this.mIdleFragment.refreshApplist();
                        }
                    }
                }
            }
        }
    };

    private final BroadcastReceiver mUpdateClockReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Context context2 = context;
            String str = intent.getStringExtra("action_str");
            MainActivity.this.showOnlineLoadingDialog(context);
            if (str.equals("installclock")) {
                ContentValues values = new ContentValues();
                values.put("clockName", intent.getStringExtra("THEME_NAME"));
                values.put("skinid", intent.getStringExtra("PACKAG_NAME"));
                values.put("filePath", intent.getStringExtra("APK_NAME"));
                values.put("clocktype", intent.getStringExtra("VERSION"));
                values.put("state", Integer.valueOf(1));
                new installUpdateTask(context, values).executeOnExecutor(MainActivity.this.mExecutorService, new Void[0]);
            } else if (str.equals("deleteclock")) {
                new uninstallUpdateTask(context, intent.getStringExtra("PACKAG_NAME"), intent.getIntExtra("INDEX", 0)).executeOnExecutor(MainActivity.this.mExecutorService, new Void[0]);
            } else if (str.equals("cleanrelaodclock")) {
                new cleanRelaodClocksinTask(context).executeOnExecutor(MainActivity.this.mExecutorService, new Void[0]);
            } else if (str.equals("installbtclock")) {
                new installBTClockfaceTask(context, intent.getStringExtra("path")).executeOnExecutor(MainActivity.this.mExecutorService, new Void[0]);
            } else if (str.equals("updateclock")) {
                new updateClockfaceTask(context).executeOnExecutor(MainActivity.this.mExecutorService, new Void[0]);
            }
        }
    };

    private Vibrator mVibrator;
    /* access modifiers changed from: private */
    public View top_black;

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            if (position == 1) {
                return MainActivity.this.mIdleFragment;
            }
            if (position == 0) {
                Log.d("MainActivity", "getItem.QUICKSETTINGS_PAGE_INDEX");
                return MainActivity.this.mQuickSettingsFragment;
            } else if (position == 2) {
                return MainActivity.this.mSportsFragment;
            } else {
                return null;
            }
        }

        public int getCount() {
            return 3;
        }
    }

    public class TimeZoneLocalChangedReciever extends BroadcastReceiver {
        public TimeZoneLocalChangedReciever() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.intent.action.TIMEZONE_CHANGED")) {
                if (MainActivity.this.mIdleFragment != null) {
                    WatchApp.setClockFace(MainActivity.this, MainActivity.this.mClockHost, WatchApp.getClockIndex(MainActivity.this));
                }
            } else if (intent.getAction().equals("android.intent.action.LOCALE_CHANGED")) {
                if (MainActivity.this.mIdleFragment != null) {
                    MainActivity.this.mIdleFragment.refreshApplist();
                }
            } else if (action.equals("watch_app_list_change")) {
                int style = System.getInt(MainActivity.this.getContentResolver(), "watch_app_list_style", 0);
                if (MainActivity.this.mIdleFragment.getAppListStyle() != style) {
                    MainActivity.this.mIdleFragment.setAppListStyle(style);
                    MainActivity.this.mIdleFragment.update();
                }
            } else if (action.equals("com.wiitetech.wiiwatch.write_settings")) {
                String key = intent.getStringExtra("key");
                if (key.equals("step_target_count")) {
                    String step = intent.getStringExtra(key);
                    System.putString(MainActivity.this.getContentResolver(), key, step);
                    Log.d("write_settings", "step: " + step);
                } else if (key.equals("persist.sys.raise.wakeup")) {
                    String raise = intent.getStringExtra(key);
                    System.putString(MainActivity.this.getContentResolver(), key, raise);
                    SystemProperties.set("persist.sys.raise.wakeup", String.valueOf(raise));
                    Log.d("write_settings", "raise: " + raise);
                } else if (key.equals("ambient_clock_switch")) {
                    int ambient = intent.getIntExtra(key, 0);
                    System.putInt(MainActivity.this.getContentResolver(), key, ambient);
                    Log.d("write_settings", "ambient: " + ambient);
                }
            }
        }
    }

    private class chooseClockfaceTask extends AsyncTask<Void, Integer, Void> {
        private Context mContext;
        private Intent mData;
        private int mRequestCode;
        private int mResultCode;

        public chooseClockfaceTask(Context context, int requestCode, int resultCode, Intent data) {
            this.mContext = context;
            this.mRequestCode = requestCode;
            this.mResultCode = resultCode;
            this.mData = data;
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... params) {
            return null;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Void result) {
            if (100 != this.mRequestCode) {
                return;
            }
            if (50 == this.mResultCode) {
                int index = this.mData.getIntExtra("index", 0);
                WatchApp.setClockIndex(this.mContext, index);
                MainActivity.this.ChangeClockApply(index);
                return;
            }
            MainActivity.this.ChangeClockApply(WatchApp.getClockIndex(this.mContext));
        }
    }

    private class cleanRelaodClocksinTask extends AsyncTask<Void, Integer, Integer> {
        private Context mContext;

        public cleanRelaodClocksinTask(Context context) {
            this.mContext = context;
        }

        /* access modifiers changed from: protected */
        public Integer doInBackground(Void... params) {
            int ret = WatchApp.clearAppCache(this.mContext);
            if (ret == 1) {
                WatchApp.updateInstalledClocks(this.mContext);
                WatchApp.setClockIndex(this.mContext, 0);
            }
            return Integer.valueOf(ret);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Integer result) {
            switch (result.intValue()) {
                case 1:
                    Toast.makeText(this.mContext, R.string.clearCacheSuccess, Toast.LENGTH_SHORT).show();
                    MainActivity.this.ChangeClockApply(0);
                    Intent it = new Intent("com.update.installclock.done");
                    it.putExtra("action_str", "cleanrelaodclock");
                    MainActivity.this.sendBroadcast(it);
                    break;
                case 2:
                    Toast.makeText(this.mContext, R.string.onDownloading, Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(this.mContext, R.string.clearCacheFailed, Toast.LENGTH_SHORT).show();
                    break;
            }
            MainActivity.this.dismissOnlineLoadingDialog();
        }
    }

    private class getClockFaceAndSetTask extends AsyncTask<Void, Integer, Void> {
        private Context mContext;

        public getClockFaceAndSetTask(Context context) {
            this.mContext = context;
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... params) {
            WatchApp.updateInstalledClocks(this.mContext);
            return null;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Void result) {
            int clockCont = WatchApp.getAllClockCount();
            if (clockCont != 0) {
                int index = WatchApp.getClockIndex(this.mContext);
                if (index >= clockCont || index < 0) {
                    index = 0;
                }
                String path = WatchApp.getClockPath(this.mContext);
                if (path.equals("")) {
                    WatchApp.setClockIndex(this.mContext, index);
                } else {
                    int i = 0;
                    while (true) {
                        if (i >= clockCont) {
                            break;
                        } else if (((installedClock) WatchApp.getInstalledClocks().get(i)).filePath.equals(path)) {
                            index = i;
                            WatchApp.setClockIndex(this.mContext, index);
                            break;
                        } else {
                            i++;
                        }
                    }
                    if (i == clockCont) {
                        index = 0;
                        WatchApp.setClockIndex(this.mContext, 0);
                    }
                }
                WatchApp.setClockFace(this.mContext, MainActivity.this.mClockHost, index);
            }
        }
    }

    private class installBTClockfaceTask extends AsyncTask<Void, Integer, Void> {
        private String mClockfacePath;
        private Context mContext;

        public installBTClockfaceTask(Context context, String values) {
            this.mContext = context;
            this.mClockfacePath = values;
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... params) {
            WatchApp.updateInstalledClocks(this.mContext);
            return null;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Void result) {
            int index = WatchApp.getBTClockfaceIndex(this.mClockfacePath);
            if (index != -1) {
                WatchApp.setClockIndex(this.mContext, index);
                MainActivity.this.ChangeClockApply(index);
                Intent it = new Intent("com.update.installclock.done");
                it.putExtra("action_str", "cleanrelaodclock");
                MainActivity.this.sendBroadcast(it);
            }
            MainActivity.this.dismissOnlineLoadingDialog();
        }
    }

    private class installUpdateTask extends AsyncTask<Void, Integer, Void> {
        private Context mContext;
        private ContentValues mValues;

        public installUpdateTask(Context context, ContentValues values) {
            this.mContext = context;
            this.mValues = values;
        }

        /* JADX INFO: finally extract failed */
        /* access modifiers changed from: protected */
        public Void doInBackground(Void... params) {
            SQLiteDatabase mDb = new ClockSkinDBHelper(this.mContext, "clockskin.db", null, 1).getReadableDatabase();
            try {
                String clockskinId = this.mValues.getAsString("skinid");
                mDb.delete("clockskin_install_list", "skinid=?", new String[]{clockskinId});
                mDb.insert("clockskin_install_list", null, this.mValues);
                Cursor countCur = mDb.rawQuery("select * from clockskin_online_list WHERE skinid=?", new String[]{clockskinId});
                if (countCur.getCount() > 0) {
                    countCur.moveToFirst();
                    do {
                        ContentValues v = new ContentValues();
                        v.put("state", Integer.valueOf(1));
                        int update = mDb.update("clockskin_online_list", v, "skinid=?", new String[]{clockskinId});
                        Intent it = new Intent("BROADCAST_DOWNLOAD_STATE_FILTER");
                        it.putExtra("BROADCAST_DOWNLOAD_STATE", 1003);
                        MainActivity.this.sendBroadcast(it);
                    } while (countCur.moveToNext());
                }
                countCur.close();
                mDb.close();
                WatchApp.updateInstalledClocks(this.mContext);
                return null;
            } catch (Throwable th) {
                mDb.close();
                throw th;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Void result) {
            int index = WatchApp.getDownloadClockfaceIndex(this.mValues.getAsString("skinid"));
            WatchApp.setClockIndex(this.mContext, index);
            MainActivity.this.ChangeClockApply(index);
            Intent it = new Intent("com.update.installclock.done");
            it.putExtra("action_str", "installclock");
            MainActivity.this.sendBroadcast(it);
            MainActivity.this.dismissOnlineLoadingDialog();
        }
    }

    private class uninstallUpdateTask extends AsyncTask<Void, Integer, Integer> {
        private String mClockSkinId;
        private Context mContext;
        private int mSelectIndex;

        public uninstallUpdateTask(Context context, String skinId, int index) {
            this.mContext = context;
            this.mClockSkinId = skinId;
            this.mSelectIndex = index;
        }

        /* access modifiers changed from: protected */
        public Integer doInBackground(Void... params) {
            String path;
            installedClock clock = (installedClock) WatchApp.getInstalledClocks().get(this.mSelectIndex);
            if (clock.type.equals("liveClockSkin")) {
                ShellUtils.execCommand(new String[]{"pm uninstall " + clock.pkg}, true);
                return Integer.valueOf(0);
            }
            if (clock.type.equals("downloadclock")) {
                path = "/data/data/com.bid.launcherwatch//WiiwearClockSkin/" + this.mClockSkinId;
                MainActivity.this.delClockSkinFromDB(this.mClockSkinId, this.mContext);
            } else {
                path = clock.filePath;
            }
            File clockSkinDir = new File(path);
            if (clockSkinDir.isDirectory() && clockSkinDir.exists()) {
                MainActivity.this.delFolder(clockSkinDir.toString());
            }
            WatchApp.updateInstalledClocks(this.mContext);
            int index = WatchApp.getClockIndex(this.mContext);
            if (this.mSelectIndex < index) {
                index--;
            }
            WatchApp.setClockIndex(this.mContext, index);
            return Integer.valueOf(1);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Integer result) {
            if (result.intValue() == 1) {
                Intent it = new Intent("com.update.installclock.done");
                it.putExtra("action_str", "deleteclock");
                MainActivity.this.sendBroadcast(it);
                MainActivity.this.dismissOnlineLoadingDialog();
            }
        }
    }

    private class updateClockfaceTask extends AsyncTask<Void, Integer, Void> {
        private Context mContext;

        public updateClockfaceTask(Context context) {
            this.mContext = context;
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... params) {
            WatchApp.updateInstalledClocks(this.mContext);
            return null;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Void result) {
            int clockCont = WatchApp.getAllClockCount();
            if (clockCont != 0) {
                int index = WatchApp.getClockIndex(this.mContext);
                if (index >= clockCont || index < 0) {
                    index = 0;
                }
                String path = WatchApp.getClockPath(this.mContext);
                if (path.equals("")) {
                    WatchApp.setClockIndex(this.mContext, index);
                } else {
                    int i = 0;
                    while (true) {
                        if (i >= clockCont) {
                            break;
                        } else if (((installedClock) WatchApp.getInstalledClocks().get(i)).filePath.equals(path)) {
                            WatchApp.setClockIndex(this.mContext, i);
                            break;
                        } else {
                            i++;
                        }
                    }
                    if (i == clockCont) {
                        WatchApp.setClockIndex(this.mContext, 0);
                        WatchApp.setClockFace(this.mContext, MainActivity.this.mClockHost, 0);
                    }
                }
                Intent it = new Intent("com.update.installclock.done");
                it.putExtra("action_str", "cleanrelaodclock");
                MainActivity.this.sendBroadcast(it);
                MainActivity.this.dismissOnlineLoadingDialog();
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        Util.addActivity(this);
        checkPerm();
        setContentView(R.layout.activity_screen_slide);
        this.mClockHost = (FrameLayout) findViewById(R.id.bottom_clock);
        this.mExecutorService = Executors.newSingleThreadExecutor();
        new getClockFaceAndSetTask(this).executeOnExecutor(this.mExecutorService, new Void[0]);
        this.top_black = findViewById(R.id.top_black);
        this.top_black.setAlpha(0.0f);
        this.mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mAppWidgetManager = AppWidgetManager.getInstance(this);
        this.mAppWidgetHost = new WatchAppWidgetHost(this, 1024);
        //for (int id : this.mAppWidgetHost.getAppWidgetIds()) {
       //     this.mAppWidgetHost.deleteAppWidgetId(id);
        //}
        this.mAppWidgetHost.startListening();
        this.mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        this.mPager = (MainVerticalViewPager) findViewById(R.id.pager);
        this.mClockSaveView = (CustomDigitalClockSave) findViewById(R.id.clock_view);
        this.mClockSaveView.setClickCallback(new Callback() {
            public void onClick() {
                //MainActivity.this.mPowerManager.setPowerSaveMode(false);
                MainActivity.this.mHander.sendEmptyMessage(1002);
                MainActivity.this.mHander.removeMessages(1001);
            }
        });
        int style = System.getInt(getContentResolver(), "watch_app_list_style", 0);
        Log.d(TAG, "style" + style);
        this.mIdleFragment = new IdleFragment(style);
        this.mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        this.mPager.setAdapter(this.mPagerAdapter);
        this.mPager.setOffscreenPageLimit(3);
        this.mPager.setCurrentItem(1, true);
        this.mPager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
            public void onPageSelected(int mPosition) {
                Log.d(MainActivity.TAG, "onPageSelected, pos=" + mPosition);
            }
        });
        this.mPager.setOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 1) {
                    MainActivity.this.top_black.setAlpha(positionOffset);
                } else if (position == 0) {
                    MainActivity.this.top_black.setAlpha(1.0f - positionOffset);
                }
            }

            public void onPageSelected(int position) {
            }

            public void onPageScrollStateChanged(int state) {
            }
        });
        IntentFilter mScreenUpdateFilter = new IntentFilter();
        mScreenUpdateFilter.addAction("com.bid.launcherwatch.NOTIFICATION_LISTENER.POSTED");
        mScreenUpdateFilter.addAction("com.bid.launcherwatch.NOTIFICATION_LISTENER.REMOVED");
        mScreenUpdateFilter.addAction("com.bid.launcherwatch.NOTIFICATION_LISTENER.REFRESH");
        mScreenUpdateFilter.addAction("android.intent.action.SCREEN_OFF");
        mScreenUpdateFilter.addAction("android.intent.action.SCREEN_ON");
        mScreenUpdateFilter.addAction("ipc_handphone_state_change");
        registerReceiver(this.mScreenUpdateReceiver, mScreenUpdateFilter);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addAction("android.intent.action.PACKAGE_REPLACED");
        filter.addAction("android.intent.action.PACKAGE_CHANGED");
        filter.addDataScheme("package");
        registerReceiver(this.mReceiver, filter);
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction("android.intent.action.TIMEZONE_CHANGED");
        filter1.addAction("android.intent.action.LOCALE_CHANGED");
        filter1.addAction("watch_app_list_change");
        filter1.addAction("com.wiitetech.wiiwatch.write_settings");
        registerReceiver(this.mTZLocalReciever, filter1);
        IntentFilter batteryFilter = new IntentFilter();
        batteryFilter.addAction("android.intent.action.BATTERY_CHANGED");
        registerReceiver(this.mBatteryReceiver, batteryFilter);
        registerReceiver(this.mUpdateClockReceiver, new IntentFilter("com.update.installclock"));
        IntentFilter unreadLoaderfilter = new IntentFilter();
        unreadLoaderfilter.addAction("com.mediatek.action.UNREAD_CHANGED");
        registerReceiver(this.mUnreadLoader, unreadLoaderfilter);
        checkPerm();
        WatchApp.reloadUnreadPhoneAndSMS(this);
        registerReceiver(this.mPowerSaver, new IntentFilter("android.os.action.POWER_SAVE_MODE_CHANGING"));
        this.mConfigurationFontScale = getResources().getConfiguration().fontScale;
    }
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.WRITE_CONTACTS,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.CAMERA,
            Manifest.permission.VIBRATE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private void checkPerm() {
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    private boolean hasPermissions(MainActivity context, String[] permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void ChangeClockApply(int index) {
        WatchApp.setClockFace(this, this.mClockHost, index);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        if (WatchApp.isFristLaunch) {
            if (this.mPowerManager.isPowerSaveMode()) {
                this.mHander.sendEmptyMessageDelayed(1003, 2000);
            }
            WatchApp.isFristLaunch = false;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        new chooseClockfaceTask(this, requestCode, resultCode, data).executeOnExecutor(this.mExecutorService, new Void[0]);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getChooseWatchView(int index) {
        Log.d(TAG, "getChooseWatchView");
        Intent intent = new Intent();
        intent.setClassName("com.bid.launcherwatch", "com.bid.launcherwatch.ChooseClockActivity");
        intent.putExtra("index", index);
        startActivityForResult(intent, 100);
        overridePendingTransition(R.anim.enter_anim, 0);
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        Util.removeActivity(this);
        unregisterReceiver(this.mScreenUpdateReceiver);
        unregisterReceiver(this.mReceiver);
        unregisterReceiver(this.mBatteryReceiver);
        unregisterReceiver(this.mTZLocalReciever);
        unregisterReceiver(this.mUpdateClockReceiver);
        unregisterReceiver(this.mUnreadLoader);
        unregisterReceiver(this.mPowerSaver);
        try {
            this.mAppWidgetHost.stopListening();
        } catch (NullPointerException ex) {
            Log.w(TAG, "problem while stopping AppWidgetHost during Launcher destruction", ex);
        }
        this.mAppWidgetHost = null;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown   keyCode=" + keyCode);
        if (keyCode == 4) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyUp  keyCode=" + keyCode);
        if (keyCode == 4) {
            if (!(this.mPager == null || this.mPager.getCurrentItem() == 1)) {
                this.mPager.setCurrentItem(1, false);
            }
            if (this.mIdleFragment != null) {
                this.mIdleFragment.backToClock();
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putBoolean("power_saver", WatchApp.mBatterySaver);
        outState.putBoolean("frist_launch", WatchApp.isFristLaunch);
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        WatchApp.mBatterySaver = savedInstanceState.getBoolean("power_saver", false);
        WatchApp.isFristLaunch = savedInstanceState.getBoolean("frist_launch", false);
        Log.d(TAG, "onRestoreInstanceState: mBatterySaver = " + WatchApp.mBatterySaver + "\n  isFristLaunch = " + WatchApp.isFristLaunch);
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent");
        super.onNewIntent(intent);
        if ("android.intent.action.MAIN".equals(intent.getAction())) {
            Log.d(TAG, "onNewIntent - ACTION_MAIN");
            if (this.mPager != null) {
                this.mPager.setCurrentItem(1, false);
            }
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d(TAG, "onWindowFocusChanged  hasFocus " + hasFocus);
        super.onWindowFocusChanged(hasFocus);
        this.mHasFocus = hasFocus;
        WatchApp.setTopActivityStatus(hasFocus);
        if (hasFocus && this.firstEnter) {
            this.firstEnter = false;
            String connect = System.getString(getContentResolver(), "wiiwatch_connect_state");
            if (connect != null && connect.equals("connected") && this.mQuickSettingsFragment != null && this.mQuickSettingsFragment.mQuickSettingsSubFragment != null) {
                this.mQuickSettingsFragment.mQuickSettingsSubFragment.setPhoneConnectedState(true);
            }
        }
    }

    /* access modifiers changed from: private */
    public void refreshActivity(boolean batterySaver) {
        Log.d(TAG, "refreshActivity");
        if (this.mPager != null && this.mClockSaveView != null) {
            if (batterySaver) {
                this.mPager.setVisibility(View.GONE);
                this.mClockSaveView.setVisibility(View.VISIBLE);
                this.mHander.removeMessages(1001);
                this.mHander.sendEmptyMessageDelayed(1001, 6000);
            } else {
                this.mPager.setVisibility(View.VISIBLE);
                this.mClockSaveView.setVisibility(View.GONE);
                this.mHander.removeMessages(1001);
            }
            Log.d("MainActivity", "refreshActivity  " + batterySaver);
        }
    }

    public WatchAppWidgetHost getWatchWidgetHost() {
        return this.mAppWidgetHost;
    }

    public AppWidgetManager getWatchWidgetManager() {
        Log.d(TAG, "getWatchWidgetManager");
        return this.mAppWidgetManager;
    }

    public void backToNoteView(boolean needWakeup) {
        if (needWakeup) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (!powerManager.isScreenOn()) {
                WakeLock wl = powerManager.newWakeLock(268435462, "launcher:bright");
                wl.acquire();
                wl.release();
            }
        }
        if (!(this.mPager == null || this.mPager.getCurrentItem() == 1)) {
            this.mPager.setCurrentItem(1, false);
        }
        if (this.mIdleFragment != null) {
            this.mIdleFragment.backToNotification();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.mConfigurationFontScale != newConfig.fontScale) {
            this.mConfigurationFontScale = newConfig.fontScale;
            if (this.mIdleFragment != null) {
            }
        }
    }

    public void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            String str = folderPath;
            new File(folderPath.toString()).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delAllFile(String path) {
        File temp;
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            String[] tempList = file.list();
            for (int i = 0; i < tempList.length; i++) {
                if (path.endsWith(File.separator)) {
                    temp = new File(path + tempList[i]);
                } else {
                    temp = new File(path + File.separator + tempList[i]);
                }
                if (temp.isFile()) {
                    temp.delete();
                }
                if (temp.isDirectory()) {
                    delAllFile(path + "/" + tempList[i]);
                    delFolder(path + "/" + tempList[i]);
                }
            }
        }
    }

    public void delClockSkinFromDB(String clockSkinId, Context c) {
        SQLiteDatabase mDb = new ClockSkinDBHelper(c, "clockskin.db", null, 1).getReadableDatabase();
        try {
            mDb.delete("clockskin_install_list", "skinid=?", new String[]{clockSkinId});
            Cursor countCur = mDb.rawQuery("select * from clockskin_online_list WHERE skinid=?", new String[]{clockSkinId});
            if (countCur.getCount() > 0) {
                countCur.moveToFirst();
                do {
                    ContentValues v = new ContentValues();
                    v.put("state", Integer.valueOf(0));
                    int update = mDb.update("clockskin_online_list", v, "skinid=?", new String[]{clockSkinId});
                    Intent it = new Intent("BROADCAST_DOWNLOAD_STATE_FILTER");
                    it.putExtra("BROADCAST_DOWNLOAD_STATE", 1003);
                    sendBroadcast(it);
                } while (countCur.moveToNext());
            }
        } finally {
            mDb.close();
        }
    }


    public void showOnlineLoadingDialog(Context context) {
        if (this.mProgressDialog == null) {
            this.mProgressDialog = new Dialog(context, R.style.OnlineFontPreview);
            this.mProgressDialog.requestWindowFeature(1);
            this.mProgressDialog.getWindow().setType(2003);
            this.mProgressDialog.setContentView(R.layout.global_progressbar);
            this.mProgressDialog.setCancelable(true);
            this.mProgressDialog.setCanceledOnTouchOutside(false);
            this.mProgressDialog.show();
        }
    }

    public void dismissOnlineLoadingDialog() {
        if (this.mProgressDialog != null) {
            try {
                this.mProgressDialog.dismiss();
            } catch (Exception e) {
                Log.e(TAG, "Exception when Dialog.dismiss()...");
            } catch (Throwable th) {
                this.mProgressDialog = null;
                throw th;
            }
            this.mProgressDialog = null;
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!WatchApp.getTopActivityStatus()) {
            return true;
        }
        if (WatchApp.getClockViewStatus()) {
            return WatchApp.dispatchTouchEvent(this, ev);
        }
        return super.dispatchTouchEvent(ev);
    }



}
