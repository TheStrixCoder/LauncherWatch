package com.bid.launcherwatch;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemProperties;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

public class QuickSettingsFragment extends Fragment {
    private final String TAG = "cenontest_Log QuickSettingsFragment";
    /* access modifiers changed from: private */
    public List<Fragment> fragmentList;
    private CleanFragment mCleanFragment;
    private Context mContext;
    private MusicMediaFragment mMusicMediaFragment = new MusicMediaFragment();
    private PageCircleIndicator mPageCircleIndicator;
    public QuickSettingsSubFragment mQuickSettingsSubFragment;
    private QuickSettingsSystemConfigFragment mQuickSettingsSystemConfigFragment;
    private ViewPager mViewPager;
    private WeatherFragment mWeatherFragment;

    class QuickSettingsFragmentPageAdapter extends FragmentStatePagerAdapter {
        public QuickSettingsFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            return (Fragment) QuickSettingsFragment.this.fragmentList.get(position);
        }

        public int getCount() {
            return 5;
        }

        public int getItemPosition(Object object) {
            return -2;
        }
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        SystemProperties.set("persist.sys.clock.idle", String.valueOf(false));
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.mContext = getActivity();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quicksettings_fragment_layout, container, false);
        this.mViewPager = (ViewPager) view.findViewById(R.id.viewpage);
        this.fragmentList = new ArrayList();
        this.mQuickSettingsSubFragment = new QuickSettingsSubFragment();
        this.mQuickSettingsSystemConfigFragment = new QuickSettingsSystemConfigFragment();
        this.mCleanFragment = new CleanFragment();
        this.mWeatherFragment = new WeatherFragment();
        this.fragmentList.add(this.mQuickSettingsSubFragment);
        this.fragmentList.add(this.mQuickSettingsSystemConfigFragment);
        this.fragmentList.add(this.mCleanFragment);
        this.fragmentList.add(this.mMusicMediaFragment);
        this.fragmentList.add(this.mWeatherFragment);
        this.mViewPager.setAdapter(new QuickSettingsFragmentPageAdapter(getChildFragmentManager()));
        this.mViewPager.setCurrentItem(0);
        this.mViewPager.setOffscreenPageLimit(5);
        this.mPageCircleIndicator = (PageCircleIndicator) view.findViewById(R.id.indicator);
        this.mPageCircleIndicator.setViewPager(this.mViewPager);
        return view;
    }
}

