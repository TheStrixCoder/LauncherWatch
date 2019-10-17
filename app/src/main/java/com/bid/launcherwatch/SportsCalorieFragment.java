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

public class SportsCalorieFragment extends Fragment {
    private final String TAG = "SportsStepFragment";
    private TextView mCalorieText;
    private Context mContext;
    public String mCurrentCal = "0";
    private boolean mIsVisibleToUser = false;
    private final Receiver mReceiver = new Receiver(this, null);
    private StepsMeterView mStepsMeterView = null;

    private final class Receiver extends BroadcastReceiver {
        /* synthetic */ Receiver(SportsCalorieFragment this$02, Receiver receiver) {
            this();
        }

        private Receiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.sinsoft.action.health.step_count")) {
                SportsCalorieFragment.this.showUI(false);
            }
        }
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.mContext = getActivity();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sports_calorie_fragment_layout, container, false);
        this.mCalorieText = (TextView) view.findViewById(R.id.calorie_text);
        this.mCurrentCal = WatchApp.getCalories(this.mContext);
        this.mCalorieText.setText(this.mCurrentCal);
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
            this.mCurrentCal = WatchApp.getCalories(this.mContext);
            this.mCalorieText.setText(WatchApp.getCalories(this.mContext));
            if (this.mStepsMeterView != null) {
                int level = (int) ((Float.valueOf(this.mCurrentCal).floatValue() * 100.0f) / ((float) WatchApp.getTargetCalories(this.mContext)));
                if (anim) {
                    this.mStepsMeterView.runAnim(level);
                } else if (!this.mStepsMeterView.isAnim()) {
                    this.mStepsMeterView.setProgressByLevel(level);
                }
            }
        }
    }
}

