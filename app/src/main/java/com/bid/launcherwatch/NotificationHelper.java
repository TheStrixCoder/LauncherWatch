package com.bid.launcherwatch;

import android.app.Notification;
import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Action;
import android.util.Log;
import com.bid.launcherwatch.NotificationData.Entry;
import java.util.Date;

public class NotificationHelper {
    public static NotificationData mNotificationData = new NotificationData();

    private NotificationHelper() {
    }

    public static int add(Entry entry) {
        return mNotificationData.add(entry);
    }

    public static int update(Entry entry) {
        return mNotificationData.update(entry);
    }

    public static Entry remove(String pkgName, String tag, int id) {
        Entry entry = mNotificationData.remove(pkgName, tag, id);
        if (entry == null) {
            Log.w("NotificationHelper", "removeNotificationEntry failed, packageName=" + pkgName + " mTag=" + tag + " id=" + id);
        }
        return entry;
    }

    public static int findPositionByKey(String pkgName, String tag, int id) {
        return mNotificationData.findPositionByKey(pkgName, tag, id);
    }

    public static NotificationData getNotificationData() {
        return mNotificationData;
    }

    public static boolean filterNotification(Notification n) {
        if ((getFlags(n) & 34) == 0) {
            return false;
        }
        return true;
    }

    public static boolean isHighPriorityNotification(Notification n) {
        return getPriority(n) > 0;
    }

    public static void dumpNotification(Notification n) {
        Bundle localBundle = NotificationCompat.getExtras(n);
        Log.d("NotificationHelper", "title: " + getTitle(localBundle));
        Log.d("NotificationHelper", "text: " + getText(localBundle));
        Log.d("NotificationHelper", "subText: " + getSubText(localBundle));
        Log.d("NotificationHelper", "when: " + (getWhen(n) != 0 ? new Date(getWhen(n)) : Integer.valueOf(0)));
        Log.d("NotificationHelper", "priority: " + getPriority(n));
        Log.d("NotificationHelper", "showChronometer: " + getShowChronometer(localBundle));
        Log.d("NotificationHelper", "defaults: 0x" + Integer.toHexString(getDefaults(n)));
        Log.d("NotificationHelper", "flags: 0x" + Integer.toHexString(getFlags(n)));
        Log.d("NotificationHelper", "has sound" + getSound(n));
        Log.d("NotificationHelper", "vibrate: " + getVibrate(n));
        Bitmap bm = getLargeIcon(localBundle);
        if (bm != null) {
            Log.d("NotificationHelper", "large icon: " + bm.getWidth() + "x" + bm.getHeight());
        }
        Bitmap bm2 = getPicture(localBundle);
        if (bm2 != null) {
            Log.d("NotificationHelper", "big picture: " + bm2.getWidth() + "x" + bm2.getHeight());
        }
        CharSequence[] cs = getTextLines(localBundle);
        if (cs != null) {
            Log.d("NotificationHelper", "inbox style with " + cs.length + " lines");
            for (int i = 0; i < cs.length; i++) {
                Log.d("NotificationHelper", "line: " + cs[i]);
            }
        }
        int count = getActionCount(n);
        Log.d("NotificationHelper", "action count = " + count);
        if (count > 0) {
            for (int i2 = 0; i2 < count; i2++) {
                Log.d("NotificationHelper", "action " + i2 + ": " + getAction(n, i2));
            }
        }
        Log.d("NotificationHelper", "content Intent: " + getContentIntent(n));
        Log.d("NotificationHelper", "local only: " + getLocalOnly(n));
    }

    public static int getActionCount(Notification n) {
        return NotificationCompat.getActionCount(n);
    }

    public static Action getAction(Notification n, int index) {
        return NotificationCompat.getAction(n, index);
    }

    public static long getWhen(Notification n) {
        return n.when;
    }

    public static int getPriority(Notification n) {
        return n.priority;
    }

    public static int getDefaults(Notification n) {
        return n.defaults;
    }

    public static int getFlags(Notification n) {
        return n.flags;
    }

    public static boolean isDefaultVibrate(Notification n) {
        return (n.defaults & 2) != 0;
    }

    public static Uri getSound(Notification n) {
        return n.sound;
    }

    public static PendingIntent getContentIntent(Notification n) {
        return n.contentIntent;
    }

    public static boolean getLocalOnly(Notification n) {
        return NotificationCompat.getLocalOnly(n);
    }

    public static long[] getVibrate(Notification n) {
        return n.vibrate;
    }

    public static CharSequence getTitle(Bundle args) {
        return args.getCharSequence("android.title");
    }

    public static CharSequence getText(Bundle args) {
        return args.getCharSequence("android.text");
    }

    public static CharSequence getSubText(Bundle args) {
        return args.getCharSequence("android.subText");
    }

    public static boolean getShowChronometer(Bundle args) {
        return args.getBoolean("android.showChronometer");
    }

    public static Bitmap getAppIcon(Bundle args) {
        return (Bitmap) args.getParcelable("app_icon");
    }

    public static Bitmap getLargeIcon(Bundle args) {
        return (Bitmap) args.getParcelable("android.largeIcon");
    }

    public static Bitmap getPicture(Bundle args) {
        return (Bitmap) args.getParcelable("android.picture");
    }

    public static CharSequence[] getTextLines(Bundle args) {
        return args.getCharSequenceArray("android.textLines");
    }
}
