package com.example.hongjeongmin.memo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.hongjeongmin.memo.MemoContract.*;

public class MemoDbHelper extends SQLiteOpenHelper {

    private static MemoDbHelper sInstance; //싱글턴 패턴 (항상 하나의 인스턴스만 유지)
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "memo.db";

    //테이블 생성 sql
    private static final String SQL_CREATE_ENTRIES =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT)",
                    MemoContract.memoEntry.TABLE_NAME,
                    MemoContract.memoEntry._ID,
                    memoEntry.COLUMN_NAME_TITLE,
                    memoEntry.COLUMN_NAME_CONTENTS);


    //테이블 삭제 sql
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + memoEntry.TABLE_NAME;

    //팩토리 메서드
    public static synchronized MemoDbHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MemoDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public MemoDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION); //수정할 때마다 버전 올라감
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
