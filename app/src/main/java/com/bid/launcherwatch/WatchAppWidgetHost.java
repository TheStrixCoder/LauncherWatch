package com.bid.launcherwatch;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.os.TransactionTooLargeException;
import java.util.ArrayList;

public class WatchAppWidgetHost extends AppWidgetHost {
    Context mContext;
    private final ArrayList<Runnable> mProviderChangeListeners = new ArrayList<>();

    public WatchAppWidgetHost(Context context, int hostId) {
        super(context, hostId);
        this.mContext = context;
    }

    /* access modifiers changed from: protected */
    public AppWidgetHostView onCreateView(Context context, int appWidgetId, AppWidgetProviderInfo appWidget) {
        return super.onCreateView(context, appWidgetId, appWidget);
    }

    public void startListening() {
        try {
            super.startListening();
        } catch (Exception e) {
            if (!(e.getCause() instanceof TransactionTooLargeException)) {
                throw new RuntimeException(e);
            }
        }
    }

    public void stopListening() {
        super.stopListening();
        clearViews();
    }

    /* access modifiers changed from: protected */
    public void onProvidersChanged() {
        for (Runnable callback : this.mProviderChangeListeners) {
            callback.run();
        }
    }
}

