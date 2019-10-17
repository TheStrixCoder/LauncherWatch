package com.bid.launcherwatch.online;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.bid.launcherwatch.R;
import com.bid.launcherwatch.Util;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ClockSkinOnlineActivity extends Activity {
    /* access modifiers changed from: private */
    public static String TAG = "online";
    private final int ADDSTEP = 5;
    private int DownListSize = 0;
    private ClockSkinOnlineListViewAdapter adapter;
    private Cursor cur;
    private int currCount = 0;
    Cursor installedCursor;
    private OnScrollListener listener = new OnScrollListener() {
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (view.getLastVisiblePosition() == view.getCount() - 1 && ClockSkinOnlineActivity.this.mFootLoadingView != null && scrollState == 0) {
                Cursor countCur = ClockSkinOnlineActivity.this.mDb.rawQuery("select * from clockskin_online_list WHERE state = 0", null);
                int size = countCur.getCount();
                countCur.close();
                Log.d(ClockSkinOnlineActivity.TAG, " countCur.size() ==" + size);
                if (view.getCount() != size + 1) {
                    ClockSkinOnlineActivity.this.initData();
                } else if (!ClockSkinOnlineActivity.this.mUpdatLoading) {
                    ClockSkinOnlineActivity.this.mUpdatLoading = true;
                    if (ClockSkinOnlineActivity.this.mFootLoadingView != null) {
                    }
                    ClockSkinOnlineActivity.this.startDownloadToDb(5);
                }
            }
        }

        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        }
    };
    /* access modifiers changed from: private */
    public boolean mCancelAllTask = false;
    /* access modifiers changed from: private */
    public SQLiteDatabase mDb;
    private ClockSkinDBHelper mDbHelper;
    private BroadcastReceiver mDownloadStateReceiver = null;
    /* access modifiers changed from: private */
    public View mFootLoadingView;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(ClockSkinOnlineActivity.this, ClockSkinOnlineActivity.this.getString(R.string.alert_info_net_unvaliable), Toast.LENGTH_SHORT).show();
                    return;
                case 1:
                    ClockSkinOnlineActivity.this.showDialog(R.string.alert_title, R.string.alert_info_net_req_timeout);
                    return;
                case 2:
                    ClockSkinOnlineActivity.this.mDb.delete("clockskin_online_list", null, null);
                    ClockSkinOnlineActivity.this.delete(new File("/data/data/com.bid.launcherwatch//WiiwearClockSkin/zip/"));
                    ClockSkinOnlineActivity.this.startUpdate();
                    return;
                case 3:
                    ClockSkinOnlineActivity.this.initData();
                    ClockSkinOnlineActivity.this.dismissOnlineLoadingDialog();
                    return;
                case 4:
                    ClockSkinOnlineActivity.this.endUpdate();
                    ClockSkinOnlineActivity.this.dismissOnlineLoadingDialog();
                    return;
                case 6:
                    ClockSkinOnlineActivity.this.startUpdate();
                    return;
                default:
                    return;
            }
        }
    };
    private final ArrayList<String> mInstalled = new ArrayList<>();
    private ListView mListView;
    /* access modifiers changed from: private */
    public TextView mProgress;
    private Dialog mProgressDialog = null;
    /* access modifiers changed from: private */
    public boolean mUpdatLoading = false;

    private class CheckThread extends Thread {
        /* synthetic */ CheckThread(ClockSkinOnlineActivity this$02, CheckThread checkThread) {
            this();
        }

        private CheckThread() {
        }

        public void run() {
            if (NetHelper.checkFontIsNeedUpdate(ClockSkinOnlineActivity.this)) {
                Log.e("xxxx", "MSG_NET_NEED_UPDATE");
                NetHelper.sendMsg(2);
            } else if (ClockSkinOnlineActivity.this.getOnlineClockSkinCount() >= 2 || ClockSkinOnlineActivity.this.getAllClockSkinCount() >= NetHelper.getOnlineThemeListSize()) {
                Log.e("xxxx", "MSG_NET_NONEED_UPDATE");
                NetHelper.sendMsg(3);
            } else {
                NetHelper.sendMsg(6);
            }
        }
    }

    private class UpdateTask extends AsyncTask<Void, Integer, Void> {
        private int mAddStep;
        private List<OnlineClockSkinXMLNode> mList;

        public UpdateTask(List<OnlineClockSkinXMLNode> list, int addstep) {
            this.mList = list;
            this.mAddStep = addstep;
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... params) {
            int downloadCount = 0;
            for (OnlineClockSkinXMLNode item : this.mList) {
                if (!ClockSkinOnlineActivity.this.checkIsPackageExist(item.getSkinId())) {
                    if (ClockSkinOnlineActivity.this.mCancelAllTask) {
                        return null;
                    }
                    if (downloadCount >= this.mAddStep) {
                        NetHelper.sendMsg(4);
                        return null;
                    } else if (ClockSkinOnlineActivity.this.checkIsClockInstalled(item.getSkinId())) {
                        ClockSkinOnlineActivity.this.putinDatabaseWhitState(item, null, null, 1);
                    } else {
                        downloadCount++;
                        Log.d(ClockSkinOnlineActivity.TAG, "in uploadThread run()  loading   : " + downloadCount + " th item...");
                        InputStream isFirst = NetHelper.fetchOnlineFile(item.getPreview());
                        if (isFirst == null) {
                            NetHelper.sendMsg(0);
                            return null;
                        }
                        try {
                            ClockSkinOnlineActivity.this.saveFile(BitmapFactory.decodeStream(isFirst), item.getSkinId() + ".png");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            isFirst.close();
                        } catch (IOException e2) {
                            Log.e(ClockSkinOnlineActivity.TAG, "ERROR EXCEPTION: " + e2.toString());
                        }
                        ClockSkinOnlineActivity.this.putinDatabase(item, null, null);
                    }
                }
            }
            NetHelper.sendMsg(4);
            return null;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mCancelAllTask = false;
        Util.addActivity(this);
        showOnlineLoadingDialog();
        this.mDbHelper = new ClockSkinDBHelper(this, "clockskin.db", null, 1);
        this.mDb = this.mDbHelper.getReadableDatabase();
        this.installedCursor = this.mDb.rawQuery("select * from clockskin_install_list", null);
        this.installedCursor.moveToFirst();
        if (this.installedCursor.getCount() > 0) {
            do {
                this.mInstalled.add(this.installedCursor.getString(this.installedCursor.getColumnIndex("skinid")));
            } while (this.installedCursor.moveToNext());
        }
        setContentView(R.layout.online_listview);
        this.mListView = (ListView) findViewById(R.id.online_listView);
        this.mListView.setCacheColorHint(0);
        this.mProgress = (TextView) findViewById(R.id.progress_button);
        this.mProgress.setVisibility(View.GONE);
        this.mProgress.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ClockSkinOnlineActivity.this.sendBroadcast(new Intent("STOP_FONT_DOWNLOAD_SERVICE"));
            }
        });
        if (isServiceRunning()) {
            this.mProgress.setVisibility(View.VISIBLE);
        }
        this.mFootLoadingView = View.inflate(this, R.layout.dragdrop_progressbar, null);
        this.mListView.addFooterView(this.mFootLoadingView, null, false);
        NetHelper.initHandler(this.mHandler);
        Log.i(TAG, "NetHelper inited ..");
        initReceiver();
        if (!NetHelper.checkNetIsOk(this)) {
            Toast.makeText(this, getString(R.string.alert_info_net_unvaliable), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        new CheckThread(this, null).start();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        this.mCancelAllTask = true;
        Util.removeActivity(this);
        this.installedCursor.close();
        unregisterReceiver(this.mDownloadStateReceiver);
        dismissOnlineLoadingDialog();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }

    private void initReceiver() {
        this.mDownloadStateReceiver = new BroadcastReceiver() {
            public void onReceive(Context arg0, Intent arg1) {
                switch (arg1.getIntExtra("BROADCAST_DOWNLOAD_STATE", -1)) {
                    case 1001:
                        ClockSkinOnlineActivity.this.showDownloadFailAlert(R.string.font_alert_title_download_fail, R.string.font_alert_info_net_exception);
                        return;
                    case 1002:
                        ClockSkinOnlineActivity.this.showDownloadFailAlert(R.string.font_alert_title_download_fail, R.string.font_alert_info_server_exception);
                        return;
                    case 1003:
                        ClockSkinOnlineActivity.this.initData();
                        ClockSkinOnlineActivity.this.startDownloadToDb(1);
                        return;
                    case 1004:
                        int progress = arg1.getIntExtra("progress", -1);
                        if (progress == -1) {
                            ClockSkinOnlineActivity.this.mProgress.setVisibility(View.GONE);
                        } else {
                            ClockSkinOnlineActivity.this.mProgress.setVisibility(View.VISIBLE);
                        }
                        ClockSkinOnlineActivity.this.mProgress.setText(progress + "%");
                        return;
                    default:
                        return;
                }
            }
        };
        registerReceiver(this.mDownloadStateReceiver, new IntentFilter("BROADCAST_DOWNLOAD_STATE_FILTER"));
    }

    /* access modifiers changed from: private */
    public void showDownloadFailAlert(int titleResId, int contentResId) {
        Builder builder = new Builder(this);
        builder.setCancelable(false);
        final AlertDialog dlg = builder.create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.simple_alert_dialog);
        TextView content = (TextView) window.findViewById(R.id.content);
        ((TextView) window.findViewById(R.id.title)).setText(titleResId);
        content.setText(contentResId);
        ((Button) window.findViewById(R.id.alertok)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
    }

    /* access modifiers changed from: private */
    public void initData() {
        Log.e(TAG, "in initData() ..");
        int size = getOnlineClockSkinCount();
        int sizeAll = getAllClockSkinCount();
        if (size == 0 && sizeAll == NetHelper.getOnlineThemeListSize()) {
            Toast.makeText(this, getString(R.string.all_have_installed), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        this.currCount = size;
        if (this.cur != null) {
            this.cur.close();
        }
        this.cur = this.mDb.rawQuery("select * from clockskin_online_list WHERE state = 0 limit 0," + this.currCount, null);
        Log.e(TAG, "in initData() cur.size() = " + this.cur.getCount() + " sizeAll=" + sizeAll + " OnlineThemeListSize=" + NetHelper.getOnlineThemeListSize());
        if (this.adapter != null) {
            this.adapter.recycleCache();
            this.adapter.changeCursor(this.cur);
        } else {
            this.adapter = new ClockSkinOnlineListViewAdapter(this, this.cur);
        }
        if (this.currCount >= size && sizeAll == NetHelper.getOnlineThemeListSize()) {
            Log.e(TAG, "NetHelper.getOnlineThemeListSize() = " + this.cur.getCount());
            this.mListView.removeFooterView(this.mFootLoadingView);
            this.mFootLoadingView = null;
        }
        int currPosition = this.mListView.getFirstVisiblePosition();
        this.mListView.setAdapter(this.adapter);
        this.mListView.setSelection(currPosition);
        this.mListView.setOnScrollListener(this.listener);
        Intent it = new Intent("BROADCAST_LOADING_STATE_FILTER");
        it.putExtra("BROADCAST_LOADING_STATE", 1012);
        sendBroadcast(it);
    }

    /* access modifiers changed from: private */
    public void startUpdate() {
        Log.e(TAG, "in startUpdate() ..");
        startDownloadToDb(5);
        checkVersion();
    }

    /* access modifiers changed from: private */
    public void endUpdate() {
        Log.e(TAG, "in endUpdate() ..");
        markUpdate();
        initData();
        this.mUpdatLoading = false;
        if (this.mFootLoadingView != null) {
        }
    }

    private void markUpdate() {
        Log.d(TAG, "in markUpdate() ..");
        Editor ed = getSharedPreferences("updateRecord", 0).edit();
        ed.putString("last_update", NetHelper.getLastUpdateDate());
        ed.commit();
    }

    private void checkVersion() {
    }

    /* access modifiers changed from: private */
    public void startDownloadToDb(int addstep) {
        Log.d(TAG, "in startDownloadToDb() will load...");
        List<OnlineClockSkinXMLNode> toDownList = NetHelper.getOnlineThemeList();
        if (toDownList == null || toDownList.isEmpty()) {
            Log.e(TAG, "get null or empty toDownlist ...");
            NetHelper.sendMsg(4);
            return;
        }
        new UpdateTask(toDownList, addstep).execute(new Void[0]);
    }

    /* access modifiers changed from: private */
    public int getAllClockSkinCount() {
        Cursor countCurAll = this.mDb.rawQuery("select * from clockskin_online_list", null);
        int cnt = countCurAll.getCount();
        countCurAll.close();
        return cnt;
    }

    /* access modifiers changed from: private */
    public int getOnlineClockSkinCount() {
        Cursor onlineCountCur = this.mDb.rawQuery("select * from clockskin_online_list WHERE state = 0", null);
        int cnt = onlineCountCur.getCount();
        onlineCountCur.close();
        return cnt;
    }

    public void saveFile(Bitmap bm, String fileName) throws IOException {
        File dirFile = new File("/data/data/com.bid.launcherwatch//WiiwearClockSkin/preview/");
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File("/data/data/com.bid.launcherwatch//WiiwearClockSkin/preview/" + fileName)));
        bm.compress(CompressFormat.PNG, 80, bos);
        bos.flush();
        bos.close();
    }

    /* access modifiers changed from: private */
    public boolean checkIsClockInstalled(String packageName) {
        for (String installed : this.mInstalled) {
            if (installed.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean checkIsPackageExist(String packageName) {
        if (!this.mDb.isOpen()) {
            return false;
        }
        return this.mDb.query("clockskin_online_list", null, "skinid=?", new String[]{packageName}, null, null, null).moveToNext();
    }

    /* access modifiers changed from: private */
    public void putinDatabase(OnlineClockSkinXMLNode item, byte[] first, byte[] second) {
        putinDatabaseWhitState(item, first, second, 0);
    }

    /* access modifiers changed from: private */
    public void putinDatabaseWhitState(OnlineClockSkinXMLNode item, byte[] first, byte[] second, int state) {
        Log.d(TAG, "in putinDatabase()  will put into db ");
        ContentValues values = new ContentValues();
        values.put("clockName", item.getName());
        values.put("skinid", item.getSkinId());
        values.put("filePath", item.getFilePath());
        values.put("state", Integer.valueOf(state));
        if (this.mDb.isOpen()) {
            this.mDb.insert("clockskin_online_list", null, values);
        }
    }

    /* access modifiers changed from: private */
    public void showDialog(int ridTitle, int ridMsg) {
        Builder builder = new Builder(this);
        builder.setCancelable(false);
        //builder.setIcon(17301659);
        builder.setTitle(ridTitle);
        builder.setMessage(ridMsg);
        builder.setPositiveButton(R.string.btn_alert_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ClockSkinOnlineActivity.this.finish();
            }
        });
        builder.create().show();
    }

    private void showOnlineLoadingDialog() {
        if (this.mProgressDialog == null) {
            this.mProgressDialog = new Dialog(this, R.style.OnlineFontPreview);
            this.mProgressDialog.requestWindowFeature(1);
            this.mProgressDialog.setContentView(R.layout.global_progressbar);
            this.mProgressDialog.setCancelable(false);
            this.mProgressDialog.setCanceledOnTouchOutside(false);
            this.mProgressDialog.show();
        }
    }

    /* access modifiers changed from: private */
    public void dismissOnlineLoadingDialog() {
        if (this.mProgressDialog != null) {
            try {
                this.mProgressDialog.dismiss();
            } catch (Exception e) {
                Log.e(TAG, "Exception when Dialog.dismiss()...");
            } catch (Throwable th) {
                this.mProgressDialog = null;
                throw th;
            }
            this.mProgressDialog = null;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                Builder builder = new Builder(this);
                //builder.setIcon(17301659);
                builder.setTitle(getString(R.string.online_font_statistics));
                builder.setMessage(getString(R.string.online_font_count) + NetHelper.getOnlineThemeListSize() + "\n" + getString(R.string.online_theme_update_time) + NetHelper.getLastUpdateDate());
                builder.setPositiveButton(R.string.btn_alert_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            case 1:
                Editor ed = getSharedPreferences("updateRecord", Context.MODE_WORLD_WRITEABLE).edit();
                ed.putString("last_update", "201201010000");
                ed.commit();
                this.currCount = 0;
                if (this.mFootLoadingView == null) {
                    this.mFootLoadingView = View.inflate(this, R.layout.dragdrop_progressbar, null);
                    this.mListView.addFooterView(this.mFootLoadingView, null, false);
                }
                this.mListView.setSelection(0);
                showOnlineLoadingDialog();
                new CheckThread(this, null).start();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, getString(R.string.online_theme_statistics));
        menu.add(0, 1, 0, getString(R.string.reload_onlinelist));
        return true;
    }

    private boolean isServiceRunning() {
        for (RunningServiceInfo service : ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE)) {
            if (!"com.wiiteck.clockpreviewer.online.ClockSkinDownloadService".equals(service.service.getClassName())) {
                if ("com.bid.launcherwatch.online.ClockSkinDownloadService".equals(service.service.getClassName())) {
                }
            }
            return true;
        }
        return false;
    }

    public void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }
            for (File delete : childFiles) {
                delete(delete);
            }
            file.delete();
        }
    }
}
