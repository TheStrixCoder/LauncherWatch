package com.bid.launcherwatch;


import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.System;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bid.launcherwatch.view.CalculatorUtil;
import com.bid.launcherwatch.view.CustListView;
import com.bid.launcherwatch.view.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class SportsDayFragment extends Fragment {
    private final String TAG = "SportsStepFragment";
    int data = -1;
    private boolean hasStarted = false;
    Time mCalendar = new Time();
    private OnClickListener mClickListener = new OnClickListener() {
        public void onClick(View v) {
            if (SportsDayFragment.this.mStepsMeterView.getVisibility() == View.VISIBLE) {
                SportsDayFragment.this.mStepsMeterView.setVisibility(View.GONE);
                SportsDayFragment.this.mStepDayDetailview.setVisibility(View.VISIBLE);
            } else {
                SportsDayFragment.this.mStepsMeterView.setVisibility(View.VISIBLE);
                SportsDayFragment.this.mStepDayDetailview.setVisibility(View.GONE);
            }
            SportsDayFragment.this.showUI(false);
        }
    };
    /* access modifiers changed from: private */
    public Context mContext;
    public int mCurrentSteps = 0;
    private final DTReceiver mDTReceiver = new DTReceiver(this, null);
    /* access modifiers changed from: private */
    public List<Data> mData;
    /* access modifiers changed from: private */
    public DataAdapter mDataAdapter;
    /* access modifiers changed from: private */
    public CustListView mDetailListview;
    private boolean mIsVisibleToUser = false;
    private final Receiver mReceiver = new Receiver(this, null);
    /* access modifiers changed from: private */
    public RelativeLayout mStepDayDetailview;
    /* access modifiers changed from: private */
    public MiniColumnChart mStepsMeterView = null;
    private TextView mTvAverg;
    private TextView mTvTotall;
    private TextView mTvTotallDis;
    private String queryUri = "content://com.watchHealth.Walk/query";
    private ContentResolver resolver;
    User user;

    private final class DTReceiver extends BroadcastReceiver {
        /* synthetic */ DTReceiver(SportsDayFragment this$02, DTReceiver dTReceiver) {
            this();
        }

        private DTReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.intent.action.DATE_CHANGED") || action.equals("android.intent.action.TIME_SET")) {
                SportsDayFragment.this.mData = SportsDayFragment.this.getDataList();
                SportsDayFragment.this.mDataAdapter = new DataAdapter(SportsDayFragment.this.mData, SportsDayFragment.this.getActivity());
                SportsDayFragment.this.mDetailListview.setAdapter(SportsDayFragment.this.mDataAdapter);
                SportsDayFragment.this.mDataAdapter.notifyDataSetChanged();
            }
        }
    }

    private final class Receiver extends BroadcastReceiver {
        /* synthetic */ Receiver(SportsDayFragment this$02, Receiver receiver) {
            this();
        }

        private Receiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.sinsoft.action.health.step_count")) {
                SportsDayFragment.this.showUI(false);
            }
        }
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.mContext = getActivity();
        this.resolver = this.mContext.getContentResolver();
    }

    private boolean cursorIsNull(Cursor paramCursor) {
        return paramCursor == null || paramCursor.getCount() < 1;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sports_day_fragment_layout, container, false);
        this.mCurrentSteps = WatchApp.getSteps(this.mContext);
        this.mStepsMeterView = (MiniColumnChart) view.findViewById(R.id.stepDayview);
        this.mStepsMeterView.setOnClickListener(this.mClickListener);
        this.mStepDayDetailview = (RelativeLayout) view.findViewById(R.id.stepDayDetailview);
        this.mStepDayDetailview.setOnClickListener(this.mClickListener);
        this.mDetailListview = (CustListView) view.findViewById(R.id.Detail_listview);
        this.mData = getDataList();
        this.mDataAdapter = new DataAdapter(this.mData, getActivity());
        this.mDetailListview.setAdapter(this.mDataAdapter);
        this.mTvAverg = (TextView) view.findViewById(R.id.tv_averg_step);
        this.mTvTotall = (TextView) view.findViewById(R.id.tv_totall_step);
        this.mTvTotallDis = (TextView) view.findViewById(R.id.tv_totall_distance);
        ((ImageButton) view.findViewById(R.id.sport_target_settings)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.example.qs.myapplication", "com.example.qs.myapplication.MainActivity"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                try {
                    SportsDayFragment.this.mContext.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                }
            }
        });
        registerStepsCntReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.DATE_CHANGED");
        filter.addAction("android.intent.action.TIME_SET");
        this.mContext.registerReceiver(this.mDTReceiver, filter);
        showUI(true);
        return view;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.mContext.unregisterReceiver(this.mDTReceiver);
    }

    /* access modifiers changed from: private */
    public List<Data> getDataList() {
        int[] stepDate = queryLast7DayStepDate(this.resolver);
        String[] distance = queryLast7DayDistanceDate(this.resolver);
        String[] date = queryLast7Day();
        ArrayList localArrayList = new ArrayList();
        for (int i = 6; i >= 0; i--) {
            localArrayList.add(new Data(date[i], stepDate[i], distance[i]));
        }
        return localArrayList;
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    private void registerStepsCntReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.sinsoft.action.health.step_count");
        this.mContext.registerReceiver(this.mReceiver, filter);
    }

    private void unRegisterStepsCntReceiver() {
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    public String getCurrentDateByOffset(String paramString, int paramInt1, int paramInt2) {
        try {
            SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(paramString);
            GregorianCalendar localGregorianCalendar = new GregorianCalendar();
            localGregorianCalendar.add(paramInt1, paramInt2);
            return localSimpleDateFormat.format(localGregorianCalendar.getTime());
        } catch (Exception localException) {
            localException.printStackTrace();
            return null;
        }
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.mIsVisibleToUser = isVisibleToUser;
        if (this.mStepsMeterView != null) {
            if (isVisibleToUser) {
                this.hasStarted = true;
                registerStepsCntReceiver();
                showUI(true);
            } else if (this.hasStarted) {
                this.hasStarted = false;
                unRegisterStepsCntReceiver();
                this.mStepsMeterView.cleanAnim();
            }
        }
    }

    public String[] queryLast7Day() {
        this.mCalendar.setToNow();
        String[] arrayOfStr = new String[7];
        for (int j = 0; j <= 6; j++) {
            arrayOfStr[j] = getCurrentDateByOffset("MM-dd", 5, j - 6);
        }
        return arrayOfStr;
    }

    public String[] queryLast7DayDistanceDate(ContentResolver resolver2) {
        this.mCalendar.setToNow();
        String[] arrayOfInt = {"0.0", "0.0", "0.0", "0.0", "0.0", "0.0", "0.0"};
        for (int j = 0; j <= 6; j++) {
            String currentDateByOffset = getCurrentDateByOffset("yyyy-MM-dd", 5, j - 6);
            ContentResolver contentResolver = resolver2;
            Cursor cursor = contentResolver.query(Uri.parse(this.queryUri), new String[]{"_id", "date", "steps", "diatance", "kcal", "long_time", "targetsteps"}, "date=?", new String[]{currentDateByOffset}, null);
            if (cursorIsNull(cursor)) {
                Log.d("qs_queryDate1", arrayOfInt[j] + "");
            } else {
                cursor.moveToFirst();
                String distance = cursor.getString(cursor.getColumnIndexOrThrow("diatance"));
                if (distance == null) {
                    arrayOfInt[j] = "0.0";
                } else {
                    if (distance.equals("0")) {
                        distance = "0.0";
                    }
                    arrayOfInt[j] = distance;
                }
                Log.d("qs_queryDate2", arrayOfInt[j] + "");
            }
            if (cursor != null) {
                cursor.close();
            }
            Log.d("qs_weeday", " i: " + 6 + " j: " + j);
        }
        arrayOfInt[6] = WatchApp.getDistance(this.mContext);
        return arrayOfInt;
    }

    public int[] queryLast7DayStepDate(ContentResolver resolver2) {
        this.mCalendar.setToNow();
        int[] arrayOfInt = new int[7];
        for (int j = 0; j <= 6; j++) {
            String currentDateByOffset = getCurrentDateByOffset("yyyy-MM-dd", 5, j - 6);
            ContentResolver contentResolver = resolver2;
            Cursor cursor = contentResolver.query(Uri.parse(this.queryUri), new String[]{"_id", "date", "steps", "diatance", "kcal", "long_time", "targetsteps"}, "date=?", new String[]{currentDateByOffset}, null);
            if (cursorIsNull(cursor)) {
                Log.d("qs_queryDate1", arrayOfInt[j] + "");
            } else {
                cursor.moveToFirst();
                arrayOfInt[j] = cursor.getInt(cursor.getColumnIndexOrThrow("steps"));
                Log.d("qs_queryDate2", arrayOfInt[j] + "");
            }
            if (cursor != null) {
                cursor.close();
            }
            Log.d("qs_weeday", " i: " + 6 + " j: " + j);
        }
        arrayOfInt[6] = WatchApp.getSteps(this.mContext);
        return arrayOfInt;
    }

    public int[] queryStepDate(ContentResolver resolver2) {
        int weeday;
        this.mCalendar.setToNow();
        if (this.mCalendar.weekDay == 0) {
            weeday = 7;
        } else {
            weeday = this.mCalendar.weekDay;
        }
        Log.d("qs_weeday", this.mCalendar.weekDay + "");
        Log.d("qs_weeday", weeday + "");
        int[] arrayOfInt = new int[weeday];
        int i = weeday - 1;
        for (int j = 0; j <= i; j++) {
            String currentDateByOffset = getCurrentDateByOffset("yyyy-MM-dd", 5, j - i);
            ContentResolver contentResolver = resolver2;
            Cursor cursor = contentResolver.query(Uri.parse(this.queryUri), new String[]{"_id", "date", "steps", "diatance", "kcal", "long_time", "targetsteps"}, "date=?", new String[]{currentDateByOffset}, null);
            if (cursorIsNull(cursor)) {
                Log.d("qs_queryDate1", arrayOfInt[j] + "");
            } else {
                cursor.moveToFirst();
                arrayOfInt[j] = cursor.getInt(cursor.getColumnIndexOrThrow("steps"));
                Log.d("qs_queryDate2", arrayOfInt[j] + "");
            }
            if (cursor != null) {
                cursor.close();
            }
            Log.d("qs_weeday", " i: " + i + " j: " + j);
        }
        arrayOfInt[i] = WatchApp.getSteps(this.mContext);
        return arrayOfInt;
    }

    public int[] queryTargetDate(ContentResolver resolver2) {
        int weeday;
        this.mCalendar.setToNow();
        if (this.mCalendar.weekDay == 0) {
            weeday = 7;
        } else {
            weeday = this.mCalendar.weekDay;
        }
        int[] arrayOfInt = new int[weeday];
        int i = weeday - 1;
        for (int j = 0; j <= i; j++) {
            String currentDateByOffset = getCurrentDateByOffset("yyyy-MM-dd", 5, j - i);
            ContentResolver contentResolver = resolver2;
            Cursor cursor = contentResolver.query(Uri.parse(this.queryUri), new String[]{"_id", "date", "steps", "diatance", "kcal", "long_time", "targetsteps"}, "date=?", new String[]{currentDateByOffset}, null);
            if (cursorIsNull(cursor)) {
                Log.d("qs_queryTargetDate1", "cursorIsNull");
            } else {
                cursor.moveToFirst();
                if (cursor.getInt(cursor.getColumnIndexOrThrow("targetsteps")) == 0) {
                    arrayOfInt[j] = 1000;
                } else {
                    arrayOfInt[j] = cursor.getInt(cursor.getColumnIndexOrThrow("targetsteps"));
                }
                Log.d("qs_queryTargetDate2", arrayOfInt[j] + "");
            }
            if (cursor != null) {
                cursor.close();
            }
            Log.d("qs_weeday", " i: " + i + " j: " + j);
        }
        int steps = WatchApp.getSteps(this.mContext);
        int targetSteps = WatchApp.getTargetSteps(this.mContext);
        arrayOfInt[i] = targetSteps;
        Log.d("qs_weeday", " steps: " + steps + " targetSteps: " + targetSteps + " arrayOfInt[i]" + arrayOfInt[i]);
        return arrayOfInt;
    }

    public User getUser() {
        this.user = null;
        String weight = System.getString(this.resolver, "step_user_weight");
        String height = System.getString(this.resolver, "step_user_height");
        String sex = System.getString(this.resolver, "step_user_sex");
        if (weight == null || height == null || sex == null) {
            System.putString(this.resolver, "step_user_weight", "60");
            System.putString(this.resolver, "step_user_height", "175");
            System.putString(this.resolver, "step_user_sex", "1");
        }
        this.user = new User(Integer.parseInt(sex), Double.valueOf(weight).doubleValue(), Double.valueOf(height).doubleValue());
        return this.user;
    }

    public void showUI(boolean anim) {
        if (this.mStepsMeterView.getVisibility() == View.VISIBLE) {
            int[] stepDate = queryStepDate(this.resolver);
            int[] stepTargetDate = queryTargetDate(this.resolver);
            int sum = 0;
            for (int i = 0; i < stepDate.length; i++) {
                Log.d("qs_showUI", "stepDate" + stepDate[i] + "stepTargetDate" + stepTargetDate[i]);
                sum += stepDate[i];
            }
            String distance = CalculatorUtil.getDistance(sum, getUser());
            this.mTvTotall.setText(sum + "");
            this.mTvAverg.setText((sum / stepDate.length) + "");
            this.mTvTotallDis.setText(distance + "");
            if (stepDate != null) {
                String[] level = new String[stepDate.length];
                for (int i2 = 0; i2 < stepDate.length; i2++) {
                    level[i2] = ((int) ((Float.valueOf((float) stepDate[i2]).floatValue() * 100.0f) / ((float) stepTargetDate[i2]))) + "";
                    Log.d("qs_showUI", level[i2]);
                }
                if (this.mStepsMeterView == null) {
                    return;
                }
                if (anim) {
                    this.mStepsMeterView.runAnim(level);
                } else {
                    this.mStepsMeterView.setData(level);
                }
            }
        } else {
            ((Data) this.mData.get(0)).setSteps(WatchApp.getSteps(this.mContext));
            ((Data) this.mData.get(0)).setDistance(WatchApp.getDistance(this.mContext));
            this.mDataAdapter.notifyDataSetChanged();
            int sum2 = 0;
            for (int i3 = 0; i3 < this.mData.size(); i3++) {
                sum2 += ((Data) this.mData.get(i3)).getSteps();
            }
            String distance2 = CalculatorUtil.getDistance(sum2, getUser());
            this.mTvTotall.setText(sum2 + "");
            this.mTvAverg.setText((sum2 / this.mData.size()) + "");
            this.mTvTotallDis.setText(distance2 + "");
        }
    }
}

