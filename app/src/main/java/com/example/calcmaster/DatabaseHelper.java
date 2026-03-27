package com.example.calcmaster;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "calculations.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "calculations";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_INPUT = "input";
    private static final String COLUMN_RESULT = "result";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_INPUT + " TEXT, " +
                COLUMN_RESULT + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void clearAllCalculations() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public void addCalculation(String input, String result) {
        if (input == null || result == null) return;
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the calculation already exists
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                        COLUMN_INPUT + " = ? AND " + COLUMN_RESULT + " = ?",
                new String[]{input, result});

        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_INPUT, input);
            values.put(COLUMN_RESULT, result);
            db.insert(TABLE_NAME, null, values);
        }

        cursor.close();
        db.close();
    }

    public List<Calculation> getAllCalculations() {
        List<Calculation> calculations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String input = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INPUT));
                String result = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESULT));
                calculations.add(new Calculation(id, input, result));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return calculations;
    }
}