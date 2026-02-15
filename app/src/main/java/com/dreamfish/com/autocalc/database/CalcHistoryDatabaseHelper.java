package com.dreamfish.com.autocalc.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CalcHistoryDatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "calcHistory.db";

    
    public CalcHistoryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CalcHistoryDbSchema.CalcHistoryDTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                CalcHistoryDbSchema.CalcHistoryDTable.Cols.FORMULA +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
