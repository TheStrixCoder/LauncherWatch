package com.bid.launcherwatch;

import android.service.notification.StatusBarNotification;
import java.util.ArrayList;
import java.util.Comparator;

public class NotificationData {
    private final ArrayList<Entry> mEntries = new ArrayList<>();
    private final Comparator<Entry> mEntryCmp = new Comparator<Entry>() {
        public int compare(Entry a, Entry b) {
            StatusBarNotification na = a.notification;
            StatusBarNotification nb = b.notification;
            if (a.interruption == b.interruption) {
                return (int) (na.getNotification().when - nb.getNotification().when);
            }
            return a.interruption ? 1 : -1;
        }
    };
    private final Comparator<Entry> mEntryPostTimeCmp = new Comparator<Entry>() {
        public int compare(Entry a, Entry b) {
            return (int) (a.notification.getPostTime() - b.notification.getPostTime());
        }
    };

    public static final class Entry {
        /* access modifiers changed from: private */
        public boolean interruption;
        private boolean mIsRead = false;
        public NotificationSubFragment mNotificationSubFragment;
        public StatusBarNotification notification;

        public Entry() {
        }

        public Entry(StatusBarNotification n, NotificationSubFragment mFragment) {
            this.notification = n;
            this.mNotificationSubFragment = mFragment;
        }
    }

    public int size() {
        return this.mEntries.size();
    }

    public Entry get(int i) {
        return (Entry) this.mEntries.get(i);
    }

    public Entry findByKey(String pkgName, String tag, int id) {
        for (Entry e : this.mEntries) {
            if (((tag == null && e.notification.getTag() == null) || (tag != null && tag.equals(e.notification.getTag()))) && pkgName.equals(e.notification.getPackageName()) && id == e.notification.getId()) {
                return e;
            }
        }
        return null;
    }

    public int findPositionByKey(String pkgName, String tag, int id) {
        for (int i = 0; i < this.mEntries.size(); i++) {
            Entry e = (Entry) this.mEntries.get(i);
            if (((tag == null && e.notification.getTag() == null) || (tag != null && tag.equals(e.notification.getTag()))) && pkgName.equals(e.notification.getPackageName()) && id == e.notification.getId()) {
                return i;
            }
        }
        return -1;
    }

    public int add(Entry entry) {
        int N = this.mEntries.size();
        int i = 0;
        while (i < N && this.mEntryPostTimeCmp.compare((Entry) this.mEntries.get(i), entry) >= 0) {
            i++;
        }
        this.mEntries.add(i, entry);
        return i;
    }

    public int update(Entry entry) {
        StatusBarNotification n = entry.notification;
        remove(n.getPackageName(), n.getTag(), n.getId());
        int i = 0;
        while (i < this.mEntries.size() && this.mEntryPostTimeCmp.compare((Entry) this.mEntries.get(i), entry) >= 0) {
            i++;
        }
        this.mEntries.add(i, entry);
        return i;
    }

    public Entry remove(String pkgName, String tag, int id) {
        Entry e = findByKey(pkgName, tag, id);
        if (e != null) {
            this.mEntries.remove(e);
        }
        return e;
    }
}

