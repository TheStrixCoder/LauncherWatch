package com.bid.launcherwatch;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WeatherFragment extends Fragment {
    ComponentName WidgetComponent = new ComponentName("com.android.watchweather", "com.android.watchweather.WeatherAppWidget");
    private Context mContext;
    private AppWidgetProviderInfo mInfo = null;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.mContext = getActivity();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_fragment_layout, container, false);
        View widgetHostView = getWidgetHostView();
        if (widgetHostView != null) {
            return widgetHostView;
        }
        return view;
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    private AppWidgetHostView getWidgetHostView() {
        MainActivity watch = (MainActivity) this.mContext;
        AppWidgetManager manager = watch.getWatchWidgetManager();
        WatchAppWidgetHost host = watch.getWatchWidgetHost();
        for (AppWidgetProviderInfo info : manager.getInstalledProviders()) {
            if (info.provider.equals(this.WidgetComponent)) {
                this.mInfo = info;
            }
        }
        if (this.mInfo == null) {
            return null;
        }
        int widgetId = host.allocateAppWidgetId();
        AppWidgetHostView view = host.createView(this.mContext, widgetId, this.mInfo);
        view.setAppWidget(widgetId, this.mInfo);
        manager.bindAppWidgetIdIfAllowed(widgetId, this.WidgetComponent);
        return view;
    }
}
