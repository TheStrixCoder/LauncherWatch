package com.bid.launcherwatch.online;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import com.bid.launcherwatch.R;
import java.util.ArrayList;
import java.util.List;

public class ClockSkinOnlineListViewAdapter extends CursorAdapter {
    private List<OnlineClockSkinLocalNode> mCache = new ArrayList();
    private Context mContext;
    private LayoutInflater mInflater;

    public ClockSkinOnlineListViewAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mCache.clear();
    }

    public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
        return this.mInflater.inflate(R.layout.font_online_listview, arg2, false);
    }

    public void bindView(View view, Context context, Cursor cursor) {
        int targetPos = 0;
        int currPos = cursor.getPosition();
        int last = cursor.getCount() - 1;
        if (currPos != 0 && currPos != last) {
            targetPos = 2;
        } else if (currPos == 0) {
            targetPos = 1;
        } else if (currPos == last) {
            targetPos = 3;
        }
        ((ClockSkinOnlineListItem) view).bind(context, new OnlineClockSkinLocalNode(this.mContext, cursor), targetPos);
    }

    public void recycleCache() {
        if (this.mCache != null && this.mCache.size() > 0) {
            for (OnlineClockSkinLocalNode node : this.mCache) {
                Bitmap bitmap = node.getBmpFirst();
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        }
    }
}
