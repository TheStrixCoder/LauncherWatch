package com.bid.launcherwatch.online;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ClockSkinDBHelper extends SQLiteOpenHelper {
    public ClockSkinDBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS clockskin_online_list (_id INTEGER PRIMARY KEY AUTOINCREMENT,clockName VARCHAR(16),skinid VARCHAR(30),filePath VARCHAR(30),preview BLOB,customer VARCHAR(16),clocktype VARCHAR(30),state INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS clockskin_install_list (_id INTEGER PRIMARY KEY AUTOINCREMENT,clockName VARCHAR(16),skinid VARCHAR(30),filePath VARCHAR(30),preview BLOB,customer VARCHAR(16),clocktype VARCHAR(30),state INTEGER)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS clockskin_online_list");
        db.execSQL("DROP TABLE IF EXISTS clockskin_install_list");
        onCreate(db);
    }
}
