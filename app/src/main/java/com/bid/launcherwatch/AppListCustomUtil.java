package com.bid.launcherwatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.Settings.System;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.android.internal.util.XmlUtils;
import com.bid.launcherwatch.ClockUtil.ClockSet;
import com.bid.launcherwatch.ClockUtil.ClockSetWallpaper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParserException;

public class AppListCustomUtil {
    private static Drawable redbackground;
    private static Drawable redbackground1;
    private static Drawable redbackground2;
    private static Drawable redbackground3;
    private static Drawable redbackground4;
    private static Drawable redbackground5;
    private static Drawable redbackground6;
    private static Drawable redbackground7;
    private static Drawable redbackground8;
    private static Drawable redbackground9;
    static ArrayList<TopPackage> sTopPackages = null;

    public static class AppInfo {
        public String cls;
        public Drawable icon;
        public String pkg;
        public String title;

        public AppInfo(String pkg_name, String cls_name, String title_text, Drawable icon_drawable) {
            Log.i("AppListCustomUtil", "AppInfo");
            this.pkg = pkg_name;
            this.cls = cls_name;
            this.title = title_text;
            this.icon = icon_drawable;
        }
    }

    static class TopPackage {
        String className;
        int order;
        String packageName;

        public TopPackage(String pkgName, String clsName, int index) {
            Log.i("AppListCustomUtil", "TopPackage");
            this.packageName = pkgName;
            this.className = clsName;
            this.order = index;
        }
    }

    public static ArrayList<AppInfo> getAppList(Context context) {
        Intent mainIntent = new Intent("android.intent.action.MAIN", null);
        mainIntent.addCategory("android.intent.category.LAUNCHER");
        PackageManager mPackageManager = context.getPackageManager();
        Log.i("guoxiaolong", "mPackageManager=" + mPackageManager);
        List<ResolveInfo> mApps = mPackageManager.queryIntentActivities(mainIntent, 0);
        Log.i("guoxiaolong", "List<ResolveInfo> mApps=" + mApps);
        ArrayList<AppInfo> applist = new ArrayList<>();
        for (int i = 0; i < mApps.size(); i++) {
            ResolveInfo info = (ResolveInfo) mApps.get(i);
            String pkg = info.activityInfo.packageName;
            String cls = info.activityInfo.name;
            String title = (String) info.activityInfo.loadLabel(mPackageManager);
            Log.v("caojinliang", "AppListCustomUtil::title=" + title);
            Log.v("caojinliang", "AppListCustomUtil::pkg=" + pkg);
            Log.v("caojinliang", "AppListCustomUtil::cls=" + cls);
            Drawable icon = null;
            try {
                icon = context.getPackageManager().getResourcesForApplication(info.activityInfo.applicationInfo).getDrawableForDensity(info.getIconResource(), 240);
            } catch (Exception e) {
            }
            if (icon == null) {
                icon = info.activityInfo.loadIcon(mPackageManager);
            }
            Drawable repalceIcon = ReplaceAppIcon(context, cls);
            if (repalceIcon != null) {
                icon = repalceIcon;
            }
            AppInfo appinfo = new AppInfo(pkg, cls, title, IndexUpdate(icon, context, cls));
            Log.i("guoxiaolong", "appinfo109 =" + appinfo);
            if (!isHideApp(pkg, cls)) {
                applist.add(appinfo);
            }
            Log.i("guoxiaolong", "ArrayList<AppInfo> applist112 =" + applist);
        }
        loadTopPackage(context);
        ArrayList<AppInfo> applist2 = reorderApplist(applist);
        Log.i("guoxiaolong", "applist116 =" + applist2);
        return applist2;
    }

    private static boolean isHideApp(String pkg, String cls) {
        boolean isHide = false;
        if (pkg.equals("com.android.stk")) {
            isHide = true;
        }
        if (cls.equals("com.google.android.googlequicksearchbox.SearchActivity") || cls.equals("com.google.android.gms.app.settings.GoogleSettingsActivity")) {
            isHide = true;
        }
        for (ClockSet clockSetWallpaper : ClockUtil.mClockList) {
            if ((clockSetWallpaper instanceof ClockSetWallpaper) && pkg.equals(((ClockSetWallpaper) clockSetWallpaper).PackageName)) {
                return true;
            }
        }
        return isHide;
    }

    public static Drawable ReplaceAppIcon(Context context, String cls) {
        String cls2 = cls.toLowerCase(Locale.US).replace(".", "_").replace("$", "_");
        Log.v("caojinliang", "AppListCustomUtil::icon_id=" + cls2);
        int iconID = context.getResources().getIdentifier(cls2.toString(), "mipmap", "com.mediatek.watchapp");
        Log.i("AppListCustomUtil", "iconID = " + iconID);
        if (iconID == 0) {
            return null;
        }
        Drawable icon = context.getResources().getDrawable(iconID);
        Log.i("AppListCustomUtil", "context.getResources().getDrawable(iconID) = " + icon);
        return icon;
    }

    public static Drawable IndexUpdate(Drawable icon, Context context, String cls) {
        String cls2 = cls.toLowerCase(Locale.US).replace(".", "_").replace("$", "_");
        if (cls2.equals("com_android_mms_ui_bootactivity")) {
            return new BitmapDrawable(genertCountsOnBitmap(context, drawableToBitmap(icon), 1));
        }
        if (cls2.equals("com_android_dialer_dialtactswearactivity")) {
            return new BitmapDrawable(genertCountsOnBitmap(context, drawableToBitmap(icon), 0));
        }
        return icon;
    }

    @SuppressLint("ResourceType")
    static boolean loadTopPackage(Context context) {
        Log.i("AppListCustomUtil", "loadTopPackage");
        if (sTopPackages != null) {
            return false;
        }
        sTopPackages = new ArrayList<>();
        try {
            XmlResourceParser parser = context.getResources().getXml(R.xml.default_toppackage);
            AttributeSet attrs = Xml.asAttributeSet(parser);
            XmlUtils.beginDocument(parser, "toppackages");
            int depth = parser.getDepth();
            while (true) {
                int type = parser.next();
                if ((type == 3 && parser.getDepth() <= depth) || type == 1) {
                    break;
                } else if (type == 2) {
                    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TopPackage);
                    sTopPackages.add(new TopPackage(a.getString(0), a.getString(1), a.getInt(2, 0)));
                    Log.d("AppListCustomUtil", "loadTopPackage: packageName = " + a.getString(0) + ", className = " + a.getString(1));
                    a.recycle();
                }
            }
        } catch (XmlPullParserException e) {
            Log.w("AppListCustomUtil", "Got XmlPullParserException while parsing toppackage.", e);
        } catch (IOException e2) {
            Log.w("AppListCustomUtil", "Got IOException while parsing toppackage.", e2);
        }
        return false;
    }

    private static ArrayList<AppInfo> reorderApplist(ArrayList<AppInfo> data) {
        Log.i("AppListCustomUtil", "reorderApplist");
        if (sTopPackages == null || sTopPackages.isEmpty()) {
            return null;
        }
        ArrayList<AppInfo> dataReorder = new ArrayList<>(42);
        dataReorder.clear();
        for (TopPackage tp : sTopPackages) {
            int loop = 0;
            Iterator ai$iterator = data.iterator();
            while (true) {
                if (!ai$iterator.hasNext()) {
                    break;
                }
                AppInfo ai = (AppInfo) ai$iterator.next();
                if (ai.pkg.equals(tp.packageName) && ai.cls.equals(tp.className)) {
                    data.remove(ai);
                    dataReorder.add(ai);
                    break;
                }
                loop++;
            }
        }
        for (AppInfo ai2 : data) {
            dataReorder.add(ai2);
        }
        return dataReorder;
    }

    public static Bitmap genertCountsOnBitmap(Context context, Bitmap icon, int type) {
        if (redbackground == null) {
            redbackground = context.getResources().getDrawable(R.drawable.redbackgroung);
        }
        if (redbackground1 == null) {
            redbackground1 = context.getResources().getDrawable(R.drawable.redbackground2);
        }
        if (redbackground2 == null) {
            redbackground2 = context.getResources().getDrawable(R.drawable.redbackground3);
        }
        if (redbackground3 == null) {
            redbackground3 = context.getResources().getDrawable(R.drawable.redbackground4);
        }
        if (redbackground4 == null) {
            redbackground4 = context.getResources().getDrawable(R.drawable.redbackground5);
        }
        if (redbackground5 == null) {
            redbackground5 = context.getResources().getDrawable(R.drawable.redbackground6);
        }
        if (redbackground6 == null) {
            redbackground6 = context.getResources().getDrawable(R.drawable.redbackground7);
        }
        if (redbackground7 == null) {
            redbackground7 = context.getResources().getDrawable(R.drawable.redbackground8);
        }
        if (redbackground8 == null) {
            redbackground8 = context.getResources().getDrawable(R.drawable.redbackground9);
        }
        if (redbackground9 == null) {
            redbackground9 = context.getResources().getDrawable(R.drawable.redbackground10);
        }
        int iconSize = (int) context.getResources().getDimension(R.dimen.roundlist_app_icon_size);
        Log.i("AppListCustomUtil", "iconSize=" + iconSize);
        Bitmap SmsIcon = Bitmap.createBitmap(140, 140, Config.ARGB_8888);
        Canvas canvas = new Canvas(SmsIcon);
        Paint paint = new Paint();
        paint.setDither(true);
        paint.setFilterBitmap(true);
        paint.setAntiAlias(true);
        canvas.drawBitmap(icon, new Rect(0, 0, icon.getWidth(), icon.getHeight()), new Rect(0, 0, 140, 140), paint);
        Paint textPaint = new Paint(257);
        textPaint.setColor(-1);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(8.0f);
        textPaint.setTextAlign(Align.CENTER);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        int style = System.getInt(context.getContentResolver(), "watch_app_list_style", 0);
        Log.i("AppListCustomUtil", "style = " + style);
        if (style == 1) {
            if (getMissCounts(context, type) == 1) {
                redbackground.setBounds(iconSize + 40, 20, iconSize + 70, 50);
                redbackground.draw(canvas);
            } else if (getMissCounts(context, type) == 2) {
                redbackground1.setBounds(iconSize + 40, 20, iconSize + 70, 50);
                redbackground1.draw(canvas);
            } else if (getMissCounts(context, type) == 3) {
                redbackground2.setBounds(iconSize + 40, 20, iconSize + 70, 50);
                redbackground2.draw(canvas);
            } else if (getMissCounts(context, type) == 4) {
                redbackground3.setBounds(iconSize + 40, 20, iconSize + 70, 50);
                redbackground3.draw(canvas);
            } else if (getMissCounts(context, type) == 5) {
                redbackground4.setBounds(iconSize + 40, 20, iconSize + 70, 50);
                redbackground4.draw(canvas);
            } else if (getMissCounts(context, type) == 6) {
                redbackground5.setBounds(iconSize + 40, 20, iconSize + 70, 50);
                redbackground5.draw(canvas);
            } else if (getMissCounts(context, type) == 7) {
                redbackground6.setBounds(iconSize + 40, 20, iconSize + 70, 50);
                redbackground6.draw(canvas);
            } else if (getMissCounts(context, type) == 8) {
                redbackground7.setBounds(iconSize + 40, 20, iconSize + 70, 50);
                redbackground7.draw(canvas);
            } else if (getMissCounts(context, type) == 9) {
                redbackground8.setBounds(iconSize + 40, 20, iconSize + 70, 50);
                redbackground8.draw(canvas);
            } else if (getMissCounts(context, type) > 9) {
                redbackground9.setBounds(iconSize + 40, 20, iconSize + 70, 50);
                redbackground9.draw(canvas);
            }
        } else if (getMissCounts(context, type) == 1) {
            redbackground.setBounds(iconSize + 40, 0, iconSize + 70, 30);
            redbackground.draw(canvas);
        } else if (getMissCounts(context, type) == 2) {
            redbackground1.setBounds(iconSize + 40, 0, iconSize + 70, 30);
            redbackground1.draw(canvas);
        } else if (getMissCounts(context, type) == 3) {
            redbackground2.setBounds(iconSize + 40, 0, iconSize + 70, 30);
            redbackground2.draw(canvas);
        } else if (getMissCounts(context, type) == 4) {
            redbackground3.setBounds(iconSize + 40, 0, iconSize + 70, 30);
            redbackground3.draw(canvas);
        } else if (getMissCounts(context, type) == 5) {
            redbackground4.setBounds(iconSize + 40, 0, iconSize + 70, 30);
            redbackground4.draw(canvas);
        } else if (getMissCounts(context, type) == 6) {
            redbackground5.setBounds(iconSize + 40, 0, iconSize + 70, 30);
            redbackground5.draw(canvas);
        } else if (getMissCounts(context, type) == 7) {
            redbackground6.setBounds(iconSize + 40, 0, iconSize + 70, 30);
            redbackground6.draw(canvas);
        } else if (getMissCounts(context, type) == 8) {
            redbackground7.setBounds(iconSize + 40, 0, iconSize + 70, 30);
            redbackground7.draw(canvas);
        } else if (getMissCounts(context, type) == 9) {
            redbackground8.setBounds(iconSize + 40, 0, iconSize + 70, 30);
            redbackground8.draw(canvas);
        } else if (getMissCounts(context, type) > 9) {
            redbackground9.setBounds(iconSize + 40, 0, iconSize + 70, 30);
            redbackground9.draw(canvas);
        }
        return SmsIcon;
    }

    public static int getMissCounts(Context context, int type) {
        if (type == 0) {
            return WatchApp.getUnreadPhone(context);
        }
        return WatchApp.getUnreadSMS(context);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }
}
