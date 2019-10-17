package com.bid.launcherwatch;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import com.bid.launcherwatch.AppListCustomUtil.AppInfo;
import com.bid.launcherwatch.view.RoundImageView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppListRoundFragment extends Fragment {
    private static AppListRoundFragment INSTANCE;
    List<Map<String, Object>> items = new ArrayList();
    private ArrayList<AppInfo> mApps;
    private Context mContext;
    private DirectionalViewPager mDirectionalViewPager;
    private LayoutInflater mLayoutInflater;
    private LayoutParams mlp = new LayoutParams(-2, -2);

    public class RoundAppAdapter extends PagerAdapter {
        private int APP_SIZE;
        private int APP_SIZE_CENTER;
        private int SCREEN_VIEW_SIZE;
        int[] angle = {270, 225, 315, 180, 0, 135, 45, 90};
        /* access modifiers changed from: private */
        public Context mContext;
        private List<AppInfo> mList;

        /* renamed from: pm */
        private PackageManager f3pm;
        ArrayList<View> views;

        public RoundAppAdapter(Context context, List<AppInfo> list, ArrayList<View> passedviews) {
            this.mContext = context;
            Resources resources = this.mContext.getResources();
            this.APP_SIZE = (int) resources.getDimension(R.dimen.roundlist_app_icon_size);
            this.APP_SIZE_CENTER = (int) resources.getDimension(R.dimen.roundlist_app_center_size);
            this.SCREEN_VIEW_SIZE = resources.getDisplayMetrics().widthPixels;
            this.f3pm = context.getPackageManager();
            this.views = new ArrayList<>();
            for (int i = 0; i < passedviews.size(); i++) {
                this.views.add((View) passedviews.get(i));
            }
            this.mList = new ArrayList();
            for (int i2 = 0; i2 < list.size(); i2++) {
                this.mList.add((AppInfo) list.get(i2));
            }
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        public int getCount() {
            return this.views.size();
        }

        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View) this.views.get(position));
        }

        public Object instantiateItem(View container, int position) {
            View iView = (View) this.views.get(position);
            RoundImageView[] mImageViews = {(RoundImageView) iView.findViewById(R.id.apps_icon_1), (RoundImageView) iView.findViewById(R.id.apps_icon_2), (RoundImageView) iView.findViewById(R.id.apps_icon_3), (RoundImageView) iView.findViewById(R.id.apps_icon_4), (RoundImageView) iView.findViewById(R.id.apps_icon_5), (RoundImageView) iView.findViewById(R.id.apps_icon_6), (RoundImageView) iView.findViewById(R.id.apps_icon_7), (RoundImageView) iView.findViewById(R.id.apps_icon_8)};
            CustomAnalogClock6 centerimg = (CustomAnalogClock6) iView.findViewById(R.id.watch);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(new MarginLayoutParams(this.APP_SIZE_CENTER, this.APP_SIZE_CENTER));
            lp.setMargins((this.SCREEN_VIEW_SIZE - this.APP_SIZE_CENTER) / 2, (this.SCREEN_VIEW_SIZE - this.APP_SIZE_CENTER) / 2, 0, 0);
            centerimg.setLayoutParams(lp);
            for (int index = 0; index < 8; index++) {
                RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(new MarginLayoutParams(this.APP_SIZE, this.APP_SIZE));
                int gap = (this.SCREEN_VIEW_SIZE - this.APP_SIZE_CENTER) / 2;
                lp2.setMargins((int) ((((double) ((this.SCREEN_VIEW_SIZE - gap) / 2)) * (Math.cos((((double) this.angle[index]) * 3.141592653589793d) / 180.0d) + 1.0d)) + ((double) ((gap - this.APP_SIZE) / 2))), (int) ((((double) ((this.SCREEN_VIEW_SIZE - gap) / 2)) * (Math.sin((((double) this.angle[index]) * 3.141592653589793d) / 180.0d) + 1.0d)) + ((double) ((gap - this.APP_SIZE) / 2))), 0, 0);
                mImageViews[index].setLayoutParams(lp2);
            }
            for (int index2 = 0; index2 < 8; index2++) {
                int appindex = (position * 8) + index2;
                if (appindex < this.mList.size()) {
                    setAppIconOnClick(mImageViews[index2], (AppInfo) this.mList.get(appindex));
                } else {
                    setAppIconOnClick(mImageViews[index2], null);
                }
            }
            ((ViewPager) container).addView(iView);
            return iView;
        }

        private void setAppIconOnClick(ImageView app, final AppInfo appInfo) {
            if (appInfo != null) {
                app.setImageDrawable(appInfo.icon);
                app.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        ComponentName componentName = new ComponentName(appInfo.pkg, appInfo.cls);
                        Intent intent = new Intent("android.intent.action.MAIN");
                        intent.addCategory("android.intent.category.LAUNCHER");
                        intent.setComponent(componentName);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        RoundAppAdapter.this.mContext.startActivity(intent);
                    }
                });
                return;
            }
            app.setVisibility(View.GONE);
        }
    }

    public AppListRoundFragment() {
        INSTANCE = this;
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        WatchApp.setMainMenuStatus(isVisibleToUser);
        WatchApp.setIsCanSlide(false);
        SystemProperties.set("persist.sys.clock.idle", String.valueOf(false));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.applistround, container, false);
        Log.i("guoxiaolong11", "onCreateView:rootView=" + rootView);
        this.mContext = rootView.getContext();
        this.mLayoutInflater = inflater;
        this.mDirectionalViewPager = (DirectionalViewPager) rootView.findViewById(R.id.pager);
        Log.i("guoxiaolong11", "onCreateView:mDirectionalViewPager=" + this.mDirectionalViewPager);
        initViews();
        return rootView;
    }

    public void initViews() {
        this.mApps = AppListCustomUtil.getAppList(getActivity());
        Log.i("guoxiaolong", "mContext=" + this.mApps);
        Log.d("AppListRoundFragment", " initViews apps counts is: " + this.mApps.size());
        int PageCount = (int) Math.ceil((double) (((float) this.mApps.size()) / 8.0f));
        ArrayList<View> views = new ArrayList<>();
        for (int pageindex = 0; pageindex < PageCount; pageindex++) {
            //error2
            views.add(this.mLayoutInflater.inflate(R.layout.custom_view, null));
        }
        this.mDirectionalViewPager.setAdapter(new RoundAppAdapter(this.mContext, this.mApps, views));
        Log.d("guoxiaolong11", "initViews():mDirectionalViewPager=" + this.mDirectionalViewPager);
        this.mDirectionalViewPager.setOrientation(1);
    }

    public void onResume() {
        super.onResume();
    }

    public void refreshApplist() {
        Log.d("guoxiaolong11", "refreshApplist():mDirectionalViewPager=" + this.mDirectionalViewPager);
        if (this.mDirectionalViewPager != null) {
            initViews();
            this.mDirectionalViewPager.invalidate();
        }
    }

    public void onDestroy() {
        INSTANCE = null;
        super.onDestroy();
    }
}
