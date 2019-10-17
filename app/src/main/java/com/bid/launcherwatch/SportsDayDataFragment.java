package com.bid.launcherwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SportsDayDataFragment extends Fragment {
    private final String TAG = "SportsStepFragment";
    private boolean hasStarted = false;
    private String mCalories;
    private Context mContext;
    public int mCurrentSteps = 0;
    private StepsMeterView mDisMeterView;
    private TextView mDisText;
    private String mDistance;
    private boolean mIsVisibleToUser = false;
    private StepsMeterView mKcalMeterView;
    private TextView mKcalText;
    private final Receiver mReceiver = new Receiver(this, null);
    private StepsMeterView mStepsMeterView;
    private TextView mStepsText;
    private float mTargetDis;
    private int mTargetKcal;
    private int mTargetSteps;

    private final class Receiver extends BroadcastReceiver {
        /* synthetic */ Receiver(SportsDayDataFragment this$02, Receiver receiver) {
            this();
        }

        private Receiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.sinsoft.action.health.step_count")) {
                SportsDayDataFragment.this.showUI(false);
            }
        }
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.mContext = getActivity();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sports_data_fragment_layout, container, false);
        this.mStepsText = (TextView) view.findViewById(R.id.step_text);
        this.mDisText = (TextView) view.findViewById(R.id.dis_text);
        this.mKcalText = (TextView) view.findViewById(R.id.kcal_text);
        this.mDisMeterView = (StepsMeterView) view.findViewById(R.id.dis_meter);
        this.mKcalMeterView = (StepsMeterView) view.findViewById(R.id.kcal_meter);
        this.mStepsMeterView = (StepsMeterView) view.findViewById(R.id.steps_meter);
        this.mCurrentSteps = WatchApp.getSteps(this.mContext);
        this.mCalories = WatchApp.getCalories(this.mContext);
        this.mDistance = WatchApp.getDistance(this.mContext);
        this.mStepsText.setText("" + this.mCurrentSteps);
        this.mDisText.setText(this.mDistance);
        this.mKcalText.setText(this.mCalories);
        showUI(true);
        return view;
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

    public void showUI(boolean anim) {
        if (this.mIsVisibleToUser) {
            this.mCurrentSteps = WatchApp.getSteps(this.mContext);
            this.mCalories = WatchApp.getCalories(this.mContext);
            this.mDistance = WatchApp.getDistance(this.mContext);
            this.mTargetSteps = WatchApp.getTargetSteps(this.mContext);
            this.mTargetDis = WatchApp.getTargetDistance(this.mContext);
            this.mTargetKcal = WatchApp.getTargetCalories(this.mContext);
            this.mStepsText.setText("" + this.mCurrentSteps);
            this.mDisText.setText(this.mDistance);
            this.mKcalText.setText(this.mCalories);
            if (this.mStepsMeterView != null) {
                int level = (int) ((((float) this.mCurrentSteps) * 100.0f) / ((float) this.mTargetSteps));
                if (anim) {
                    this.mStepsMeterView.runAnim(level);
                } else if (!this.mStepsMeterView.isAnim()) {
                    this.mStepsMeterView.setProgressByLevel(level);
                }
            }
            if (this.mDisMeterView != null) {
                int level2 = (int) ((Float.parseFloat(this.mDistance) * 100.0f) / this.mTargetDis);
                if (anim) {
                    this.mDisMeterView.runAnim(level2);
                } else if (!this.mDisMeterView.isAnim()) {
                    this.mDisMeterView.setProgressByLevel(level2);
                }
            }
            if (this.mKcalMeterView != null) {
                int level3 = (int) ((Float.parseFloat(this.mDistance) * 100.0f) / this.mTargetDis);
                if (anim) {
                    this.mKcalMeterView.runAnim(level3);
                } else if (!this.mKcalMeterView.isAnim()) {
                    this.mKcalMeterView.setProgressByLevel(level3);
                }
            }
        }
    }
}

