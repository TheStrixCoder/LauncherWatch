package com.bid.launcherwatch;


import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.INotificationManager;
import android.app.INotificationManager.Stub;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.Vibrator;
import android.provider.Settings.System;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.PageTransformer;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bid.launcherwatch.NotificationData.Entry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NotificationFragment extends Fragment {
    /* access modifiers changed from: private */
    public static final String TAG = NotificationFragment.class.getSimpleName();
    private static NotificationFragment sInstance;
    private Context mContext;
    private NotificationService ns;
    private NotificationListenerService mListener = new NotificationListenerService() {
        public void onNotificationPosted(final StatusBarNotification mSbn) {
            NotificationFragment.this.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Log.v(NotificationFragment.TAG, "onNotificationPosted: " + mSbn);
                    String pkg = mSbn.getPackageName();
                    String tag = mSbn.getTag();
                    if (tag == null) {
                        tag = "";
                    }
                    if ((!pkg.equals("com.mediatek.wearable") && !pkg.equals("com.weitetech.smartconnect.wiiwearsdk") && !pkg.equals("com.weite.smartconnect.wiiwearsdk")) || !tag.contains("com.mediatek.swp")) {
                        if (NotificationHelper.filterNotification(mSbn.getNotification())) {
                            Log.i(NotificationFragment.TAG, "**********  onNotificationPosted, match filter");
                            NotificationHelper.dumpNotification(mSbn.getNotification());
                        } else {
                            Log.i(NotificationFragment.TAG, "**********  onNotificationPosted");
                            if (NotificationHelper.getTitle(NotificationCompat.getExtras(mSbn.getNotification())) != null) {
                                NotificationFragment.this.addNotification(mSbn);
                            }
                        }
                    }
                }
            });
        }

        public void onNotificationRemoved(final StatusBarNotification mSbn) {
            NotificationFragment.this.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Log.v(NotificationFragment.TAG, "********** onNotificationRemoved: " + mSbn);
                    NotificationFragment.this.removeNotification(mSbn);
                }
            });
        }
    };
    /* access modifiers changed from: private */
    public View mNoItems;
    private INotificationManager mNoMan;
    /* access modifiers changed from: private */
    public int mPageCount = 0;
    private VerticalViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private PowerManager mPowerManager;
    private LayoutTransition mRealLayoutTransition;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent != null && "com.bid.launcherwatch.NOTIFICATION_LISTENER.CANCEL".equals(intent.getAction())) {
                Log.i(NotificationFragment.TAG, "mReceiver::ACTION_NOTIFICATION_CANCEL_BY_USER");
                NotificationFragment.this.removeNotification(intent.getExtras());
            } else if ((intent == null || !"com.bid.launcherwatch.DEMO_MODE_POST".equals(intent.getAction())) && intent.getAction().equals("android.intent.action.LOCALE_CHANGED") && NotificationFragment.this.mNoItems != null) {
                TextView textview = (TextView) NotificationFragment.this.mNoItems.findViewById(R.id.no_items_textview);
                if (textview != null) {
                    textview.setText(R.string.notification_no_items);
                }
            }
        }
    };
    private Vibrator mVibrator;

    private class NotificationPagerAdapter extends FragmentStatePagerAdapter {
        public NotificationPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            return NotificationFragment.this.getNotificationSubFragment(position);
        }

        public int getCount() {
            NotificationFragment.logd("NotificationPagerAdapter size() = " + NotificationFragment.this.mPageCount, new Object[0]);
            return NotificationFragment.this.mPageCount;
        }

        public int getItemPosition(Object object) {
            return -2;
        }
    }

    public NotificationFragment() {
        sInstance = this;
    }

    public static NotificationFragment getInstance() {
        if (sInstance == null) {
            sInstance = new NotificationFragment();
        }
        return sInstance;
    }

    public void onCreate(Bundle icicle) {
        logd("onCreate(%s)", icicle);
        super.onCreate(icicle);
        this.ns=new NotificationService();
        this.mContext = getActivity();
        this.mPowerManager = (PowerManager) this.mContext.getSystemService(Context.POWER_SERVICE);
        this.mVibrator = (Vibrator) this.mContext.getSystemService(Context.VIBRATOR_SERVICE);
        this.mNoMan = Stub.asInterface(ServiceManager.getService(Context.NOTIFICATION_SERVICE));
        try {
            //registerAsSystemService(this.mContext, new ComponentName(this.mContext.getPackageName(), getClass().getCanonicalName()), ns);
        } catch (Exception e) {
            Log.e(TAG, "Cannot register listener", e);
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.bid.launcherwatch.NOTIFICATION_LISTENER.CANCEL");
        filter.addAction("android.intent.action.LOCALE_CHANGED");
        getActivity().registerReceiver(this.mReceiver, filter);
    }

//    private void registerAsSystemService(Context mContext, ComponentName componentName, NotificationService i) {
//        String className = "android.service.notification.NotificationListenerService";
//        try {
//
//            @SuppressWarnings("rawtypes")
//            Class NotificationListenerService = Class.forName(className);
//
//            //Parameters Types
//            //you define the types of params you will pass to the method
//            @SuppressWarnings("rawtypes")
//            Class[] paramTypes= new Class[3];
//            paramTypes[0]= Context.class;
//            paramTypes[1]= ComponentName.class;
//            paramTypes[2] = int.class;
//
//            Method register = NotificationListenerService.getMethod("registerAsSystemService", paramTypes);
//
//            //Parameters of the registerAsSystemService method (see official doc for more info)
//            Object[] params= new Object[3];
//            Context ctx = null;
//            params[0]= mContext;
//            //error1
//
//            params[1]= new ComponentName(mContext.getPackageName(), mContext.getClass().getCanonicalName());
//            params[2]= -1; // All user of the device, -2 if only current user
//            // finally, invoke the function on our instance
//            register.invoke(i, params);
//
//        } catch (ClassNotFoundException e) {
//            Log.e(TAG, "Class not found", e);
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            Log.e(TAG, "No such method", e);
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            Log.e(TAG, "InvocationTarget", e);
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            Log.e(TAG, "Illegal access", e);
//            e.printStackTrace();
//        }
//
//    }
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onAttach(Activity activity) {
        logd("onAttach(%s)", activity.getClass().getSimpleName());
        super.onAttach(activity);
    }

    private void notifyDataChangedPosted(StatusBarNotification mSbn) {
        this.mContext.sendBroadcast(new Intent("com.bid.launcherwatch.NOTIFICATION_LISTENER.POSTED"));
    }

    private void notifyDataChangedRemoved(StatusBarNotification mSbn) {
        this.mContext.sendBroadcast(new Intent("com.bid.launcherwatch.NOTIFICATION_LISTENER.REMOVED"));
    }

    /* access modifiers changed from: private */
    public static void logd(String msg, Object... args) {
    }

    private void refreshPager() {
        boolean z = false;
        if (this.mPager != null) {
            this.mPageCount = NotificationHelper.getNotificationData().size();
            logd("NotificationPagerAdapter mPageCount, size() = " + this.mPageCount, new Object[0]);
            this.mPager.getAdapter().notifyDataSetChanged();
            if (this.mPageCount == 0) {
                z = true;
            }
            setNoItemsVisibility(z);
        }
    }

    private boolean getNotificationComingState() {
        int mode = 0;
        try {
            mode = this.mNoMan.getZenMode();
        } catch (RemoteException e) {
        }
        return mode != 3;
    }

    /* access modifiers changed from: private */
    public void addNotification(StatusBarNotification mSbn) {
        int pos = NotificationHelper.findPositionByKey(mSbn.getPackageName(), mSbn.getTag(), mSbn.getId());
        if (NotificationHelper.isHighPriorityNotification(mSbn.getNotification())) {
        }
        String mPkgName = mSbn.getPackageName();
        String mTag = mSbn.getTag();
        if (mTag == null) {
            mTag = "";
        }
        boolean isNeedNoteAction = getNotificationComingState();
        Log.v("caojinliang", "isNeedNoteAction=" + isNeedNoteAction);
        MainActivity mMain = (MainActivity) getActivity();
        int isScreenOn = System.getInt(this.mContext.getContentResolver(), "notification_screenon", 0);
        if (isNeedNoteAction) {
            mMain.backToNoteView(isScreenOn != 0);
        } else {
            mMain.backToNoteView(false);
        }
        boolean isVibrate = true;
        boolean vibrateWhenSMS = System.getInt(this.mContext.getContentResolver(), "vibrate_when_get_sms", 1) == 1;
        if (mPkgName.equals("com.android.mms") && !vibrateWhenSMS) {
            isVibrate = false;
        }
        if (mPkgName.equals("com.android.systemui") && mTag.equals("low_battery")) {
            isVibrate = false;
        }
        if (mPkgName.equals("com.adups.fota")) {
            isVibrate = false;
        }
        if (!isNeedNoteAction) {
            isVibrate = false;
        }
        if (!NotificationHelper.isDefaultVibrate(mSbn.getNotification()) && NotificationHelper.getVibrate(mSbn.getNotification()) == null && isVibrate) {
            this.mVibrator.vibrate(150);
            Log.v("caojinliang", "mVibrator.vibrate");
        }
        if (pos == -1) {
            Log.d(TAG, "addNotification: added at " + NotificationHelper.add(new Entry(mSbn, NotificationSubFragment.create(mSbn))));
            refreshPager();
            this.mPager.setCurrentItem(0);
        } else {
            Log.d(TAG, "addNotification: refresh at " + pos + " for" + mSbn);
            NotificationHelper.update(NotificationHelper.getNotificationData().get(pos));
            NotificationSubFragment subFragment = getNotificationSubFragment(pos);
            if (subFragment != null) {
                subFragment.setContent(mSbn);
                subFragment.refreshContent();
            }
        }
        notifyDataChangedPosted(mSbn);
    }

    /* access modifiers changed from: private */
    public void removeNotification(StatusBarNotification mSbn) {
        String mPkgName = mSbn.getPackageName();
        String mTag = mSbn.getTag();
        int mId = mSbn.getId();
        Log.d(TAG, "removeNotification packageName=" + mPkgName + " mTag=" + mTag + " id=" + mId);
        if (NotificationHelper.remove(mPkgName, mTag, mId) != null) {
            refreshPager();
            notifyDataChangedRemoved(mSbn);
        }
    }

    private void setNoItemsVisibility(boolean mIsNoItems) {
        if (this.mNoItems != null) {
            this.mNoItems.setVisibility(mIsNoItems ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /* access modifiers changed from: private */
    public NotificationSubFragment getNotificationSubFragment(int mPos) {
        if (mPos >= 0 && mPos < NotificationHelper.getNotificationData().size()) {
            Entry mEntry = NotificationHelper.getNotificationData().get(mPos);
            if (mEntry != null) {
                return mEntry.mNotificationSubFragment;
            }
        }
        return null;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_fragment_layout, container, false);
        this.mPageCount = NotificationHelper.getNotificationData().size();
        logd("NotificationPagerAdapter mPageCount, size() = " + this.mPageCount, new Object[0]);
        this.mPager = (VerticalViewPager) view.findViewById(R.id.vpager);
        this.mNoItems = view.findViewById(R.id.notification_no_items);
        setNoItemsVisibility(this.mPageCount == 0);
        this.mPagerAdapter = new NotificationPagerAdapter(getChildFragmentManager());
        this.mPager.setAdapter(this.mPagerAdapter);
        this.mPager.setClipToPadding(false);
        this.mPager.setPageMargin(10);
        this.mPager.setCurrentItem(0);
        this.mPager.setOffscreenPageLimit(2);
        this.mPager.setPageTransformer(false, new PageTransformer() {
            public void transformPage(View page, float position) {
                page.setAlpha(1.0f - Math.abs(position));
            }
        });
        this.mPager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
            public void onPageSelected(int mPosition) {
                NotificationSubFragment mFragPrev = NotificationFragment.this.getNotificationSubFragment(mPosition - 1);
                if (mFragPrev != null) {
                    mFragPrev.collapseLayout();
                }
                NotificationSubFragment mFragNext = NotificationFragment.this.getNotificationSubFragment(mPosition + 1);
                if (mFragNext != null) {
                    mFragNext.collapseLayout();
                }
            }
        });
        this.mRealLayoutTransition = new LayoutTransition();
        this.mRealLayoutTransition.setAnimateParentHierarchy(true);
        return view;
    }

    public void onDestroy() {
        logd("onDestroy()", new Object[0]);
        sInstance = null;
        getActivity().unregisterReceiver(this.mReceiver);
        super.onDestroy();
    }

    public void onResume() {
        logd("onResume()", new Object[0]);
        super.onResume();
        this.mPager.setLayoutTransition(null);
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        WatchApp.setMainMenuStatus(isVisibleToUser);
        WatchApp.setIsCanSlide(false);
        SystemProperties.set("persist.sys.clock.idle", String.valueOf(false));
    }

    public int getPageCount() {
        return this.mPageCount;
    }

    /* access modifiers changed from: private */
    public void removeNotification(Bundle mArgs) {
        String mKey = mArgs.getString("key");
        Log.d(TAG, "removeNotification Key=" + mKey);
        try {
            this.mListener.cancelNotification(mKey);
        } catch (NullPointerException e) {
            Log.e(TAG, "removeNotification NullPointerException.");
        }
    }
}

