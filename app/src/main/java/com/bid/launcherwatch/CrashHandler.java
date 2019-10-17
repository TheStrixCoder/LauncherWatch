package com.bid.launcherwatch;

import android.content.Context;
import android.os.Process;
import android.util.Log;

import java.lang.Thread.UncaughtExceptionHandler;

public class CrashHandler implements UncaughtExceptionHandler {
    private static CrashHandler instance = new CrashHandler();
    private Context mContext;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return instance;
    }

    public void setCustomCrashHanler(Context context) {
        this.mContext = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e("xiaocai_uncaughtException", "" + ex);
        Util.exic();
        Process.killProcess(Process.myPid());
        System.exit(0);
    }
}
