package com.bid.launcherwatch;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;

import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.service.wallpaper.IWallpaperConnection;
import android.service.wallpaper.IWallpaperEngine;
import android.service.wallpaper.IWallpaperService;
import android.service.wallpaper.WallpaperService;

import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

public class MyWallpaperService extends WallpaperService {
    Context mContext;
    Intent mIntent;
    View mClockHost;
    String packageName="";
    String className="";
    ServiceConnection sc;
    private WallpaperConnection mWallpaperConnection;

    public MyWallpaperService(Context context, Intent intent, View clockHost, ServiceConnection sc) {
        this.mContext = context;
        this.mIntent = intent;
        this.mClockHost = clockHost;
        this.sc=sc;
    }
    public MyWallpaperService(){

    }

    @Override
    public Engine onCreateEngine() {

        Log.e("MyWallpaperService:","Engine creating");
        return new MyWallpaperServiceEngine();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("MyWallpaperService:","Started");

        IntentFilter intentFilter = new IntentFilter("broadcast.to.service.class");
        MyBroadcastReceiver mReceiver = new MyBroadcastReceiver(MyWallpaperService.this);
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mReceiver, intentFilter);
        Log.e("MyWallpaperService:","Registered");
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
        //this.stopService(mIntent);
    }
    public void receiveCommand(String packageName,String service) {
        //Log.d("got", String.valueOf(i));
        //Log.e("RRRRRRRRRRRRRRRRRRRRRRRR:",packageName +"  "+service);

        Intent mWallpaperIntent=new Intent("android.service.wallpaper.WallpaperService");
        mWallpaperIntent.setComponent(new ComponentName(packageName, service));


        String requiredPermission = "android.permission.BIND_WALLPAPER";
        int checkVal = MyWallpaperService.this.checkCallingOrSelfPermission(requiredPermission);
        if (checkVal== PackageManager.PERMISSION_GRANTED)
        {
            Log.e("PERMISSION:","GRANTED");
        }
        else
            Log.e("PERMISSION:","NOT GRANTED");




        mWallpaperConnection = new WallpaperConnection(mWallpaperIntent, getApplicationContext());
                if (!mWallpaperConnection.connect()) {
                    mWallpaperConnection = null;
                }
//                //context.bindService(mWallpaperIntent,mWallpaperConnection,Context.BIND_AUTO_CREATE);
//                Log.e("wall", "connect");
        //getApplicationContext().startService(mWallpaperIntent);

    }


    //Service Connection class
    class WallpaperConnection extends IWallpaperConnection.Stub implements ServiceConnection {
        View mClockHost;
        boolean mConnected;
        Context mContext;
        IWallpaperEngine mEngine;
        Intent mIntent; //declare final
        IWallpaperService mService;
        MyWallpaperService myWallpaperService;
        WallpaperConnection(Intent intent, Context context) {
            this.mContext = MyWallpaperService.this;
            this.mIntent = intent;
            //this.mClockHost = clockHost;
        }

        private boolean connect() {
            synchronized (this) {
                try
                {
                    if (!this.mContext.bindService(this.mIntent, this, Context.BIND_AUTO_CREATE)) {
                        return false;
                    }
                    this.mConnected = true;
                    Log.d("xiaocai_clockFragment", "WallpaperConnection connect");
                    return true;
                }catch (Exception e)
                {
                    Log.e("Unable to get android.permission.BIND_WALLPAPER:",e.getMessage());
                    return  false;
                }

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

    public class MyBroadcastReceiver extends BroadcastReceiver {
        private final MyWallpaperService _myWallpaper;

        public MyBroadcastReceiver(MyWallpaperService myWallpaper) {
            _myWallpaper = myWallpaper;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            _myWallpaper.receiveCommand(intent.getStringExtra("package"),intent.getStringExtra("service"));
            //Log.e("RRRRRRRRRRRRRRRRRRRRRRRR:",intent.getStringExtra("package")+intent.getStringExtra("service"));
        }
    }


}
