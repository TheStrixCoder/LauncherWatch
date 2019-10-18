package com.bid.launcherwatch;

import android.os.Bundle;
import android.os.SystemProperties;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.support.wearable.myView.WearableListView;

public class AppListFragment extends Fragment {
    private static AppListFragment INSTANCE;
    public WearableListView mAppListView;
    public AppListUtil mApplistUtil;

    public AppListFragment() {
        Log.d("AppListFragment", "AppListFragment.this:" + this + ",INSTANCE:" + INSTANCE, new Throwable("AppListFragment"));
        INSTANCE = this;
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        WatchApp.setMainMenuStatus(isVisibleToUser);
        WatchApp.setIsCanSlide(false);
        SystemProperties.set("persist.sys.clock.idle", String.valueOf(false));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AppListFragment", "onCreateView.this:" + this);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.applist, container, false);
        this.mApplistUtil = new AppListUtil(rootView.getContext());
        this.mAppListView = (WearableListView) rootView.findViewById(R.id.app_list_view);
        this.mAppListView.setAdapter(this.mApplistUtil.getAdapter());
        this.mAppListView.setOnClickListener((View.OnClickListener) this.mApplistUtil);
        return rootView;
    }

    public void refreshApplist() {
        Log.d("AppListFragment", "refreshApplist.mApplistUtil:" + this.mApplistUtil + ",mAppListView:" + this.mAppListView + ", this: " + this);
        if (this.mApplistUtil != null && this.mAppListView != null) {
            this.mApplistUtil.getApplist();
            this.mApplistUtil.getAdapter().notifyDataSetChanged();
            this.mAppListView.invalidate();
        }
    }

    public void onDestroy() {
        Log.d("AppListFragment", "onDestroy, this: " + this);
        this.mAppListView = null;
        this.mApplistUtil = null;
        INSTANCE = null;
        super.onDestroy();
    }
}
