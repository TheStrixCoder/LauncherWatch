package com.bid.launcherwatch;


import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.service.wallpaper.WallpaperService;
import android.support.wearable.watchface.WatchFaceService;
import android.view.SurfaceHolder;
import android.view.View;

public class MyWallpaperService extends WallpaperService {
    Context mContext;
    Intent mIntent;
    View mClockHost;
    String packageName="";
    String className="";
    ServiceConnection sc;

    public MyWallpaperService(Context context, Intent intent, View clockHost, ServiceConnection sc) {
        this.mContext = context;
        this.mIntent = intent;
        this.mClockHost = clockHost;
        this.sc=sc;
    }
    public MyWallpaperService(){}

    @Override
    public Engine onCreateEngine() {

        return new MyWallpaperServiceEngine();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //this.packageName=intent.getStringExtra("package");
        //this.className=intent.getStringExtra("service");

        return super.onStartCommand(intent, flags, startId);
    }

    private class MyWallpaperServiceEngine extends WallpaperService.Engine {
        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.stopService(mIntent);
    }
}
