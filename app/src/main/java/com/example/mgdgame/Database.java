package com.example.mgdgame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VER = 1;
    private static final String DATABASE_NAME = "MGD_DB.db";
    private static final String TABLE_NAME = "USER_SCORES";
    private static final String COL1 = "ID";
    private static final String COL2 = "USERNAME";
    private static final String COL3 = "SCORE";

    public Database(Context argContext) {
        super(argContext, DATABASE_NAME, null, DATABASE_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String lsCreateTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "USERNAME TEXT NOT NULL, SCORE INTEGER NOT NULL)";
        db.execSQL(lsCreateTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    public void addData(String argUsername, String argScore) {

        SQLiteDatabase db = this.getWritableDatabase();

        if(null == argUsername || null == argScore) return;

        ContentValues lsContentValues = new ContentValues();

        lsContentValues.put(COL2, argUsername);
        lsContentValues.put(COL3, argScore);

        long lsResult = db.insert(TABLE_NAME, null, lsContentValues); //null columnHack just for inserting blank columns

    }

    public List<Score> getAll(){

        SQLiteDatabase db = this.getReadableDatabase();
        String lsSortSelection = COL1 + " = ?";
        String[] lsSortArg = {"SCORE"};
        String lsSortOrder = COL3 + " DESC";

        Cursor lsCursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        List<Score> lsReturnList = new ArrayList<>();

        while(lsCursor.moveToNext()) {

            Score lsScore = new Score(lsCursor.getString(1), lsCursor.getInt(2));
            lsReturnList.add(lsScore);

        }

        lsCursor.close();
        return lsReturnList;

    }

}

class Score {

    private final int ID = -1;
    private final String USERNAME;
    private final int SCORE_VALUE;

    Score(String argUsername, int argScoreVal) {

        this.USERNAME       = argUsername;
        this.SCORE_VALUE    = argScoreVal;

    }

    public float getScore() { return this.SCORE_VALUE; }
    public String getUsername() { return this.USERNAME; }
}