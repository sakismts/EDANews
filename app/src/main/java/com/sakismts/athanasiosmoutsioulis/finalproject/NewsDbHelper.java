package com.sakismts.athanasiosmoutsioulis.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by AthanasiosMoutsioulis on 17/03/16.
 */
public class NewsDbHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME ="newsList";
    public static final String TABLE_NAME_FAVORITES ="favorites";
    public static final String COLUMN_ID ="_id";
    public static final String COLUMN_RECORD_ID ="record_id";
    public static final String COLUMN_TITLE ="title";
    public static final String COLUMN_URL ="url";
    public static final String COLUMN_DATE ="date";
    public static final String COLUMN_SDESC ="short_description";
    public static final String COLUMN_DESC ="description";
    public static final String COLUMN_IMGURL ="img_url";



    private static  final String DATABASE_NAME = "news.db";
    private static final int DATABASE_VERSION = 1;

    private  static  final  String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_RECORD_ID + " INTEGER NOT NULL, "
            + COLUMN_TITLE + " VARCHAR(80) NOT NULL, "
            + COLUMN_SDESC + " VARCHAR(80) NOT NULL, "
            + COLUMN_DESC + " VARCHAR(200) NOT NULL, "
            + COLUMN_DATE+ " VARCHAR(80) NOT NULL, "
            + COLUMN_IMGURL+ " VARCHAR(80) NOT NULL, "
            +COLUMN_URL + " VARCHAR(255) NOT NULL, UNIQUE(record_id) ON CONFLICT IGNORE);";
    private  static  final  String DATABASE_CREATE_FAVORITES = "CREATE TABLE "
            + TABLE_NAME_FAVORITES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_RECORD_ID + " INTEGER NOT NULL, "
            + COLUMN_TITLE + " VARCHAR(80) NOT NULL, "
            + COLUMN_SDESC + " VARCHAR(80) NOT NULL, "
            + COLUMN_DESC + " VARCHAR(200) NOT NULL, "
            + COLUMN_DATE+ " VARCHAR(80) NOT NULL, "
            + COLUMN_IMGURL+ " VARCHAR(80) NOT NULL, "
            +COLUMN_URL + " VARCHAR(255) NOT NULL, UNIQUE(record_id) ON CONFLICT IGNORE);";

    public NewsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE_FAVORITES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

    }
}
