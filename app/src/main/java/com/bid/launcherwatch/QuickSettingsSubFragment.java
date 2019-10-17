package com.bid.launcherwatch;


import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.support.v4.app.Fragment;
import android.support.wearable.R.styleable;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class QuickSettingsSubFragment extends Fragment implements OnClickListener {
    private final int SIGNAL_NO_SERVICE = 99;
    private final int SIGNAL_STRENGTH_GOOD = 3;
    private final int SIGNAL_STRENGTH_GREAT = 4;
    private final int SIGNAL_STRENGTH_MODERATE = 2;
    private final int SIGNAL_STRENGTH_NONE_OR_UNKNOWN = 0;
    private final int SIGNAL_STRENGTH_POOR = 1;
    private final int STATE_IN_SERVICE = 0;
    private final int STATE_OUT_OF_SERVICE = 1;
    private final String TAG = "QuickSettingsSubFragment";
    /* access modifiers changed from: private */
    public boolean isAirPlaneMode = false;
    /* access modifiers changed from: private */
    public boolean isCharging;
    /* access modifiers changed from: private */
    public ImageView mAirplaneMode;
    /* access modifiers changed from: private */
    public int mAsu = -999;
    /* access modifiers changed from: private */
    public ImageView mBatteryDrawable;
    private Context mContext;
    /* access modifiers changed from: private */
    public int mDbm = -999;
    /* access modifiers changed from: private */
    public int mNetWorkType;
    private ImageView mNetworkState;
    private TextView mOprator;
    private ImageView mPhoneConnectState;
    private ImageView mSIMState;
    /* access modifiers changed from: private */
    public int mServiceState = 1;
    private TelephonyManager mTelephonyManager;
    private Myreciever myreciever = new Myreciever();
    /* access modifiers changed from: private */
    public int status;
    private PhoneStateListener watchPhoneStateListener = new PhoneStateListener() {
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            QuickSettingsSubFragment.this.mAsu = signalStrength.getGsmSignalStrength();
            QuickSettingsSubFragment.this.mDbm = signalStrength.getCdmaDbm();
            QuickSettingsSubFragment.this.updateSim();
        }

        public void onDataConnectionStateChanged(int state, int networkType) {
            QuickSettingsSubFragment.this.mNetWorkType = networkType;
            switch (state) {
            }
            QuickSettingsSubFragment.this.updateSim();
        }

        public void onServiceStateChanged(ServiceState state) {
            if (state != null) {
                QuickSettingsSubFragment.this.mServiceState = 0;
            } else {
                QuickSettingsSubFragment.this.mServiceState = 1;
            }
            QuickSettingsSubFragment.this.showSimOprater();
            QuickSettingsSubFragment.this.updateSim();
            super.onServiceStateChanged(state);
        }
    };

    public class Myreciever extends BroadcastReceiver {
        public Myreciever() {
        }

        public void onReceive(Context context, Intent intent) {
            boolean z = true;
            String action = intent.getAction();
            if (action.equals("android.intent.action.BATTERY_CHANGED")) {
                QuickSettingsSubFragment.this.status = intent.getIntExtra("status", -1);
                QuickSettingsSubFragment quickSettingsSubFragment = QuickSettingsSubFragment.this;
                if (!(QuickSettingsSubFragment.this.status == 2 || QuickSettingsSubFragment.this.status == 5)) {
                    z = false;
                }
                quickSettingsSubFragment.isCharging = z;
                if (QuickSettingsSubFragment.this.isCharging) {
                    QuickSettingsSubFragment.this.mBatteryDrawable.setVisibility(View.VISIBLE);
                } else {
                    QuickSettingsSubFragment.this.mBatteryDrawable.setVisibility(View.INVISIBLE);
                }
            } else if (action.equals("android.intent.action.AIRPLANE_MODE")) {
                QuickSettingsSubFragment.this.isAirPlaneMode = intent.getBooleanExtra("state", false);
                QuickSettingsSubFragment.this.mServiceState = 1;
                QuickSettingsSubFragment.this.showSimOprater();
                QuickSettingsSubFragment.this.updateSim();
            } else if (!action.equals("android.intent.action.AIRPLANE_MODE")) {
            } else {
                if (QuickSettingsSubFragment.this.isAirPlaneModeOn()) {
                    QuickSettingsSubFragment.this.mAirplaneMode.setImageResource(R.drawable.smart_watch_airmode_on);
                } else {
                    QuickSettingsSubFragment.this.mAirplaneMode.setImageResource(R.drawable.smart_watch_airmode_off);
                }
            }
        }
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.mContext = getActivity();
        this.mTelephonyManager = (TelephonyManager) this.mContext.getSystemService(Context.TELEPHONY_SERVICE);
        this.mTelephonyManager.listen(this.watchPhoneStateListener, 321);
        this.mNetWorkType = this.mTelephonyManager.getNetworkType();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.BATTERY_CHANGED");
        filter.addAction("android.intent.action.AIRPLANE_MODE");
        this.mContext.registerReceiver(this.myreciever, filter);
        this.isAirPlaneMode = isAirPlaneModeOn();
    }

    public void onDestroy() {
        super.onDestroy();
        this.mContext.unregisterReceiver(this.myreciever);
    }

    public void onResume() {
        super.onResume();
        showSimOprater();
        setViewState();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quicksetting_subfragment_layout, container, false);
        this.mBatteryDrawable = (ImageView) view.findViewById(R.id.BatteryDrawable);
        this.mPhoneConnectState = (ImageView) view.findViewById(R.id.phone_connect);
        this.mAirplaneMode = (ImageView) view.findViewById(R.id.airplane_mode);
        this.mAirplaneMode.setOnClickListener(this);
        this.mPhoneConnectState.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.wiitetech.WiiWatchPro", "com.wiitetech.WiiWatchPro.ui.ServerActivity"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED|Intent.FLAG_ACTIVITY_NEW_TASK);
                    QuickSettingsSubFragment.this.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                }
                try {
                    Intent intent2 = new Intent();
                    intent2.setComponent(new ComponentName("com.wiitetech.WiiWatchProUart", "com.wiitetech.WiiWatchProUart.ui.ServerActivity"));
                    intent2.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED|Intent.FLAG_ACTIVITY_NEW_TASK);
                    QuickSettingsSubFragment.this.startActivity(intent2);
                } catch (ActivityNotFoundException e2) {
                }
            }
        });
        boolean state = System.getInt(this.mContext.getContentResolver(), "WiiWatchBluetoothState", 0) == 3;
        if (1 == System.getInt(this.mContext.getContentResolver(), "watch_app_phone_connect_state", 0)) {
            state = true;
        }
        setPhoneConnectedState(state);
        this.mSIMState = (ImageView) view.findViewById(R.id.sim_status);
        this.mNetworkState = (ImageView) view.findViewById(R.id.network_stat);
        this.mOprator = (TextView) view.findViewById(R.id.sim_oprator);
        this.mSIMState.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.signal_no));
        return view;
    }

    public void setPhoneConnectedState(boolean connected) {
        if (connected) {
            this.mPhoneConnectState.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.phone_connected));
        } else {
            this.mPhoneConnectState.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.phone_disconnect));
        }
    }

    private boolean is2G() {
        if (this.mNetWorkType == 1 || this.mNetWorkType == 2) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean isAirPlaneModeOn() {
        if (System.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) != 0) {
            return true;
        }
        return false;
    }

    public int mapSignalLevel(boolean is3G, int dBm, int asu) {
        int level = 0;
        switch (this.mNetWorkType) {
            case 1:
            case 2:
            case 4:
            case 7:
            case 11:
                if (asu > 2 && asu != 99) {
                    if (asu < 12) {
                        if (asu < 8) {
                            if (asu < 5) {
                                level = 1;
                                break;
                            } else {
                                level = 2;
                                break;
                            }
                        } else {
                            level = 3;
                            break;
                        }
                    } else {
                        level = 4;
                        break;
                    }
                } else {
                    level = 0;
                    break;
                }

            case 3:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 12:
            case 14:
            case 15:
                if (dBm > -111 && dBm != 85) {
                    if (dBm < -91) {
                        if (dBm < -98) {
                            if (dBm < -103) {
                                level = 1;
                                break;
                            } else {
                                level = 2;
                                break;
                            }
                        } else {
                            level = 3;
                            break;
                        }
                    } else {
                        level = 4;
                        break;
                    }
                } else {
                    level = 0;
                    break;
                }
            case 13:
                if (dBm > -131 && dBm != 85) {
                    if (dBm < -111) {
                        if (dBm < -118) {
                            if (dBm < -123) {
                                level = 1;
                                break;
                            } else {
                                level = 2;
                                break;
                            }
                        } else {
                            level = 3;
                            break;
                        }
                    } else {
                        level = 4;
                        break;
                    }
                } else {
                    level = 0;
                    break;
                }
        }
        if (this.mServiceState != 0) {
            return 99;
        }
        return level;
    }

    private int getNetWorkType() {
        int subId = SubscriptionManager.getDefaultSubscriptionId();
        int actualDataNetworkType = this.mTelephonyManager.getDataNetworkType();
        int actualVoiceNetworkType = this.mTelephonyManager.getVoiceNetworkType();
        if (actualDataNetworkType != 0) {
            return actualDataNetworkType;
        }
        if (actualVoiceNetworkType != 0) {
            return actualVoiceNetworkType;
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public void updateSim() {
        this.mNetWorkType = getNetWorkType();
        boolean is3G = !is2G();
        int level = mapSignalLevel(is3G, this.mDbm, this.mAsu);
        Log.v("QuickSettingsSubFragment", "updateSim::is3G =" + is3G);
        Log.v("QuickSettingsSubFragment", "updateSim::mDbm =" + this.mDbm);
        Log.v("QuickSettingsSubFragment", "updateSim::mAsu =" + this.mAsu);
        Log.v("QuickSettingsSubFragment", "updateSim::level =" + level);
        boolean hasSim = true;
        if (isSimInserd()) {
            if (!this.isAirPlaneMode) {
                if (this.mServiceState != 1) {
                    switch (level) {
                        case 0:
                            this.mSIMState.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.signal_0));
                            break;
                        case 1:
                            this.mSIMState.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.signal_1));
                            break;
                        case 2:
                            this.mSIMState.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.signal_2));
                            break;
                        case 3:
                            this.mSIMState.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.signal_3));
                            break;
                        case 4:
                            this.mSIMState.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.signal_4));
                            break;
                        case 99:
                            this.mSIMState.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.signal_no));
                            hasSim = false;
                            break;
                        default:
                            this.mSIMState.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.signal_no));
                            hasSim = false;
                            break;
                    }
                } else {
                    this.mSIMState.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.signal_search));
                }
            } else {
                this.mSIMState.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.signal_plane));
                this.mNetworkState.setImageDrawable(null);
                return;
            }
        } else {
            this.mSIMState.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.signal_no));
            hasSim = false;
        }
        if (hasSim) {
            switch (this.mNetWorkType) {
                case 1:
                case 2:
                case 4:
                case 7:
                case 11:
                    this.mNetworkState.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.network_2g));
                    break;
                case 3:
                case 5:
                case 6:
                case 8:
                case 9:
                case 10:
                case 12:
                case 14:
                case 15:
                    this.mNetworkState.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.network_3g));
                    break;
                case 13:
                    this.mNetworkState.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.network_4g));
                    break;
                default:
                    this.mNetworkState.setImageDrawable(null);
                    break;
            }
        } else {
            this.mNetworkState.setImageDrawable(null);
        }
    }

    /* access modifiers changed from: private */
    public void showSimOprater() {
        if (this.isAirPlaneMode) {
            this.mOprator.setText(" ");
            this.mOprator.setVisibility(View.GONE);
            return;
        }
        String oprator = this.mTelephonyManager.getNetworkOperatorName();
        if (oprator != null && !oprator.equals("")) {
            this.mOprator.setVisibility(View.VISIBLE);
            this.mOprator.setText(oprator);
        }
    }

    private boolean isSimInserd() {
        if (this.mTelephonyManager != null) {
            return this.mTelephonyManager.hasIccCard();
        }
        return false;
    }

    private void setViewState() {
        if (isAirPlaneModeOn()) {
            this.mAirplaneMode.setImageResource(R.drawable.smart_watch_airmode_on);
        } else {
            this.mAirplaneMode.setImageResource(R.drawable.smart_watch_airmode_off);
        }
    }

    public void onClick(View v) {
        String toast_info;
        String str = "";
        switch (v.getId()) {
            case R.id.airplane_mode /*2131624088*/:
                if (isAirPlaneModeOn()) {
                    setAirPlaneModeOn(false);
                    this.mAirplaneMode.setImageResource(R.drawable.smart_watch_airmode_off);
                    toast_info = this.mContext.getResources().getString(R.string.airplane_mode_off).toString();
                } else {
                    setAirPlaneModeOn(true);
                    this.mAirplaneMode.setImageResource(R.drawable.smart_watch_airmode_on);
                    toast_info = this.mContext.getResources().getString(R.string.airplane_mode_on).toString();
                }
                Toast.makeText(this.mContext, toast_info, Toast.LENGTH_SHORT).show();
                return;
            default:
                return;
        }
    }

    private void setAirPlaneModeOn(boolean on) {
        Global.putInt(this.mContext.getContentResolver(), "airplane_mode_on", on ? 1 : 0);
        Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
        intent.putExtra("state", on);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.getUserHandleForUid(getId()));
        Log.d("QuickSettingsSubFragment", "setAirPlaneModeOff()");
    }
}

