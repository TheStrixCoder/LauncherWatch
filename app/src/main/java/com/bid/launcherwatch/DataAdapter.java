package com.bid.launcherwatch;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class DataAdapter extends BaseAdapter {
    private List<Data> data;
    private LayoutInflater mInflater;

    class ViewHolder {
        TextView mDate;
        TextView mDistance;
        TextView mSteps;

        ViewHolder() {
        }
    }

    public DataAdapter(List<Data> paramList, Context paramContext) {
        this.data = paramList;
        this.mInflater = (LayoutInflater) paramContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        if (this.data == null) {
            return 0;
        }
        return this.data.size();
    }

    public boolean isEnabled(int position) {
        return false;
    }

    public Object getItem(int paramInt) {
        if (this.data == null) {
            return null;
        }
        return (Data) this.data.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return (long) paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
        ViewHolder localViewHolder;
        if (paramView == null) {
            localViewHolder = new ViewHolder();
            paramView = this.mInflater.inflate(R.layout.item_layout, null);
            localViewHolder.mDate = (TextView) paramView.findViewById(R.id.detail_date);
            localViewHolder.mDistance = (TextView) paramView.findViewById(R.id.detail_distance);
            localViewHolder.mSteps = (TextView) paramView.findViewById(R.id.detail_steps);
            paramView.setTag(localViewHolder);
        } else {
            localViewHolder = (ViewHolder) paramView.getTag();
        }
        Data localData = (Data) this.data.get(paramInt);
        localViewHolder.mDate.setText(localData.getDate());
        localViewHolder.mSteps.setText(String.valueOf(localData.getSteps()));
        localViewHolder.mDistance.setText(localData.getDistance());
        return paramView;
    }
}

