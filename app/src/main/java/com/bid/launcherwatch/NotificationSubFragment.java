package com.bid.launcherwatch;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.DateTimeView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bid.launcherwatch.NotificationScrollView.Callback;
import java.util.Date;

public class NotificationSubFragment extends Fragment implements Callback {
    /* access modifiers changed from: private */
    public static final String TAG = NotificationSubFragment.class.getSimpleName();
    private Button mAction1;
    private Button mAction2;
    private Button mAction3;
    private View mBackgroundView;
    private Chronometer mChronometer;
    private Context mContext;
    private Button mDismiss;
    private ImageView mIconNoteType;
    private ImageView mIconView;
    private int mId;
    private String mKey;
    private Button mOpen;
    private ImageButton mOptions;
    private String mPkgName;
    private ViewGroup mRootView;
    private StatusBarNotification mSbn;
    private NotificationScrollView mScrollView;
    private String mTag;
    private TextView mTextNoteType;
    private TextView mTextView;
    private DateTimeView mTime;
    private TextView mTitleView;

    public static NotificationSubFragment create(StatusBarNotification mSbn2) {
        NotificationSubFragment fragment = new NotificationSubFragment();
        fragment.setContent(mSbn2);
        return fragment;
    }

    public void setContent(StatusBarNotification mSbn2) {
        this.mSbn = mSbn2;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mRootView = (ViewGroup) inflater.inflate(R.layout.notification_content_layout, container, false);
        this.mScrollView = (NotificationScrollView) this.mRootView.findViewById(R.id.card_scroll_view);
        this.mBackgroundView = this.mRootView.findViewById(R.id.card_background);
        this.mIconView = (ImageView) this.mRootView.findViewById(R.id.icon);
        this.mTitleView = (TextView) this.mRootView.findViewById(R.id.title);
        this.mTextView = (TextView) this.mRootView.findViewById(R.id.text);
        this.mOptions = (ImageButton) this.mRootView.findViewById(R.id.options);
        this.mAction1 = (Button) this.mRootView.findViewById(R.id.action1);
        this.mAction2 = (Button) this.mRootView.findViewById(R.id.action2);
        this.mAction3 = (Button) this.mRootView.findViewById(R.id.action3);
        this.mOpen = (Button) this.mRootView.findViewById(R.id.open);
        this.mDismiss = (Button) this.mRootView.findViewById(R.id.close);
        this.mTime = (DateTimeView) this.mRootView.findViewById(R.id.time);
        this.mChronometer = (Chronometer) this.mRootView.findViewById(R.id.chronometer);
        this.mIconNoteType = (ImageView) this.mRootView.findViewById(R.id.type_icon);
        this.mTextNoteType = (TextView) this.mRootView.findViewById(R.id.type_text);
        this.mScrollView.setCallback(this);
        this.mOptions.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                NotificationSubFragment.this.expandLayout();
            }
        });
        this.mDismiss.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                NotificationSubFragment.this.requestDismiss();
            }
        });
        if (this.mSbn == null) {
            onRestoreInstanceState(savedInstanceState);
        }
        refreshContent();
        return this.mRootView;
    }

    private void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String pkgName = savedInstanceState.getString("package_name");
            String tag = savedInstanceState.getString("tag");
            int id = savedInstanceState.getInt("id");
            NotificationData nData = NotificationHelper.getNotificationData();
            int nPos = nData.findPositionByKey(pkgName, tag, id);
            if (nPos != -1) {
                setContent(nData.get(nPos).notification);
            }
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("package_name", this.mPkgName);
        outState.putString("tag", this.mTag);
        outState.putInt("id", this.mId);
    }

    public void handleSwipe(View view) {
        if (this.mContext != null) {
            this.mContext.sendBroadcast(new Intent("com.bid.launcherwatch.NOTIFICATION_LISTENER.CANCEL").putExtra("key", this.mKey));
        }
        try {
            if (this.mSbn.getNotification().deleteIntent != null) {
                this.mSbn.getNotification().deleteIntent.send();
            }
        } catch (CanceledException e) {
            Log.w(TAG, "Delete intent, PendingIntent.CanceledException");
        }
        this.mRootView.setVisibility(View.GONE);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
    }

    public void expandLayout() {
        if (isCollapse()) {
            this.mOptions.setVisibility(View.GONE);
            if (this.mTextView != null) {
                this.mTextView.setMaxLines(15);
                Bundle mArgs = NotificationCompat.getExtras(this.mSbn.getNotification());
                if (NotificationHelper.getText(mArgs) != null) {
                    this.mTextView.setText(NotificationHelper.getText(mArgs));
                } else {
                    loadAndSetBigText(this.mSbn.getNotification(), this.mTextView, false);
                }
            }
            setActionsVisibility();
        }
    }

    public void collapseLayout() {
        if (!isCollapse()) {
            this.mOptions.setVisibility(View.VISIBLE);
            loadAndSetContentText(this.mSbn.getNotification());
            this.mAction1.setVisibility(View.GONE);
            this.mAction2.setVisibility(View.GONE);
            this.mAction3.setVisibility(View.GONE);
            this.mOpen.setVisibility(View.GONE);
            this.mDismiss.setVisibility(View.GONE);
        }
    }

    private boolean isCollapse() {
        return this.mOptions.getVisibility() == View.VISIBLE;
    }

    /* access modifiers changed from: private */
    public void requestDismiss() {
        this.mScrollView.dismissChild(this.mScrollView);
    }

    private void loadAndSetIcon(Notification mNotification, ImageView mView) {
        if (mView != null) {
            Bitmap remoteAppIcon = NotificationHelper.getAppIcon(NotificationCompat.getExtras(mNotification));
            Drawable localAppIcon = null;
            if (remoteAppIcon == null && this.mContext != null) {
                try {
                    localAppIcon = this.mContext.getPackageManager().getApplicationIcon(this.mPkgName);
                } catch (NameNotFoundException localNameNotFoundException) {
                    Log.e(TAG, "Package name not found.", localNameNotFoundException);
                }
            }
            if (remoteAppIcon != null) {
                mView.setImageBitmap(remoteAppIcon);
            } else if (localAppIcon != null) {
                mView.setImageDrawable(localAppIcon);
            }
        }
    }

    private void loadAndSetTime(Notification mNotification) {
        long mWhen = 0;
        if (this.mSbn != null) {
            mWhen = this.mSbn.getPostTime();
        }
        Log.d(TAG, "when: " + new Date(mWhen));
        if (mWhen == 0) {
            this.mChronometer.setVisibility(View.GONE);
            this.mTime.setVisibility(4);
        } else if (NotificationHelper.getShowChronometer(NotificationCompat.getExtras(mNotification))) {
            if (this.mChronometer != null) {
                this.mChronometer.setVisibility(View.VISIBLE);
                this.mChronometer.setBase((SystemClock.elapsedRealtime() - System.currentTimeMillis()) + mWhen);
                this.mChronometer.start();
            }
        } else if (this.mTime != null) {
            this.mTime.setVisibility(0);
            this.mTime.setTime(mWhen);
        }
    }

    private void loadAndSetActions(Notification mNotification) {
        new WearableExtender(mNotification);
        this.mAction1.setTag("INVISIBLE");
        this.mAction2.setTag("INVISIBLE");
        this.mAction3.setTag("INVISIBLE");
        int mSize = NotificationCompat.getActionCount(mNotification);
        Log.d(TAG, "loadAndSetActions from NotificationCompat");
        if (mSize >= 1) {
            loadAndSetActions(this.mAction1, NotificationCompat.getAction(mNotification, 0));
        }
        if (mSize >= 2) {
            loadAndSetActions(this.mAction2, NotificationCompat.getAction(mNotification, 1));
        }
        if (mSize >= 3) {
            loadAndSetActions(this.mAction3, NotificationCompat.getAction(mNotification, 2));
        }
        loadAndSetContentIntent(this.mOpen, mNotification);
    }

    private void loadAndSetActions(Button mView, final Action mLocalAction) {
        if (mView != null) {
            mView.setText(mLocalAction.title);
            mView.setTag("VISIBLE");
            mView.setOnClickListener(new OnClickListener() {
                public void onClick(View paramAnonymousView) {
                    try {
                        Log.d(NotificationSubFragment.TAG, "Send actionIntent");
                        if (mLocalAction.actionIntent != null) {
                            mLocalAction.actionIntent.send();
                        }
                    } catch (CanceledException e) {
                        Log.w(NotificationSubFragment.TAG, "PendingIntent.CanceledException");
                    }
                }
            });
        }
    }

    private void loadAndSetContentIntent(Button mView, final Notification mNotification) {
        if (mView != null && mNotification.contentIntent != null) {
            mView.setTag("VISIBLE");
            mView.setOnClickListener(new OnClickListener() {
                public void onClick(View paramAnonymousView) {
                    try {
                        Log.d(NotificationSubFragment.TAG, "Send contentIntent");
                        mNotification.contentIntent.send();
                        if ((mNotification.flags & 16) == 16 && (mNotification.flags & 64) == 0) {
                            NotificationSubFragment.this.requestDismiss();
                        }
                    } catch (CanceledException e) {
                        Log.w(NotificationSubFragment.TAG, "PendingIntent.CanceledException");
                    }
                }
            });
        }
    }

    private void setActionsVisibility() {
        String mTag1 = (String) this.mAction1.getTag();
        String mTag2 = (String) this.mAction2.getTag();
        String mTag3 = (String) this.mAction3.getTag();
        String mTagOpen = (String) this.mOpen.getTag();
        Log.d(TAG, "setActionsVisibility");
        if (mTag1 == null || !mTag1.equals("VISIBLE")) {
            this.mAction1.setVisibility(View.GONE);
        } else {
            this.mAction1.setVisibility(View.VISIBLE);
        }
        if (mTag2 == null || !mTag2.equals("VISIBLE")) {
            this.mAction2.setVisibility(View.GONE);
        } else {
            this.mAction2.setVisibility(View.VISIBLE);
        }
        if (mTag3 == null || !mTag3.equals("VISIBLE")) {
            this.mAction3.setVisibility(View.GONE);
        } else {
            this.mAction3.setVisibility(View.VISIBLE);
        }
        if (mTagOpen != null && mTagOpen.equals("VISIBLE")) {
            this.mOpen.setVisibility(View.VISIBLE);
        }
        if (this.mDismiss != null) {
            this.mDismiss.setVisibility(View.VISIBLE);
        }
    }

    private void loadAndSetContentText(Notification mNotification) {
        Bundle mArgs = NotificationCompat.getExtras(mNotification);
        if (this.mTitleView != null) {
            this.mTitleView.setText(NotificationHelper.getTitle(mArgs));
        }
        if (this.mTextView != null) {
            this.mTextView.setMaxLines(3);
            if (NotificationHelper.getText(mArgs) != null) {
                this.mTextView.setText(NotificationHelper.getText(mArgs));
            } else {
                loadAndSetBigText(mNotification, this.mTextView, false);
            }
        }
    }

    private void loadAndSetBigText(Notification mNotif, TextView mView, boolean mAdjustLayout) {
        if (mView != null) {
            CharSequence[] mChar = NotificationHelper.getTextLines(NotificationCompat.getExtras(mNotif));
            if (mChar != null) {
                mView.setText("");
                if (mAdjustLayout) {
                    mView.setMaxLines(8);
                }
                for (int n = 0; n < mChar.length; n++) {
                    if (mChar[n].length() < 20) {
                        mView.append(mChar[n] + "\n");
                    } else {
                        mView.append(mChar[n].subSequence(0, 17) + "...\n");
                    }
                }
            }
        }
    }

    private void loadAndSetKey() {
        if (this.mSbn != null) {
            this.mKey = this.mSbn.getKey();
            this.mPkgName = this.mSbn.getPackageName();
            this.mTag = this.mSbn.getTag();
            this.mId = this.mSbn.getId();
            return;
        }
        Log.d(TAG, "loadAndSetKey failed, pkg=" + this.mPkgName + ", tag=" + this.mTag + ", id=" + this.mId);
    }

    public void refreshContent() {
        loadAndSetKey();
        if (this.mRootView != null && this.mSbn != null) {
            Notification mNotif = this.mSbn.getNotification();
            String pkg = this.mSbn.getPackageName();
            boolean isFromPhone = false;
            if (pkg.equals("com.mediatek.wearable") || pkg.equals("com.weitetech.smartconnect.wiiwearsdk") || pkg.equals("com.weite.smartconnect.wiiwearsdk") || pkg.equals("com.wiite.devicedaemon")) {
                isFromPhone = true;
            }
            if (isFromPhone) {
                this.mIconNoteType.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.type_phone));
                this.mTextNoteType.setText(this.mContext.getResources().getString(R.string.type_phone));
            } else {
                this.mIconNoteType.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.type_watch));
                this.mTextNoteType.setText(this.mContext.getResources().getString(R.string.type_watch));
            }
            loadAndSetActions(mNotif);
            loadAndSetTime(mNotif);
            loadAndSetIcon(mNotif, this.mIconView);
            loadAndSetContentText(mNotif);
            if (!isCollapse()) {
                setActionsVisibility();
            }
        }
    }
}

