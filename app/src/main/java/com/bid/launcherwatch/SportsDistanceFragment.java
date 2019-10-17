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

public class SportsDistanceFragment extends Fragment {
    private final String TAG = "SportsStepFragment";
    private boolean hasStarted = false;
    private Context mContext;
    public String mCurrentDistance;
    private TextView mDistanceText;
    private boolean mIsVisibleToUser = false;
    private final Receiver mReceiver = new Receiver(this, null);
    private StepsMeterView mStepsMeterView = null;

    private final class Receiver extends BroadcastReceiver {
        /* synthetic */ Receiver(SportsDistanceFragment this$02, Receiver receiver) {
            this();
        }

        private Receiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.sinsoft.action.health.step_count")) {
                SportsDistanceFragment.this.showUI(false);
            }
        }
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.mContext = getActivity();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sports_distance_fragment_layout, container, false);
        this.mDistanceText = (TextView) view.findViewById(R.id.distance_text);
        this.mCurrentDistance = WatchApp.getDistance(this.mContext);
        this.mDistanceText.setText(this.mCurrentDistance);
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
            this.mCurrentDistance = WatchApp.getDistance(this.mContext);
            this.mDistanceText.setText(this.mCurrentDistance);
            if (this.mStepsMeterView != null) {
                int level = (int) ((Float.valueOf(this.mCurrentDistance).floatValue() * 100.0f) / WatchApp.getTargetDistance(this.mContext));
                if (anim) {
                    this.mStepsMeterView.runAnim(level);
                } else if (!this.mStepsMeterView.isAnim()) {
                    this.mStepsMeterView.setProgressByLevel(level);
                }
            }
        }
    }
}

