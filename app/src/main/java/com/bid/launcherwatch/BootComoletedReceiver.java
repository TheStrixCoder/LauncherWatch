package com.bid.launcherwatch;

import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings.System;
import android.util.Log;

public class BootComoletedReceiver extends BroadcastReceiver {
    private String TAG = BootComoletedReceiver.class.getSimpleName();
    private Context mContext;
    private Handler mHandler = new Handler();
    private PowerManager mPowerManager;

    public void onReceive(Context context, Intent intent) {
        Log.d(this.TAG, "onReceive  action = " + intent.getAction());
        this.mContext = context;
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            onBatterySaveIf();
        }
    }

    private void onBatterySaveIf() {
        boolean btySaver = true;
        Log.d("WatchApp", "onBatterySaveIf");
        this.mPowerManager = (PowerManager) this.mContext.getSystemService(Context.POWER_SERVICE);
        if (System.getInt(this.mContext.getContentResolver(), "battery_saver", -1) != 1) {
            btySaver = false;
        }
        if (btySaver) {
        }
    }
}
