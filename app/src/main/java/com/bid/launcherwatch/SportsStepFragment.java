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

public class SportsStepFragment extends Fragment {
    private final String TAG = "SportsStepFragment";
    private Context mContext;
    public int mCurrentSteps = 0;
    private boolean mIsVisibleToUser = false;
    private final Receiver mReceiver = new Receiver(this, null);
    private StepsMeterView mStepsMeterView = null;
    private TextView mStepsText;

    private final class Receiver extends BroadcastReceiver {
        /* synthetic */ Receiver(SportsStepFragment this$02, Receiver receiver) {
            this();
        }

        private Receiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.sinsoft.action.health.step_count")) {
                SportsStepFragment.this.showUI(false);
            }
        }
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.mContext = getActivity();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sports_step_fragment_layout, container, false);
        this.mStepsText = (TextView) view.findViewById(R.id.step_text);
        this.mCurrentSteps = WatchApp.getSteps(this.mContext);
        this.mStepsText.setText("" + this.mCurrentSteps);
        this.mStepsMeterView = (StepsMeterView) view.findViewById(R.id.steps_meter);
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
                registerStepsCntReceiver();
                showUI(true);
            } else {
                unRegisterStepsCntReceiver();
                this.mStepsMeterView.cleanAnim();
            }
        }
    }

    public void showUI(boolean anim) {
        if (this.mIsVisibleToUser) {
            this.mCurrentSteps = WatchApp.getSteps(this.mContext);
            this.mStepsText.setText("" + this.mCurrentSteps);
            if (this.mStepsMeterView != null) {
                int level = (int) ((((float) this.mCurrentSteps) * 100.0f) / ((float) WatchApp.getTargetSteps(this.mContext)));
                if (anim) {
                    this.mStepsMeterView.runAnim(level);
                } else if (!this.mStepsMeterView.isAnim()) {
                    this.mStepsMeterView.setProgressByLevel(level);
                }
            }
        }
    }
}

