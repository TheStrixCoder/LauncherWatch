package com.bid.launcherwatch;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.os.SystemProperties;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class IdleFragment extends Fragment {
    private static final String TAG = IdleFragment.class.getSimpleName();
    private static int mIndex = -1;
    private static int mStyle = 0;
    public final ClockFragment mClockFragment = ClockFragment.getInstance();
    /* access modifiers changed from: private */
    public Fragment mCurAppListFragment;
    /* access modifiers changed from: private */
    public AppFitnessModeFragment mFitnessFragment = new AppFitnessModeFragment();
    private MyAnalogClock mMyclock = null;
    /* access modifiers changed from: private */
    public final NotificationFragment mNotificationFragment = NotificationFragment.getInstance();
    private HorizontalViewPager mPager;
    private PagerAdapter mPagerAdapter;
    /* access modifiers changed from: private */
    public View top_black;

    public static class ClockFragment extends Fragment {
        private static ClockFragment INSTANCE;
        private String TAG = "cenontest_Log ClockFragment";
        View clockview = null;
        private FrameLayout mClockHost = null;
        OnTouchListener mOnTouchListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                WatchApp.onTouch(event);
                return false;
            }
        };
        private boolean mPauseState = false;

        public static ClockFragment getInstance() {
            if (INSTANCE == null) {
                INSTANCE = new ClockFragment();
            }
            return INSTANCE;
        }

        public ClockFragment() {
            INSTANCE = this;
        }

        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            WatchApp.setIsCanSlide(true);
            WatchApp.setClockViewStatus(isVisibleToUser);
            SystemProperties.set("persist.sys.clock.idle", String.valueOf(true));
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.idle_fragment_layout_new, container, false);
            FrameLayout clockFragment = (FrameLayout) view.findViewById(R.id.idle_clock);
            this.mClockHost = (FrameLayout) getActivity().findViewById(R.id.bottom_clock);
            clockFragment.setOnTouchListener(this.mOnTouchListener);
            clockFragment.setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View v) {
                    //WatchApp.setLiveWatchFaceDisconnect();
                    ClockFragment.this.getChooseWatchView();
                    return true;
                }
            });
            return view;
        }

        public void getChooseWatchView() {
            if (WatchApp.getAllClockCount() > 0) {
                ((MainActivity) getActivity()).getChooseWatchView(WatchApp.getClockIndex(getActivity()));
            }
        }

        public void onCreate(Bundle savedInstanceState) {
            Log.d("xiaocai_clockFragment", "ClockFragment_onCreate");
            super.onCreate(savedInstanceState);
        }

        public void onActivityCreated(Bundle savedInstanceState) {
            Log.d("xiaocai_clockFragment", "ClockFragment_onActivityCreated");
            super.onActivityCreated(savedInstanceState);
        }

        public void onStart() {
            super.onStart();
            Log.d("xiaocai_clockFragment", "ClockFragment_onStart");
        }

        public void onDetach() {
            Log.d("xiaocai_clockFragment", "ClockFragment_onDetach");
            super.onDetach();
        }

        public void onResume() {
            Log.d("xiaocai_clockFragment", "ClockFragment_onResume");
            super.onResume();
            this.mPauseState = false;
            WatchApp.setLiveWatchFaceVisibility(true);
        }

        public void onPause() {
            Log.d("xiaocai_clockFragment", "ClockFragment_onPause");
            super.onPause();
            this.mPauseState = true;
            WatchApp.setLiveWatchFaceVisibility(false);
        }

        public void onStop() {
            Log.d("xiaocai_clockFragment", "ClockFragment_onStop");
            super.onStop();
        }

        public void onDestroyView() {
            Log.d("xiaocai_clockFragment", "ClockFragment_onDestroyView");
            super.onDestroyView();
        }

        public void onDestroy() {
            Log.d("xiaocai_clockFragment", "ClockFragment_onDestroy");
            super.onDestroy();
        }
    }

    private class IdlePagerAdapter extends FragmentStatePagerAdapter {
        private int mChildCount = 4;

        public IdlePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            if (position == 1) {
                return IdleFragment.this.mClockFragment;
            }
            if (position == 0) {
                return IdleFragment.this.mNotificationFragment;
            }
            if (position == 2) {
                return IdleFragment.this.mCurAppListFragment;
            }
            if (position == 3) {
                return IdleFragment.this.mFitnessFragment;
            }
            return IdleFragment.this.mClockFragment;
        }

        public int getCount() {
            return 4;
        }

        public void notifyDataSetChanged() {
            this.mChildCount = getCount();
            super.notifyDataSetChanged();
        }

        public int getItemPosition(Object object) {
            if (this.mChildCount <= 0) {
                return super.getItemPosition(object);
            }
            this.mChildCount--;
            return -2;
        }
    }

    public IdleFragment() {
    }

    @SuppressLint("ValidFragment")
    public IdleFragment(int style) {
        setAppListStyle(style);
    }

    public void setAppListStyle(int style) {
        mStyle = style;
        if (style == 0) {
            this.mCurAppListFragment = new AppArcListFragment();
        } else if (1 == style) {
            this.mCurAppListFragment = new AppListRoundFragment();
        } else {
            this.mCurAppListFragment = new AppListRoundFragment();
        }
    }

    public void refreshApplist() {
        if (this.mCurAppListFragment != null) {
            if (this.mCurAppListFragment instanceof AppListFragment) {
                ((AppListFragment) this.mCurAppListFragment).refreshApplist();
            } else if (this.mCurAppListFragment instanceof AppMatrixFragment) {
                ((AppMatrixFragment) this.mCurAppListFragment).refreshApplist();
            } else if (this.mCurAppListFragment instanceof AppListRoundFragment) {
                ((AppListRoundFragment) this.mCurAppListFragment).refreshApplist();
            } else if (this.mCurAppListFragment instanceof AppArcListFragment) {
                ((AppArcListFragment) this.mCurAppListFragment).refreshApplist();
            }
        }
        if (this.mFitnessFragment != null) {
            this.mFitnessFragment.refreshApplist();
        }
    }

    public int getAppListStyle() {
        return mStyle;
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        WatchApp.setClockViewStatus(isVisibleToUser);
        SystemProperties.set("persist.sys.clock.idle", String.valueOf(true));
    }

    public void backToClock() {
        if (this.mPager != null && this.mPager.getCurrentItem() != 1) {
            this.mPager.setCurrentItem(1, false);
        }
    }

    public void backToNotification() {
        if (this.mPager != null && this.mPager.getCurrentItem() != 0) {
            this.mPager.setCurrentItem(0, false);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.idle_fragment_layout, container, false);
        this.top_black = getActivity().findViewById(R.id.top_black);
        this.mPager = (HorizontalViewPager) view.findViewById(R.id.pager);
        this.mPagerAdapter = new IdlePagerAdapter(getChildFragmentManager());
        this.mPager.setAdapter(this.mPagerAdapter);
        this.mPager.setOffscreenPageLimit(4);
        this.mPager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
            }
        });
        this.mPager.setOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 1) {
                    IdleFragment.this.top_black.setAlpha(positionOffset);
                } else if (position == 0) {
                    IdleFragment.this.top_black.setAlpha(1.0f - positionOffset);
                }
            }

            public void onPageSelected(int position) {
            }

            public void onPageScrollStateChanged(int state) {
                Log.d("qs", "state" + state);
            }
        });
        backToClock();
        return view;
    }

    public void update() {
        this.mPagerAdapter.notifyDataSetChanged();
    }
}

