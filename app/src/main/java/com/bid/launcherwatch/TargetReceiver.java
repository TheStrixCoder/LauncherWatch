package com.bid.launcherwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings.System;
import android.util.Log;

public class TargetReceiver extends BroadcastReceiver {
    TargetDialog alarmDialog;

    public void onReceive(Context context, Intent intent) {
        Log.d("TargetReceiver", "onReceive");
        int complete = System.getInt(context.getContentResolver(), "level_complete", 0);
        if (this.alarmDialog == null) {
            this.alarmDialog = new TargetDialog(context);
        }
        int level = intent.getIntExtra("target_level", 0);
        Log.d("TargetReceiver", "level-" + level + " -complete-" + complete);
        if (this.alarmDialog != null && !this.alarmDialog.isShowing() && complete == 0 && level >= 100) {
            this.alarmDialog.show();
            System.putInt(context.getContentResolver(), "level_complete", 1);
        }
    }
}
