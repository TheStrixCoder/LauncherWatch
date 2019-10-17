package com.bid.launcherwatch;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemProperties;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearSnapHelper;
import android.support.wearable.view.CurvedChildLayoutManager;
import android.support.wearable.view.WearableRecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AppFitnessModeFragment extends Fragment {
    private static AppFitnessModeFragment INSTANCE;
    public WearableRecyclerView mAppListView;
    public AppFitnessListUtil mApplistUtil;

    public class MyLauncherChildLayoutManager extends CurvedChildLayoutManager {
        private float mProgressToCenter;

        public MyLauncherChildLayoutManager(Context context) {
            super(context);
        }

        public void updateChild(View child, WearableRecyclerView parent) {
            super.updateChild(child, parent);
            this.mProgressToCenter = Math.abs(0.5f - ((child.getY() / ((float) parent.getHeight())) + ((((float) child.getHeight()) / 2.0f) / ((float) parent.getHeight()))));
            this.mProgressToCenter = Math.min(this.mProgressToCenter, 0.5f);
            View iconImage = child.findViewById(R.id.icon_image);
            iconImage.setPivotX((this.mProgressToCenter + 1.0f) * 50.0f);
            iconImage.setScaleX(1.0f - (this.mProgressToCenter * 1.4f));
            iconImage.setScaleY(1.0f - (this.mProgressToCenter * 1.4f));
            iconImage.setAlpha(1.0f - (this.mProgressToCenter * 2.0f));
            View iconName = child.findViewById(R.id.icon_name);
            iconName.setPivotX(this.mProgressToCenter * -100.0f);
            iconName.setScaleX(1.0f - this.mProgressToCenter);
            iconName.setScaleY(1.0f - this.mProgressToCenter);
            iconName.setAlpha(1.0f - (this.mProgressToCenter * 2.0f));
            child.setTranslationX(-16.0f);
        }
    }

    public AppFitnessModeFragment() {
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fitness_list, container, false);
        this.mApplistUtil = new AppFitnessListUtil(rootView.getContext());
        this.mAppListView = (WearableRecyclerView) rootView.findViewById(R.id.recycler_launcher_view);
        this.mAppListView.setCenterEdgeItems(true);
        this.mAppListView.setLayoutManager(new MyLauncherChildLayoutManager(rootView.getContext()));
        this.mAppListView.setAdapter(this.mApplistUtil.getArcAdapter());
        new LinearSnapHelper().attachToRecyclerView(this.mAppListView);
        this.mAppListView.smoothScrollToPosition(1);
        return rootView;
    }

    public void refreshApplist() {
        if (this.mApplistUtil != null && this.mAppListView != null) {
            this.mApplistUtil.getApplist();
            this.mApplistUtil.getArcAdapter().notifyDataSetChanged();
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

