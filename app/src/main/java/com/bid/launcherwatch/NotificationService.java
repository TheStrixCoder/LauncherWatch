package com.bid.launcherwatch;



import android.app.Notification;
import android.app.Notification.Builder;
import android.app.Notification.InboxStyle;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import com.bid.launcherwatch.NotificationData.Entry;

public class NotificationService extends NotificationListenerService {
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent != null && "com.bid.launcherwatch.NOTIFICATION_LISTENER.CANCEL".equals(intent.getAction())) {
                Log.i("NotificationListenerService", "**********  ACTION_NOTIFICATION_CANCEL_BY_USER");
                NotificationService.this.removeNotification(intent.getExtras());
            } else if (intent != null && "com.bid.launcherwatch.DEMO_MODE_POST".equals(intent.getAction())) {
                NotificationService.this.dumpData();
                NotificationManager nManager = (NotificationManager) NotificationService.this.getSystemService(Context.NOTIFICATION_SERVICE);
                Builder ncomp = new Builder(context);
                ncomp.setContentTitle("+928005506My Notification");
                ncomp.setContentText("Notification Listener Service Example");
                ncomp.setTicker("Notification Listener Service Example");
                //ncomp.setSmallIcon(17301668);
                ncomp.setAutoCancel(true);
                ncomp.setPriority(Notification.PRIORITY_HIGH);
                ncomp.setWhen(System.currentTimeMillis());
                ncomp.setShowWhen(true);
                nManager.notify((int) System.currentTimeMillis(), new InboxStyle(ncomp).addLine("line11111111111111111111111111111111111111111111111").addLine("line22222222222222222222222222222222222222222222222").addLine("line33333333333333333333333333333333333333333333333").addLine("line44444444444444444444444444444444444444444444444").addLine("line55555555555555555555555555555555555555555555555").addLine("line66666666666666666666666666666666666666666666666").addLine("line77777777777777777777777777777777777777777777777").addLine("line88888888888888888888888888888888888888888888888").addLine("line99999999999999999999999999999999999999999999999").setSummaryText("+9 more").setBigContentTitle("+0928005506Bigg").build());
            }
        }
    };

    public NotificationService() {
        Log.d("NotificationListenerService", "NotificationService start!");
    }

    public void onCreate() {
        super.onCreate();
        Log.i("NotificationListenerService", "**********  onCreate");
        ensureEnabled(getApplicationContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.bid.launcherwatch.NOTIFICATION_LISTENER.CANCEL");
        filter.addAction("com.bid.launcherwatch.DEMO_MODE_POST");
        registerReceiver(this.mReceiver, filter);
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mReceiver);
    }

    private void ensureEnabled(Context context) {
        String existingListeners;
        String settingStr = "enabled_notification_listeners";
        String meFlattened = new ComponentName(context, NotificationService.class).flattenToString();
        String existingListeners2 = Secure.getString(context.getContentResolver(), settingStr);
        Log.i("NotificationListenerService", "ensureEnabled");
        if (TextUtils.isEmpty(existingListeners2)) {
            existingListeners = meFlattened;
        } else if (!existingListeners2.contains(meFlattened)) {
            existingListeners = existingListeners2 + ":" + meFlattened;
        } else {
            return;
        }
        Secure.putString(context.getContentResolver(), settingStr, existingListeners);
    }

    public void onNotificationRemoved(StatusBarNotification sbn) {
    }

    public void onNotificationPosted(StatusBarNotification sbn) {
    }

    /* access modifiers changed from: private */
    public void removeNotification(Bundle mArgs) {
        String mKey = mArgs.getString("key");
        Log.d("NotificationListenerService", "removeNotification Key=" + mKey);
        try {
            cancelNotification(mKey);
        } catch (NullPointerException e) {
            Log.e("NotificationListenerService", "removeNotification NullPointerException.");
        }
    }

    private void dumpData(Entry mEntry) {
        StatusBarNotification mSbn = mEntry.notification;
        Log.d("NotificationListenerService", "notification=" + mSbn.getNotification());
        int id = mSbn.getId();
        String name = mSbn.getPackageName();
        long time = mSbn.getPostTime();
        boolean clearable = mSbn.isClearable();
        boolean playing = mSbn.isOngoing();
        CharSequence text = mSbn.getNotification().tickerText;
        Log.d("NotificationListenerService", "id:" + id + " name:" + name + " time:" + time);
        Log.d("NotificationListenerService", "isClearable:" + clearable + " isOngoing:" + playing + " tickerText:" + text);
        NotificationHelper.dumpNotification(mSbn.getNotification());
    }

    /* access modifiers changed from: private */
    public void dumpData() {
        NotificationData mNotificationData = NotificationHelper.getNotificationData();
        int N = mNotificationData.size();
        Log.d("NotificationListenerService", "  notification icons: " + N);
        for (int i = 0; i < N; i++) {
            Entry e = mNotificationData.get(i);
            Log.d("NotificationListenerService", "    [" + i + "]");
            dumpData(e);
        }
    }
}
