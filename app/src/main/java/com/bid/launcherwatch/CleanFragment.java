package com.bid.launcherwatch;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bid.launcherwatch.cleananimation.MovingDotView;

public class CleanFragment extends Fragment {
    private Context mContext;
    public int mCurrentSteps = 0;
    private boolean mIsVisibleToUser = false;
    private MovingDotView mMovingDotView;
    //private StepsMeterView mStepsMeterView = null;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.mContext = getActivity();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.clean_fragment_layout, container, false);
        this.mMovingDotView = (MovingDotView) view.findViewById(R.id.main_movingView);
        this.mMovingDotView.setContext(this.mContext);
        this.mMovingDotView.setMaxDotSpeed(10);
        this.mMovingDotView.setMinDotSpeed(1);
        this.mMovingDotView.setDotsCount(50);
        return view;
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }
}
