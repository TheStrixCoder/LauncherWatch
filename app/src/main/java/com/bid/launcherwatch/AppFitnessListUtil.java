package com.bid.launcherwatch;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.wearable.view.CircledImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bid.launcherwatch.AppListCustomUtil.AppInfo;
import java.util.ArrayList;

public class AppFitnessListUtil {
    /* access modifiers changed from: private */
    public ArrayList<AppInfo> mApps = new ArrayList<>();
    private AppArcListViewAdapter mArcAdaper;
    private Context mContext;

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
            return new MyViewHolder(this.mInflater.inflate(R.layout.fitness_item_layout, null));
        }

        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            MyViewHolder holder = (MyViewHolder) viewHolder;
            AppInfo info = (AppInfo) AppFitnessListUtil.this.mApps.get(position);
            holder.icon.setImageDrawable(info.icon);
            holder.name.setText(info.title);
            holder.itemView.setTag(Integer.valueOf(position));
            holder.itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    int pos = ((Integer) v.getTag()).intValue();
                    Intent intent = new Intent();
                    intent.setAction("com.sprots.wiiteer.HomeActivity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//268435456
                    intent.putExtra("mode", pos);
                    intent.putExtra("luncher", true);
                    AppArcListViewAdapter.this.mContext.startActivity(intent);
                }
            });
        }

        public int getItemCount() {
            return AppFitnessListUtil.this.mApps.size();
        }
    }

    public AppFitnessListUtil(Context context) {
        this.mContext = context;
        getApplist();
        this.mArcAdaper = new AppArcListViewAdapter(this.mContext);
    }

    public void getApplist() {
        this.mApps.clear();
        this.mApps.add(new AppInfo("", "", this.mContext.getResources().getString(R.string.str_outdoor_run), this.mContext.getResources().getDrawable(R.drawable.ic_run_outdoor)));
        this.mApps.add(new AppInfo("", "", this.mContext.getResources().getString(R.string.str_outdoor_walk), this.mContext.getResources().getDrawable(R.drawable.ic_walk_outdoor)));
        this.mApps.add(new AppInfo("", "", this.mContext.getResources().getString(R.string.str_indoor_run), this.mContext.getResources().getDrawable(R.drawable.ic_run_indoor)));
        this.mApps.add(new AppInfo("", "", this.mContext.getResources().getString(R.string.str_bike), this.mContext.getResources().getDrawable(R.drawable.ic_bike2)));
        this.mApps.add(new AppInfo("", "", this.mContext.getResources().getString(R.string.str_basket), this.mContext.getResources().getDrawable(R.drawable.ic_basketball2)));
        this.mApps.add(new AppInfo("", "", this.mContext.getResources().getString(R.string.str_foot), this.mContext.getResources().getDrawable(R.drawable.ic_football2)));
        this.mApps.add(new AppInfo("", "", this.mContext.getResources().getString(R.string.str_pinpang), this.mContext.getResources().getDrawable(R.drawable.ic_pin_pang2)));
        this.mApps.add(new AppInfo("", "", this.mContext.getResources().getString(R.string.str_badminton), this.mContext.getResources().getDrawable(R.drawable.ic_badminton2)));
        this.mApps.add(new AppInfo("", "", this.mContext.getResources().getString(R.string.str_rope_skipping), this.mContext.getResources().getDrawable(R.drawable.ic_rope_skiping2)));
    }

    public Adapter getArcAdapter() {
        return this.mArcAdaper;
    }
}
