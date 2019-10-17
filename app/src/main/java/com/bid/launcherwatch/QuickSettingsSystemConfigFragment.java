package com.bid.launcherwatch;



import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.drawable.AnimationDrawable;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.Objects;

import static android.content.Context.TELEPHONY_SERVICE;

public class QuickSettingsSystemConfigFragment extends Fragment {
    private boolean SimState = false;
    private final String TAG = "QuickSettingsSystemConfigFragment";
//    OnClickListener click = new OnClickListener() {
//        public void onClick(DialogInterface dialog, int which) {
//            boolean z = false;
//            final int subId = SubscriptionManager.getDefaultSubscriptionId();
//            if (which == -1) {
//                QuickSettingsSystemConfigFragment.this.setNetworkTypeDone = false;
//                boolean isEnable = QuickSettingsSystemConfigFragment.this.mTelephonyManager.getDataEnabled(subId);
//                TelephonyManager r4 = QuickSettingsSystemConfigFragment.this.mTelephonyManager;
//                if (!isEnable) {
//                    z = true;
//                }
//                r4.setDataEnabled(subId, z);
//                QuickSettingsSystemConfigFragment.this.myHandler.sendEmptyMessageDelayed(55, 2000);
//                final int networkType = !isEnable ? 1 : 9;
//                new Thread(new Runnable() {
//                    public void run() {
//                        QuickSettingsSystemConfigFragment.this.mTelephonyManager.setPreferredNetworkType(subId, networkType);
//                        Global.putInt(QuickSettingsSystemConfigFragment.this.mContext.getContentResolver(), "preferred_network_mode" + subId, networkType);
//                        QuickSettingsSystemConfigFragment.this.setNetworkTypeDone = true;
//                    }
//                }).start();
//            }
//            dialog.dismiss();
//        }
//    };
    private FrameLayout frame;
    private Handler handle = new Handler();
    /* access modifiers changed from: private */
    public ImageView high;
    private boolean isFindMeWorking = true;
    /* access modifiers changed from: private */
    public ImageView low;
    private AnimationDrawable mAnimBTDrawable;
    private AnimationDrawable mAnimationDrawable;
    private ImageView mBTState;
    /* access modifiers changed from: private */
    public BluetoothAdapter mBTadapter = null;
    /* access modifiers changed from: private */
    public Context mContext;
    private Handler mFindMeHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.d("QuickSettingsSystemConfigFragment", "mFindMeHandler handleMessage, msg.what = " + msg.what);
            switch (msg.what) {
                case 1:
                    QuickSettingsSystemConfigFragment.this.updateUiViews();
                    return;
                case 2:
                    QuickSettingsSystemConfigFragment.this.updateImage();
                    return;
                default:
                    return;
            }
        }
    };
    private IntentFilter mIntentFilter;
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            QuickSettingsSystemConfigFragment.this.setMobiledataInfo();
        }
    };
    /* access modifiers changed from: private */
    public boolean mIsWifiConnected = false;
    private NotificationManager mNoMan;

    public void setMobileDataState(boolean mobileDataEnabled)
    {
        try
        {
            TelephonyManager telephonyService = (TelephonyManager) mContext.getSystemService(TELEPHONY_SERVICE);

            Method setMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("setDataEnabled", boolean.class);

            if (null != setMobileDataEnabledMethod)
            {
                setMobileDataEnabledMethod.invoke(telephonyService, mobileDataEnabled);
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error setting mobile data state", ex);
        }
    }

    public boolean getMobileDataState()
    {
        try
        {
            TelephonyManager telephonyService = (TelephonyManager) mContext.getSystemService(TELEPHONY_SERVICE);

            Method getMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("getDataEnabled");

            if (null != getMobileDataEnabledMethod)
            {
                boolean mobileDataEnabled = (Boolean) getMobileDataEnabledMethod.invoke(telephonyService);

                return mobileDataEnabled;
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error getting mobile data state", ex);
        }

        return false;
    }
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            String toast_info;
            String toast_info2;
            boolean z = true;
            boolean z2 = false;
            switch (v.getId()) {
                case R.id.system_screenon_guesture /*2131624098*/:
                    if (QuickSettingsSystemConfigFragment.this.getSystemRaiseWakeUp()) {
                        QuickSettingsSystemConfigFragment.this.m_system_screenon_guesture.setImageResource(R.drawable.smart_watch_screenon_guesture_off);
                        toast_info2 = QuickSettingsSystemConfigFragment.this.mContext.getResources().getString(R.string.screenon_guesture_off).toString();
                        Intent intent = new Intent();
                        intent.setAction("action.RAISE_WAKEUP_ENABLE");
                        intent.putExtra("raise_wakeup_enable", false);
                        QuickSettingsSystemConfigFragment.this.getActivity().sendBroadcast(intent);
                        Log.d("QuickSettingsSystemConfigFragment", "stop ScreenSensorService");
                    } else {
                        QuickSettingsSystemConfigFragment.this.m_system_screenon_guesture.setImageResource(R.drawable.smart_watch_screenon_guesture_on);
                        toast_info2 = QuickSettingsSystemConfigFragment.this.mContext.getResources().getString(R.string.screenon_guesture_on).toString();
                        Intent intent2 = new Intent();
                        intent2.setAction("action.RAISE_WAKEUP_ENABLE");
                        intent2.putExtra("raise_wakeup_enable", true);
                        QuickSettingsSystemConfigFragment.this.getActivity().sendBroadcast(intent2);
                        Log.d("QuickSettingsSystemConfigFragment", "start ScreenSensorService");
                    }
                    Toast.makeText(QuickSettingsSystemConfigFragment.this.mContext, toast_info2, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.notify_style /*2131624099*/:
                    boolean status = QuickSettingsSystemConfigFragment.this.getNotificationComingState();
                    QuickSettingsSystemConfigFragment quickSettingsSystemConfigFragment = QuickSettingsSystemConfigFragment.this;
                    if (status) {
                        z = false;
                    }
                    quickSettingsSystemConfigFragment.setNotificationComingState(z);
                    if (status) {
                        QuickSettingsSystemConfigFragment.this.m_notifyStyle.setImageResource(R.drawable.zenmode_on);
                        Toast.makeText(QuickSettingsSystemConfigFragment.this.mContext, QuickSettingsSystemConfigFragment.this.mContext.getResources().getString(R.string.send_info_text2).toString(), Toast.LENGTH_SHORT).show();
                        break;
                    } else {
                        QuickSettingsSystemConfigFragment.this.m_notifyStyle.setImageResource(R.drawable.zenmode_off);
                        Toast.makeText(QuickSettingsSystemConfigFragment.this.mContext, QuickSettingsSystemConfigFragment.this.mContext.getResources().getString(R.string.send_info_text1).toString(), Toast.LENGTH_SHORT).show();
                        break;
                    }
                case R.id.system_airplane_mode /*2131624102*/:
                    if (QuickSettingsSystemConfigFragment.this.isAirPlaneModeOn()) {
                        QuickSettingsSystemConfigFragment.this.setAirPlaneModeOn(false);
                        QuickSettingsSystemConfigFragment.this.m_system_airplane_mode.setImageResource(R.drawable.smart_watch_airmode_off);
                        toast_info = QuickSettingsSystemConfigFragment.this.mContext.getResources().getString(R.string.airplane_mode_off).toString();
                    } else {
                        QuickSettingsSystemConfigFragment.this.setAirPlaneModeOn(true);
                        QuickSettingsSystemConfigFragment.this.m_system_airplane_mode.setImageResource(R.drawable.smart_watch_airmode_on);
                        toast_info = QuickSettingsSystemConfigFragment.this.mContext.getResources().getString(R.string.airplane_mode_on).toString();
                    }
                    Toast.makeText(QuickSettingsSystemConfigFragment.this.mContext, toast_info, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.mobile_data_settings /*2131624103*/:
                    if (mTelephonyManager.hasIccCard()) {
                        if (!QuickSettingsSystemConfigFragment.this.isAirPlaneModeOn() && QuickSettingsSystemConfigFragment.this.mTelephonyManager.getCallState() == 0) {
                            int isEnable = QuickSettingsSystemConfigFragment.this.mTelephonyManager.getDataState();
                            TelephonyManager r6 = QuickSettingsSystemConfigFragment.this.mTelephonyManager;
                            if (isEnable!=0) {
                                z2 = true;
                            }
                            setMobileDataState(z2);
                            QuickSettingsSystemConfigFragment.this.myHandler.sendEmptyMessageDelayed(55, 2000);
                            break;
                        } else {
                            return;
                        }
                    } else {
                        Toast.makeText(QuickSettingsSystemConfigFragment.this.mContext, R.string.mobiledata_remind, Toast.LENGTH_SHORT).show();
                        return;
                    }

                case R.id.low /*2131624106*/:
                    QuickSettingsSystemConfigFragment.this.midd.setVisibility(View.VISIBLE);
                    QuickSettingsSystemConfigFragment.this.high.setVisibility(View.INVISIBLE);
                    QuickSettingsSystemConfigFragment.this.low.setVisibility(View.GONE);
                    QuickSettingsSystemConfigFragment.this.setBrightnessLevel(((QuickSettingsSystemConfigFragment.this.maxBrightnessLevel - QuickSettingsSystemConfigFragment.this.minBrightnessLevel) / 2) + QuickSettingsSystemConfigFragment.this.minBrightnessLevel);
                    break;
                case R.id.high /*2131624107*/:
                    QuickSettingsSystemConfigFragment.this.midd.setVisibility(View.GONE);
                    QuickSettingsSystemConfigFragment.this.high.setVisibility(View.GONE);
                    QuickSettingsSystemConfigFragment.this.low.setVisibility(View.VISIBLE);
                    QuickSettingsSystemConfigFragment.this.setBrightnessLevel(QuickSettingsSystemConfigFragment.this.minBrightnessLevel);
                    break;
                case R.id.midd /*2131624108*/:
                    QuickSettingsSystemConfigFragment.this.midd.setVisibility(View.GONE);
                    QuickSettingsSystemConfigFragment.this.high.setVisibility(View.VISIBLE);
                    QuickSettingsSystemConfigFragment.this.low.setVisibility(View.GONE);
                    QuickSettingsSystemConfigFragment.this.setBrightnessLevel(QuickSettingsSystemConfigFragment.this.maxBrightnessLevel);
                    break;
                case R.id.system_connected_device /*2131624109*/:
                    if (!QuickSettingsSystemConfigFragment.this.isGpsModeOn()) {
                        QuickSettingsSystemConfigFragment.this.setGpsModeOn(true);
                        QuickSettingsSystemConfigFragment.this.m_system_find_my_device.setImageResource(R.drawable.gps_on);
                        break;
                    } else {
                        QuickSettingsSystemConfigFragment.this.setGpsModeOn(false);
                        QuickSettingsSystemConfigFragment.this.m_system_find_my_device.setImageResource(R.drawable.gps_off);
                        break;
                    }
            }
        }
    };
    OnLongClickListener mOnLongClickListener = new OnLongClickListener() {
        public boolean onLongClick(View v) {
            QuickSettingsSystemConfigFragment.this.mContext.startActivity(new Intent("android.intent.action.SHOW_BRIGHTNESS_DIALOG"));
            return true;
        }
    };
    private PowerManager mPowerManager;
    final ContentObserver mSettingsObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean selfChange) {
            QuickSettingsSystemConfigFragment.this.setViewState();
        }
    };
    /* access modifiers changed from: private */
    public TelephonyManager mTelephonyManager;
    /* access modifiers changed from: private */
    public WifiManager mWifiManager;
    private ImageView mWifiState;
    private ImageView m_mobile_data_settings;
    /* access modifiers changed from: private */
    public ImageView m_notifyStyle;
    /* access modifiers changed from: private */
    public ImageView m_system_airplane_mode;
    /* access modifiers changed from: private */
    public ImageView m_system_find_my_device;
    /* access modifiers changed from: private */
    public ImageView m_system_screenon_guesture;
    /* access modifiers changed from: private */
    public int maxBrightnessLevel = 0;
    /* access modifiers changed from: private */
    public ImageView midd;
    /* access modifiers changed from: private */
    public int minBrightnessLevel = 0;
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 55:
                    QuickSettingsSystemConfigFragment.this.setMobiledataInfo();
                    return;
                default:
                    return;
            }
        }
    };
    private Runnable runnable;
    boolean setNetworkTypeDone = true;
    private BroadcastReceiver wifiIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.net.wifi.WIFI_STATE_CHANGED")) {
                switch (intent.getIntExtra("wifi_state", 0)) {
                    case 0:
                        QuickSettingsSystemConfigFragment.this.wifiAnimaStart();
                        return;
                    case 1:
                        QuickSettingsSystemConfigFragment.this.mIsWifiConnected = false;
                        QuickSettingsSystemConfigFragment.this.wifiDisConnectedShow(context);
                        return;
                    case 2:
                        QuickSettingsSystemConfigFragment.this.wifiAnimaStart();
                        return;
                    case 3:
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                QuickSettingsSystemConfigFragment.this.wifiConnectedShow(QuickSettingsSystemConfigFragment.this.mContext);
                            }
                        }, 500);
                        return;
                    default:
                        return;
                }
            } else if (action.equals("android.net.wifi.supplicant.CONNECTION_CHANGE")) {
                if (WifiInfo.getDetailedStateOf((SupplicantState) intent.getParcelableExtra("connected")) == DetailedState.CONNECTED) {
                }
            } else if (action.equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                QuickSettingsSystemConfigFragment.this.updateBTStat();
            } else if (!action.equals("android.intent.action.AIRPLANE_MODE")) {
                if (action.equals("android.intent.action.LOCALE_CHANGED")) {
                }
            } else if (QuickSettingsSystemConfigFragment.this.isAirPlaneModeOn()) {
                QuickSettingsSystemConfigFragment.this.m_system_airplane_mode.setImageResource(R.drawable.smart_watch_airmode_on);
            } else {
                QuickSettingsSystemConfigFragment.this.m_system_airplane_mode.setImageResource(R.drawable.smart_watch_airmode_off);
            }
        }
    };

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.mContext = getActivity();
        this.mPowerManager = (PowerManager) this.mContext.getSystemService(Context.POWER_SERVICE);
        getMaxAndMinBrightnessLevel();
        //this.mTelephonyManager = TelephonyManager.from(this.mContext);
        //this.SimState = TelephonyManager.getDefault().hasIccCard();
        this.mIntentFilter = new IntentFilter();
        this.mIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.mIntentFilter.addAction("android.intent.action.ANY_DATA_STATE");
        IntentFilter wifiIntentFilter = new IntentFilter();
        wifiIntentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        wifiIntentFilter.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE");
        wifiIntentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        wifiIntentFilter.addAction("android.intent.action.AIRPLANE_MODE");
        wifiIntentFilter.addAction("android.intent.action.LOCALE_CHANGED");
        this.mContext.registerReceiver(this.wifiIntentReceiver, wifiIntentFilter);
        this.mWifiManager = (WifiManager) this.mContext.getSystemService(Context.WIFI_SERVICE);
        this.mBTadapter = BluetoothAdapter.getDefaultAdapter();
        this.mNoMan = (NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quicksetting_system_config, container, false);
        this.m_notifyStyle = (ImageView) view.findViewById(R.id.notify_style);
        this.m_system_screenon_guesture = (ImageView) view.findViewById(R.id.system_screenon_guesture);
        this.m_system_airplane_mode = (ImageView) view.findViewById(R.id.system_airplane_mode);
        this.m_system_find_my_device = (ImageView) view.findViewById(R.id.system_connected_device);
        this.m_mobile_data_settings = (ImageView) view.findViewById(R.id.mobile_data_settings);
        this.frame = (FrameLayout) view.findViewById(R.id.frame);
        this.low = (ImageView) view.findViewById(R.id.low);
        this.midd = (ImageView) view.findViewById(R.id.midd);
        this.high = (ImageView) view.findViewById(R.id.high);
        this.mBTState = (ImageView) view.findViewById(R.id.bt_stat);
        updateBTStat();
        this.mBTState.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (QuickSettingsSystemConfigFragment.this.mBTadapter.getState() == BluetoothAdapter.STATE_OFF) {
                    QuickSettingsSystemConfigFragment.this.mBTadapter.enable();
                } else if (QuickSettingsSystemConfigFragment.this.mBTadapter.getState() == BluetoothAdapter.STATE_ON) {
                    QuickSettingsSystemConfigFragment.this.mBTadapter.disable();
                }
            }
        });
        this.mBTState.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                QuickSettingsSystemConfigFragment.this.startBTSettings();
                return true;
            }
        });
        this.mWifiState = (ImageView) view.findViewById(R.id.wifi_status);
        if (this.mWifiManager.isWifiEnabled()) {
            wifiConnectedShow(this.mContext);
        } else {
            this.mWifiState.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.wifi_off));
        }
        this.mWifiState.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (QuickSettingsSystemConfigFragment.this.mWifiManager.isWifiEnabled()) {
                    QuickSettingsSystemConfigFragment.this.mWifiManager.setWifiEnabled(false);
                } else {
                    QuickSettingsSystemConfigFragment.this.mWifiManager.setWifiEnabled(true);
                }
            }
        });
        this.mWifiState.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                QuickSettingsSystemConfigFragment.this.startWifiSettings();
                return true;
            }
        });
        this.m_system_find_my_device.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                QuickSettingsSystemConfigFragment.this.startGPSsettings();
                return true;
            }
        });
        this.mContext.getContentResolver().registerContentObserver(Global.getUriFor("zen_mode"), false, this.mSettingsObserver);
        this.mContext.getContentResolver().registerContentObserver(System.getUriFor("persist.sys.raise.wakeup"), false, this.mSettingsObserver);
        this.mContext.getContentResolver().registerContentObserver(System.getUriFor("step_target_count"), false, this.mSettingsObserver);
        return view;
    }

    private void initViews() {
        setViewState();
        setViewsOnClickListener();
        setBrightnessLevelBg();
    }

    /* access modifiers changed from: private */
    public void setViewState() {
        if (getNotificationComingState()) {
            this.m_notifyStyle.setImageResource(R.drawable.zenmode_off);
        } else {
            this.m_notifyStyle.setImageResource(R.drawable.zenmode_on);
        }
        if (getSystemRaiseWakeUp()) {
            this.m_system_screenon_guesture.setImageResource(R.drawable.smart_watch_screenon_guesture_on);
        } else {
            this.m_system_screenon_guesture.setImageResource(R.drawable.smart_watch_screenon_guesture_off);
        }
        if (isAirPlaneModeOn()) {
            this.m_system_airplane_mode.setImageResource(R.drawable.smart_watch_airmode_on);
        } else {
            this.m_system_airplane_mode.setImageResource(R.drawable.smart_watch_airmode_off);
        }
        setMobiledataInfo();
        this.mContext.registerReceiver(this.mIntentReceiver, this.mIntentFilter);
        if (isGpsModeOn()) {
            this.m_system_find_my_device.setImageResource(R.drawable.gps_on);
        } else {
            this.m_system_find_my_device.setImageResource(R.drawable.gps_off);
        }
    }

    public void onResume() {
        super.onResume();
        initViews();
        if (this.mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
            wifiDisConnectedShow(this.mContext);
        } else {
            wifiConnectedShow(this.mContext);
        }
    }

    public void onPause() {
        super.onPause();
        this.mContext.unregisterReceiver(this.mIntentReceiver);
    }

    private void setViewsOnClickListener() {
        this.m_notifyStyle.setOnClickListener(this.mOnClickListener);
        this.m_system_screenon_guesture.setOnClickListener(this.mOnClickListener);
        this.m_system_airplane_mode.setOnClickListener(this.mOnClickListener);
        this.m_system_find_my_device.setOnClickListener(this.mOnClickListener);
        this.m_mobile_data_settings.setOnClickListener(this.mOnClickListener);
        this.high.setOnClickListener(this.mOnClickListener);
        this.low.setOnClickListener(this.mOnClickListener);
        this.midd.setOnClickListener(this.mOnClickListener);
        this.high.setOnLongClickListener(this.mOnLongClickListener);
        this.low.setOnLongClickListener(this.mOnLongClickListener);
        this.midd.setOnLongClickListener(this.mOnLongClickListener);
    }

    /* access modifiers changed from: private */
    public void setMobiledataInfo() {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(TELEPHONY_SERVICE);
        if (getMobileDataState() && !isAirPlaneModeOn() && Objects.requireNonNull(tm).hasIccCard()) {
            this.m_mobile_data_settings.setImageResource(R.drawable.smart_watch_mobile_data_on);
        } else {
            this.m_mobile_data_settings.setImageResource(R.drawable.smart_watch_mobile_data_off);
        }
    }

    private void setBrightnessLevelBg() {
        if (getCurrBrightnessLevel() == 0) {
            this.midd.setVisibility(View.GONE);
            this.high.setVisibility(View.GONE);
            this.low.setVisibility(View.VISIBLE);
        } else if (getCurrBrightnessLevel() == 1) {
            this.midd.setVisibility(View.VISIBLE);
            this.high.setVisibility(View.GONE);
            this.low.setVisibility(View.GONE);
        } else if (getCurrBrightnessLevel() == 2) {
            this.midd.setVisibility(View.GONE);
            this.high.setVisibility(View.VISIBLE);
            this.low.setVisibility(View.GONE);
        }
    }

    /* access modifiers changed from: private */
    public boolean getNotificationComingState() {
        return  false;
    }

    /* access modifiers changed from: private */
    public void setNotificationComingState(boolean b) {
        //this.mNoMan.setZenMode(b ? 0 : 3, null, "F");
    }

    /* access modifiers changed from: private */
    public boolean getSystemRaiseWakeUp() {
        return SystemProperties.getBoolean("persist.sys.raise.wakeup", false);
    }

    /* access modifiers changed from: private */
    public boolean isAirPlaneModeOn() {
        if (System.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) != 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean isGpsModeOn() {
        if (Secure.getInt(this.mContext.getContentResolver(), "location_mode", 0) != 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void setGpsModeOn(boolean on) {
        Secure.putInt(this.mContext.getContentResolver(), "location_mode", on ? 1 : 0);
    }

    /* access modifiers changed from: private */
    public void setAirPlaneModeOn(boolean on) {
        Global.putInt(this.mContext.getContentResolver(), "airplane_mode_on", on ? 1 : 0);
        Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
        intent.putExtra("state", on);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.getUserHandleForUid(getId()));
        Log.d("QuickSettingsSystemConfigFragment", "setAirPlaneModeOff()");
    }

    private int getCurrBrightnessLevel() {
        int value = 30;
        try {
            value = System.getInt(this.mContext.getContentResolver(), "screen_brightness");
        } catch (SettingNotFoundException e) {
        }
        int median = (this.maxBrightnessLevel - this.minBrightnessLevel) / 3;
        if (value <= this.minBrightnessLevel + median) {
            return 0;
        }
        if (value > this.minBrightnessLevel + median && value < this.minBrightnessLevel + (median * 2)) {
            return 1;
        }
        if (value >= this.minBrightnessLevel + (median * 2)) {
            return 2;
        }
        return 0;
    }

    private void getMaxAndMinBrightnessLevel() {
        //this.maxBrightnessLevel = this.mPowerManager.getMaximumScreenBrightnessSetting();
        //this.minBrightnessLevel = this.mPowerManager.getMinimumScreenBrightnessSetting();
    }

    /* access modifiers changed from: private */
    public void setBrightnessLevel(int level) {
        //this.mPowerManager.setBacklightBrightness(level);
        System.putInt(this.mContext.getContentResolver(), "screen_brightness", level);
    }

    /* access modifiers changed from: private */
    public void updateImage() {
    }

    /* access modifiers changed from: private */
    public void updateUiViews() {
    }

    /* access modifiers changed from: private */
    public void wifiConnectedShow(Context context) {
        int level = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getRssi();
        wifiAnimaStop();
        if (level > -55) {
            this.mWifiState.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.wifi_4));
        } else if (level > -70) {
            this.mWifiState.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.wifi_3));
        } else if (level > -85) {
            this.mWifiState.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.wifi_2));
        } else if (level > -100) {
            this.mWifiState.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.wifi_1));
        } else {
            this.mWifiState.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.wifi_4));
        }
    }

    /* access modifiers changed from: private */
    public void wifiDisConnectedShow(Context context) {
        this.mIsWifiConnected = false;
        wifiAnimaStop();
        this.mWifiState.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.wifi_off));
    }

    /* access modifiers changed from: private */
    public void startWifiSettings() {
        this.mContext.startActivity(new Intent("android.settings.WIFI_SETTINGS"));
    }

    /* access modifiers changed from: private */
    public void startBTSettings() {
        this.mContext.startActivity(new Intent("android.settings.BLUETOOTH_SETTINGS"));
    }

    /* access modifiers changed from: private */
    public void startGPSsettings() {
        this.mContext.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
    }

    /* access modifiers changed from: private */
    public void wifiAnimaStart() {
        this.mWifiState.setImageResource(R.drawable.anima_wifi);
        this.mAnimationDrawable = (AnimationDrawable) this.mWifiState.getDrawable();
        this.mAnimationDrawable.start();
    }

    private void wifiAnimaStop() {
        if (this.mAnimationDrawable != null) {
            this.mAnimationDrawable.stop();
        }
    }

    private void BTAnimaStart() {
        this.mBTState.setImageResource(R.drawable.anima_bt);
        this.mAnimBTDrawable = (AnimationDrawable) this.mBTState.getDrawable();
        this.mAnimBTDrawable.start();
    }

    private void BTAnimaStop() {
        if (this.mAnimBTDrawable != null) {
            this.mAnimBTDrawable.stop();
        }
    }

    /* access modifiers changed from: private */
    public void updateBTStat() {
        int blueState = this.mBTadapter.getState();
        if (blueState == 12) {
            BTAnimaStop();
            this.mBTState.setImageResource(R.drawable.bt_on);
        } else if (blueState == 10) {
            BTAnimaStop();
            this.mBTState.setImageResource(R.drawable.bt_off);
        } else if (blueState == 13 || blueState == 11) {
            BTAnimaStart();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        this.mContext.unregisterReceiver(this.wifiIntentReceiver);
        this.handle.removeCallbacks(this.runnable);
    }
}

