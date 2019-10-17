package com.bid.launcherwatch;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.bid.launcherwatch.AppListCustomUtil.AppInfo;
import java.util.ArrayList;

public class AppMatrixFragment extends Fragment implements OnTouchListener {
    private static AppMatrixFragment INSTANCE;
    /* access modifiers changed from: private */
    public CircleScoller circlescoller;
    private AppsAdapter mAppAdapter;
    public GridView mAppListView;
    /* access modifiers changed from: private */
    public ArrayList<AppInfo> mApps;
    private OnItemClickListener mListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            AppInfo info = (AppInfo) AppMatrixFragment.this.mApps.get(position);
            ComponentName componet = new ComponentName(info.pkg, info.cls);
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.setComponent(componet);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            AppMatrixFragment.this.getActivity().startActivity(intent);
        }
    };
    private OnScrollListener mOnScrollListener = new OnScrollListener() {
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == 0) {
                AppMatrixFragment.this.circlescoller.setVisibility(View.GONE);
                AppMatrixFragment.this.circlescoller.invalidate();
            } else if (scrollState == 1) {
                AppMatrixFragment.this.circlescoller.setVisibility(View.VISIBLE);
                AppMatrixFragment.this.circlescoller.invalidate();
            } else if (scrollState == 2) {
                AppMatrixFragment.this.circlescoller.setVisibility(View.VISIBLE);
                AppMatrixFragment.this.circlescoller.invalidate();
            }
        }

        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            Log.i("TAG", "totalItemCount" + totalItemCount + " " + visibleItemCount);
            AppMatrixFragment.this.circlescoller.setHighLightPercent(firstVisibleItem, visibleItemCount, totalItemCount);
            AppMatrixFragment.this.circlescoller.invalidate();
        }
    };
    private int touchY;

    private class AppsAdapter extends BaseAdapter {
        public AppsAdapter() {
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = AppMatrixFragment.this.getActivity().getLayoutInflater().inflate(R.layout.gridview_item, parent, false);
            }
            AppInfo info = (AppInfo) AppMatrixFragment.this.mApps.get(position);
            TextView title = (TextView) convertView.findViewById(R.id.item_text);
            ((ImageView) convertView.findViewById(R.id.item_image)).setImageDrawable(info.icon);
            title.setText(info.title);
            return convertView;
        }

        public final int getCount() {
            return AppMatrixFragment.this.mApps.size();
        }

        public final Object getItem(int position) {
            return AppMatrixFragment.this.mApps.get(position);
        }

        public final long getItemId(int position) {
            return (long) position;
        }
    }

    public AppMatrixFragment() {
        Log.d("AppMatrixFragment", "AppMatrixFragment.this:" + this + ",INSTANCE:" + INSTANCE, new Throwable("AppMatrixFragment"));
        INSTANCE = this;
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        WatchApp.setMainMenuStatus(isVisibleToUser);
        WatchApp.setIsCanSlide(false);
        SystemProperties.set("persist.sys.clock.idle", String.valueOf(false));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AppMatrixFragment", "onCreateView.this:" + this);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.applist_matrix, container, false);
        int w = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        this.mAppListView = (GridView) rootView.findViewById(R.id.app_matrix_view);
        this.circlescoller = (CircleScoller) rootView.findViewById(R.id.circlescoller);
        this.circlescoller.setScreenSize(w, w);
        loadApps();
        this.mAppAdapter = new AppsAdapter();
        this.mAppListView.setAdapter(this.mAppAdapter);
        this.mAppListView.setOnItemClickListener(this.mListener);
        this.mAppListView.setOnScrollListener(this.mOnScrollListener);
        return rootView;
    }

    public void refreshApplist() {
        if (this.mAppListView != null) {
            loadApps();
            this.mAppAdapter.notifyDataSetChanged();
            this.mAppListView.invalidate();
        }
    }

    public void onDestroy() {
        Log.d("AppMatrixFragment", "onDestroy, this: " + this);
        this.mAppListView = null;
        INSTANCE = null;
        super.onDestroy();
    }

    private void loadApps() {
        this.mApps = AppListCustomUtil.getAppList(getActivity());
    }

    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case 0:
                this.touchY = y;
                break;
            case 1:
                this.touchY = 0;
                break;
        }
        return true;
    }
}
