package com.bid.launcherwatch;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.wearable.view.CircledImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.wearable.view.WearableListView;
import com.bid.launcherwatch.AppListCustomUtil.AppInfo;
import java.util.ArrayList;

public class AppListUtil implements WearableListView.OnClickListener {
    private AppListViewAdapter mAdaper;
    /* access modifiers changed from: private */
    public ArrayList<AppInfo> mApps;
    private AppArcListViewAdapter mArcAdaper;
    private Context mContext;
    private PackageManager mPackageManager = this.mContext.getPackageManager();

    @Override
    public void onClick(View view) {

    }

    class AppArcListViewAdapter extends Adapter {
        /* access modifiers changed from: private */
        public final Context mContext;
        private final LayoutInflater mInflater;

        class MyViewHolder extends ViewHolder {
            CircledImageView icon;
            TextView name;

            public MyViewHolder(View itemView) {
                super(itemView);
                this.icon = (CircledImageView) itemView.findViewById(R.id.icon_image);
                this.name = (TextView) itemView.findViewById(R.id.icon_name);
            }
        }

        public AppArcListViewAdapter(Context context) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(context);
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(this.mInflater.inflate(R.layout.row_advanced_item_layout, null));
        }

        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            MyViewHolder holder = (MyViewHolder) viewHolder;
            AppInfo info = (AppInfo) AppListUtil.this.mApps.get(position);
            holder.icon.setImageDrawable(info.icon);
            holder.name.setText(info.title);
            holder.itemView.setTag(Integer.valueOf(position));
            holder.itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    AppInfo info = (AppInfo) AppListUtil.this.mApps.get(((Integer) v.getTag()).intValue());
                    ComponentName componentName = new ComponentName(info.pkg, info.cls);
                    Intent intent = new Intent("android.intent.action.MAIN");
                    intent.addCategory("android.intent.category.LAUNCHER");
                    intent.setComponent(componentName);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    AppArcListViewAdapter.this.mContext.startActivity(intent);
                }
            });
        }

        public int getItemCount() {
            return AppListUtil.this.mApps.size();
        }
    }

    class AppListViewAdapter extends WearableListView.Adapter {
        private final Context mContext;
        private final LayoutInflater mInflater;

        public AppListViewAdapter(Context context) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(context);
        }

        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WearableListView.ViewHolder(this.mInflater.inflate(R.layout.applist_item, null));
        }

        public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
            TextView view = (TextView) holder.itemView.findViewById(R.id.app_name);
            AppInfo info = (AppInfo) AppListUtil.this.mApps.get(position);
            ((ImageView) holder.itemView.findViewById(R.id.app_icon)).setImageDrawable(info.icon);
            view.setText(info.title);
            holder.itemView.setTag(Integer.valueOf(position));
        }

        public int getItemCount() {
            return AppListUtil.this.mApps.size();
        }
    }

    public AppListUtil(Context context) {
        this.mContext = context;
        getApplist();
        this.mAdaper = new AppListViewAdapter(this.mContext);
        this.mArcAdaper = new AppArcListViewAdapter(this.mContext);
    }

    public void getApplist() {
        this.mApps = AppListCustomUtil.getAppList(this.mContext);
    }

    public void onItemClick(View view, int position) {
        AppInfo info = (AppInfo) this.mApps.get(position);
        ComponentName componentName = new ComponentName(info.pkg, info.cls);
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        this.mContext.startActivity(intent);
    }

    public WearableListView.Adapter getAdapter() {
        return this.mAdaper;
    }

    public Adapter getArcAdapter() {
        return this.mArcAdaper;
    }
}