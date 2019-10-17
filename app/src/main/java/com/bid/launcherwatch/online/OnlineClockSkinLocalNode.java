package com.bid.launcherwatch.online;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.IOException;

public class OnlineClockSkinLocalNode {
    private String apkName;
    private String author;
    private Bitmap bmpFirst;
    private Bitmap bmpSecond;
    private String clocktype;
    byte[] inBmpFirst;
    private String name;
    private String packageName;
    private float size;
    private int state;

    public OnlineClockSkinLocalNode() {
        this.name = "";
        this.packageName = "";
        this.apkName = "";
        this.bmpFirst = null;
        this.bmpSecond = null;
        this.author = "";
        this.size = 0.0f;
        this.clocktype = "";
        this.state = 0;
    }

    public OnlineClockSkinLocalNode(Context unUsed, Cursor cursor) {
        synchronized (this) {
            this.name = cursor.getString(cursor.getColumnIndex("clockName"));
            this.packageName = cursor.getString(cursor.getColumnIndex("skinid"));
            this.apkName = cursor.getString(cursor.getColumnIndex("filePath"));
            try {
                this.bmpFirst = getImageBitmap(this.packageName + ".png");
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.clocktype = cursor.getString(cursor.getColumnIndex("clocktype"));
            this.state = cursor.getInt(cursor.getColumnIndex("state"));
        }
        return;
    }

    public Bitmap getImageBitmap(String filename) throws IOException {
        String filepath = "/data/data/com.bid.launcherwatch//WiiwearClockSkin/preview/" + filename;
        if (!new File(filepath).exists()) {
            return null;
        }
        return BitmapFactory.decodeFile(filepath);
    }

    public String getName() {
        return this.name;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getApkName() {
        return this.apkName;
    }

    public Bitmap getBmpFirst() {
        return this.bmpFirst;
    }

    public String getAuthor() {
        return this.author;
    }

    public float getSize() {
        return this.size;
    }

    public String getClockType() {
        return this.clocktype;
    }

    public int getState() {
        return this.state;
    }

    public byte[] getBmpFirstByByte() {
        return this.inBmpFirst;
    }
}

