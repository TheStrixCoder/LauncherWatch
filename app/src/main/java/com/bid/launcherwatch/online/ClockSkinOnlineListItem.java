package com.bid.launcherwatch.online;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bid.launcherwatch.R;

public class ClockSkinOnlineListItem extends RelativeLayout {
    private Button btn;
    private ImageView ivFirst;
    /* access modifiers changed from: private */
    public OnlineClockSkinLocalNode mThemeNode = null;
    private TextView tvAuthor;
    private TextView tvThemeName;

    public ClockSkinOnlineListItem(Context context) {
        super(context);
    }

    public ClockSkinOnlineListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.ivFirst = (ImageView) findViewById(R.id.lv_item_iv_first);
        this.tvThemeName = (TextView) findViewById(R.id.lv_item_tv_name);
        this.tvAuthor = (TextView) findViewById(R.id.lv_item_tv_author);
        this.btn = (Button) findViewById(R.id.lv_item_btn_install);
    }

    public final void bind(final Context context, OnlineClockSkinLocalNode node, int pos) {
        this.mThemeNode = node;
        this.btn.setVisibility(VISIBLE);
        if (this.mThemeNode.getState() == 0) {
            this.btn.setEnabled(true);
        } else if (this.mThemeNode.getState() == 1) {
            this.btn.setVisibility(GONE);
        }
        this.ivFirst.setImageBitmap(node.getBmpFirst());
        this.tvThemeName.setText(node.getName());
        this.tvAuthor.setText(getResources().getString(R.string.font_tv_all) + node.getAuthor());
        this.btn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (ClockSkinOnlineListItem.this.mThemeNode.getState() == 0) {
                    Intent it = new Intent(context, ClockSkinDownloadService.class);
                    it.putExtra("THEME_NAME", ClockSkinOnlineListItem.this.mThemeNode.getName());
                    it.putExtra("APK_NAME", ClockSkinOnlineListItem.this.mThemeNode.getApkName());
                    it.putExtra("APK_SIZE", ClockSkinOnlineListItem.this.mThemeNode.getSize());
                    it.putExtra("PACKAG_NAME", ClockSkinOnlineListItem.this.mThemeNode.getPackageName());
                    it.putExtra("BMP_FIRST", ClockSkinOnlineListItem.this.mThemeNode.getBmpFirstByByte());
                    it.putExtra("AUTHOR", ClockSkinOnlineListItem.this.mThemeNode.getAuthor());
                    it.putExtra("VERSION", ClockSkinOnlineListItem.this.mThemeNode.getClockType());
                    Log.e("onlineListItem", "apkName = " + ClockSkinOnlineListItem.this.mThemeNode.getApkName());
                    context.startService(it);
                }
            }
        });
    }
}
