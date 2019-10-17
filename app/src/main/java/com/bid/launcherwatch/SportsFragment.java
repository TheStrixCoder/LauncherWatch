package com.bid.launcherwatch;



import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

public class SportsFragment extends Fragment {
    private final String TAG = "cenontest_Log SportFragment";
    /* access modifiers changed from: private */
    public List<Fragment> fragmentList;
    private Context mContext;
    private PageCircleIndicator mPageCircleIndicator;
    private SportsCalorieFragment mSportsCalorieFragment;
    public SportsDayDataFragment mSportsDayDataFragment;
    public SportsDayFragment mSportsDayFragment;
    private SportsDistanceFragment mSportsDistanceFragment;
    private SportsStepFragment mSportsStepFragment;
    private ViewPager mViewPager;

    class SportFragmentPageAdapter extends FragmentStatePagerAdapter {
        public SportFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            return (Fragment) SportsFragment.this.fragmentList.get(position);
        }

        public int getCount() {
            return 2;
        }

        public int getItemPosition(Object object) {
            return -2;
        }
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.mContext = getActivity();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sports_fragment_layout, container, false);
        this.mViewPager = (ViewPager) view.findViewById(R.id.viewpage);
        this.fragmentList = new ArrayList();
        this.mSportsCalorieFragment = new SportsCalorieFragment();
        this.mSportsDistanceFragment = new SportsDistanceFragment();
        this.mSportsStepFragment = new SportsStepFragment();
        this.mSportsDayFragment = new SportsDayFragment();
        this.mSportsDayDataFragment = new SportsDayDataFragment();
        this.fragmentList.add(this.mSportsDayDataFragment);
        this.fragmentList.add(this.mSportsDayFragment);
        this.mViewPager.setAdapter(new SportFragmentPageAdapter(getChildFragmentManager()));
        this.mViewPager.setCurrentItem(0);
        this.mViewPager.setOffscreenPageLimit(2);
        this.mPageCircleIndicator = (PageCircleIndicator) view.findViewById(R.id.indicator);
        this.mPageCircleIndicator.setViewPager(this.mViewPager);
        return view;
    }
}
