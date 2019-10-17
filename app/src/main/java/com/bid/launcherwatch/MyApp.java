package com.bid.launcherwatch;

import android.app.Application;
import android.content.Context;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class MyApp extends Application {
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().setCustomCrashHanler(getApplicationContext());
        initImageLoader(getApplicationContext());
    }

    public static void initImageLoader(Context context) {
        Builder config = new Builder(context);
        config.threadPriority(3);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(52428800);
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        ImageLoader.getInstance().init(config.build());
    }
}
