package com.bid.launcherwatch;
//R unresolved
public class ClockUtil {
    public static final ClockSet ld_26 = new ClockSet(R.layout.item_26, R.string.clockface70, R.drawable.ld_26_priview);
    public static final ClockSet mClock70 = new ClockSet(R.layout.item71, R.string.clockface70, R.drawable.watch_clock_priview_71);
    public static final ClockSet[] mClockList = new ClockSet[0];
    public static final bgSettingClockSet mCustomDialClock = new bgSettingClockSet(R.layout.custom_dial_clockface, R.string.clockface70, R.drawable.custom_dial_preview_no);

    public static class ClockSet {
        public int mThumbImageId;
        public int mTitleId;
        public int mViewId;

        public ClockSet(int id_view, int id_title, int id_thumb) {
            this.mViewId = id_view;
            this.mTitleId = id_title;
            this.mThumbImageId = id_thumb;

        }
        ClockSet(){
        }
    }

    public static class ClockSetWallpaper extends ClockSet {
        public String PackageName;
        public String ServiceName;
    }

    public static class bgSettingClockSet extends ClockSet {
        public int mThumbImageId;
        public int mTitleId;
        public int mViewId;

        public bgSettingClockSet(int id_view, int id_title, int id_thumb) {
            super(id_view, id_title, id_thumb);
            this.mViewId = id_view;
            this.mTitleId = id_title;
            this.mThumbImageId = id_thumb;
        }
    }
}