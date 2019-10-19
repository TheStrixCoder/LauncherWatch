package com.bid.launcherwatch;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import com.bid.launcherwatch.WatchApp.installedClock;
import com.bid.launcherwatch.imagepicker.ImagePicker;
import com.bid.launcherwatch.imagepicker.ImagePicker.Callback;
import com.bid.launcherwatch.imagepicker.cropper.CropImage.ActivityBuilder;
import com.bid.launcherwatch.imagepicker.cropper.CropImageView.CropShape;
import com.bid.launcherwatch.imagepicker.cropper.CropImageView.Guidelines;
import com.bid.launcherwatch.online.ClockSkinOnlineActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import java.io.File;
import java.util.ArrayList;

public class ChooseClockActivity extends Activity {
    private static int RESULT_LOAD_IMAGE = 10;
    private final String INDEX = "index";
    private final int RESULTSCODE = 50;
    String TAG = "ChooseClockActivity";
    public ImageAdapter imageAdapter;
    private ImagePicker imagePicker = new ImagePicker();
    /* access modifiers changed from: private */
    public ArrayList<installedClock> mAllClockSkins;
    /* access modifiers changed from: private */
    public String mCustomDialWatchfacePath = null;
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            int index;
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    ChooseClockActivity.this.mIndex = WatchApp.getClockIndex(ChooseClockActivity.this);
                    ChooseClockActivity.this.initUI();
                    ChooseClockActivity.this.scrollToView();
                    return;
                case 1:
                    int allClockCnt = WatchApp.getAllClockCount();
                    int selectedPos = ChooseClockActivity.this.myGallery.getSelectedItemPosition();
                    if (selectedPos >= allClockCnt) {
                        index = allClockCnt - 1;
                    } else {
                        index = selectedPos;
                    }
                    ChooseClockActivity.this.mIndex = WatchApp.getClockIndex(ChooseClockActivity.this);
                    ChooseClockActivity.this.initUI();
                    ChooseClockActivity.this.scrollToView(index);
                    return;
                case 2:
                    ChooseClockActivity.this.mIndex = WatchApp.getClockIndex(ChooseClockActivity.this);
                    ChooseClockActivity.this.initUI();
                    ChooseClockActivity.this.scrollToView(ChooseClockActivity.this.mIndex);
                    return;
                case 3:
                    ChooseClockActivity.this.initUI();
                    ChooseClockActivity.this.scrollToView(ChooseClockActivity.this.myGallery.getSelectedItemPosition());
                    return;
                default:
                    return;
            }
        }
    };
    /* access modifiers changed from: private */
    public DisplayImageOptions mImageOptions;
    /* access modifiers changed from: private */
    public int mIndex;
    PackageManager mPackageManager;
    private final BroadcastReceiver mUpdateClockReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String str = intent.getStringExtra("action_str");
            if (str.equals("installclock")) {
                ChooseClockActivity.this.mHandler.sendEmptyMessage(0);
            } else if (str.equals("deleteclock")) {
                ChooseClockActivity.this.mHandler.sendEmptyMessage(1);
            } else if (str.equals("cleanrelaodclock")) {
                ChooseClockActivity.this.mHandler.sendEmptyMessage(2);
            }
        }
    };
    /* access modifiers changed from: private */
    public Gallery myGallery;

    private class ImageAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        /* access modifiers changed from: private */
        public Context mContext;
        private DisplayImageOptions options = new Builder().showImageOnLoading(R.drawable.loadding_backround).showImageForEmptyUri(R.drawable.loadding_backround).showImageOnFail(R.drawable.loadding_backround).cacheInMemory(false).cacheOnDisk(false).bitmapConfig(Config.RGB_565).build();

        private class ViewHolder {
            /* access modifiers changed from: private */
            public ImageView mImage;
            /* access modifiers changed from: private */
            public TextView mTitle;

            /* synthetic */ ViewHolder(ImageAdapter this$12, ViewHolder viewHolder) {
                this();
            }

            private ViewHolder() {
            }
        }

        public ImageAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
            ChooseClockActivity.this.mImageOptions = new Builder().showImageOnLoading(R.drawable.loadding_backround).showImageForEmptyUri(R.drawable.loadding_backround).showImageOnFail(R.drawable.loadding_backround).cacheInMemory(false).cacheOnDisk(false).bitmapConfig(Config.RGB_565).displayer(new RoundedBitmapDisplayer(240)).build();
            this.mContext = context;
        }

        public int getCount() {
            return ChooseClockActivity.this.mAllClockSkins.size();
        }

        public Object getItem(int position) {
            return Integer.valueOf(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder(this, null);
                convertView = this.inflater.inflate(R.layout.horizontal_list_item, null);
                holder.mImage = (ImageView) convertView.findViewById(R.id.img_list_item);
                holder.mTitle = (TextView) convertView.findViewById(R.id.text_list_item);
                Button deleteButton = (Button) convertView.findViewById(R.id.delete_clock);
                Button configurationButton = (Button) convertView.findViewById(R.id.configuration);
                int i = position;
                String clockskinType = ((installedClock) ChooseClockActivity.this.mAllClockSkins.get(position)).type;
                if (clockskinType.equals("downloadclock") || clockskinType.equals("sdclock") || clockskinType.equals("liveClockSkin")) {
                    if (clockskinType.equals("liveClockSkin") && ((installedClock) ChooseClockActivity.this.mAllClockSkins.get(position)).configurationAction != null) {
                        configurationButton.setVisibility(View.VISIBLE);
                        final int i2 = position;
                        configurationButton.setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                Intent it = new Intent(((installedClock) ChooseClockActivity.this.mAllClockSkins.get(i2)).configurationAction);
                                it.addCategory("com.google.android.wearable.watchface.category.WEARABLE_CONFIGURATION");
                                ImageAdapter.this.mContext.startActivity(it);
                            }
                        });
                    }
                    final int i3 = position;
                    deleteButton.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            Intent it = new Intent("com.update.installclock");
                            it.putExtra("action_str", "deleteclock");
                            it.putExtra("PACKAG_NAME", ((installedClock) ChooseClockActivity.this.mAllClockSkins.get(i3)).pkg);
                            it.putExtra("INDEX", i3);
                            ImageAdapter.this.mContext.sendBroadcast(it);
                        }
                    });
                    if (ChooseClockActivity.this.mIndex == position) {
                        deleteButton.setVisibility(View.GONE);
                    } else if (!clockskinType.equals("sdclock")) {
                        deleteButton.setVisibility(View.VISIBLE);
                    } else if (((installedClock) ChooseClockActivity.this.mAllClockSkins.get(position)).filePath.contains("system")) {
                        deleteButton.setVisibility(View.GONE);
                    } else {
                        deleteButton.setVisibility(View.VISIBLE);
                    }
                } else if (clockskinType.equals("addclockskin")) {
                    deleteButton.setBackgroundResource(R.drawable.refresh_list);
                    deleteButton.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            Intent it = new Intent("com.update.installclock");
                            it.putExtra("action_str", "cleanrelaodclock");
                            ImageAdapter.this.mContext.sendBroadcast(it);
                        }
                    });
                } else if (clockskinType.equals("bgsettingclock")) {
                    deleteButton.setVisibility(View.GONE);
                    configurationButton.setVisibility(View.GONE);
                    configurationButton.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            ChooseClockActivity.this.startChooser();
                        }
                    });
                } else {
                    deleteButton.setVisibility(View.GONE);
                    configurationButton.setVisibility(View.GONE);
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String thumbnailFilePath = ((installedClock) ChooseClockActivity.this.mAllClockSkins.get(position)).previewPath;
            int thumbnailRes = ((installedClock) ChooseClockActivity.this.mAllClockSkins.get(position)).img_preview_id;
            if (thumbnailFilePath != null) {
                String url = "file://" + thumbnailFilePath;
                ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(mContext));
                ImageLoader.getInstance().displayImage(url, holder.mImage, this.options);
            } else if (thumbnailRes != 0) {
                holder.mImage.setImageResource(((installedClock) ChooseClockActivity.this.mAllClockSkins.get(position)).img_preview_id);
            }
            if (((installedClock) ChooseClockActivity.this.mAllClockSkins.get(position)).type.equals("bgsettingclock") && ChooseClockActivity.this.mCustomDialWatchfacePath != null) {
                String url2 = "file://" + ChooseClockActivity.this.mCustomDialWatchfacePath;
                ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(mContext));
                ImageLoader.getInstance().displayImage(url2, holder.mImage, ChooseClockActivity.this.mImageOptions);
            }
            ResolveInfo resolveInfo = ((installedClock) ChooseClockActivity.this.mAllClockSkins.get(position)).resolveInfo;
            if (resolveInfo != null) {
                Drawable thumbDrawable = null;
                if (((installedClock) ChooseClockActivity.this.mAllClockSkins.get(position)).type.equals("apkclockskin")) {
                    try {
                        Resources themeResources = ChooseClockActivity.this.mPackageManager.getResourcesForApplication(resolveInfo.activityInfo.packageName);
                        int imgClockPreviewId = themeResources.getIdentifier("img_clock_preview", "drawable", resolveInfo.activityInfo.packageName);
                        if (imgClockPreviewId != 0) {
                            thumbDrawable = themeResources.getDrawableForDensity(imgClockPreviewId, 160, null);
                        }
                    } catch (NameNotFoundException e) {
                    }
                } else {
                    thumbDrawable = ChooseClockActivity.this.mPackageManager.getDrawable(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.metaData.getInt("com.google.android.wearable.watchface.preview_circular"), resolveInfo.serviceInfo.applicationInfo);
                }
                if (thumbDrawable != null) {
                    holder.mImage.setImageDrawable(thumbDrawable);
                }
            }
            return convertView;
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.addActivity(this);
        Log.d(this.TAG, "onCreate");
        setContentView(R.layout.activity_choose_clock);
        this.myGallery = (Gallery) findViewById(R.id.myGallery);
        this.mIndex = 0;
        if (getIntent() != null) {
            this.mIndex = getIntent().getIntExtra("index", 0);
        }
        initUI();
        scrollToView();
        registerReceiver(this.mUpdateClockReceiver, new IntentFilter("com.update.installclock.done"));
        this.mPackageManager = getPackageManager();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        Log.d(this.TAG, "onResume");
        super.onResume();
    }

    public void onDestroy() {
        Log.d(this.TAG, "onDestroy");
        super.onDestroy();
        Util.removeActivity(this);
        unregisterReceiver(this.mUpdateClockReceiver);
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        Log.d(this.TAG, "onNewIntent");
        super.onNewIntent(intent);
    }

    /* access modifiers changed from: private */
    public void scrollToView() {
        this.myGallery.setSelection(this.mIndex);
    }

    /* access modifiers changed from: private */
    public void scrollToView(int index) {
        this.myGallery.setSelection(index);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d(this.TAG, "onWindowFocusChanged");
        super.onWindowFocusChanged(hasFocus);
    }

    public void initUI() {
        this.mCustomDialWatchfacePath = getCustomDialWatchfacePath();
        this.mAllClockSkins = new ArrayList<>(WatchApp.getInstalledClocks());
        installedClock addClockSkin = new installedClock();
        addClockSkin.type = "addclockskin";
        addClockSkin.img_preview_id = R.drawable.add_button;
        this.mAllClockSkins.add(addClockSkin);
        int size = this.mAllClockSkins.size();
        if (this.imageAdapter != null) {
            this.imageAdapter.notifyDataSetChanged();
        } else {
            this.imageAdapter = new ImageAdapter(this);
            this.myGallery.setAdapter(this.imageAdapter);
        }
        this.myGallery.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (((installedClock) ChooseClockActivity.this.mAllClockSkins.get(position)).type.equals("addclockskin")) {
                    ChooseClockActivity.this.startActivity(new Intent(ChooseClockActivity.this, ClockSkinOnlineActivity.class));
                } else if (!((installedClock) ChooseClockActivity.this.mAllClockSkins.get(position)).type.equals("bgsettingclock") || ChooseClockActivity.this.mCustomDialWatchfacePath != null) {
                    Intent intent = new Intent();
                    intent.putExtra("index", position);
                    ChooseClockActivity.this.setResult(50, intent);
                    ChooseClockActivity.this.finish();
                    ChooseClockActivity.this.overridePendingTransition(0, R.anim.exit_anim);
                } else {
                    ChooseClockActivity.this.startChooser();
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.imagePicker.onActivityResult(this, requestCode, resultCode, data);
    }

    /* access modifiers changed from: private */
    public void startChooser() {
        this.imagePicker.setCropImage(true);
        this.imagePicker.startChooser(this, new Callback() {
            public void onPickImage(Uri imageUri) {
            }

            public void onCropImage(Uri imageUri) {
                String imgPath = ChooseClockActivity.this.getApplicationContext().getCacheDir() + File.separator + imageUri.toString().substring(imageUri.toString().lastIndexOf("/") + 1);
                Editor mEditor = ChooseClockActivity.this.getSharedPreferences("custom_clockview_bg_img", 0).edit();
                mEditor.putString("bg_img", imgPath);
                mEditor.commit();
                int allClockCnt = WatchApp.getAllClockCount();
                if (ChooseClockActivity.this.myGallery.getSelectedItemPosition() < allClockCnt) {
                    int selectedPos = allClockCnt - 1;
                    Intent intent = new Intent();
                    intent.putExtra("index", selectedPos);
                    ChooseClockActivity.this.setResult(50, intent);
                    ChooseClockActivity.this.finish();
                    ChooseClockActivity.this.overridePendingTransition(0, R.anim.exit_anim);
                }
            }

            public void cropConfig(ActivityBuilder builder) {
                builder.setMultiTouchEnabled(false).setGuidelines(Guidelines.OFF).setCropShape(CropShape.OVAL).setRequestedSize(400, 400).setAspectRatio(1, 1);
            }
        });
    }

    public String getCustomDialWatchfacePath() {
        String picPath = getSharedPreferences("custom_clockview_bg_img", 0).getString("bg_img", "");
        if (!new File(picPath).exists()) {
            return null;
        }
        return picPath;
    }
}
