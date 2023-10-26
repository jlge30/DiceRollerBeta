package com.jose.diceroller.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "diceroller.db";
    public static final String TABLE_PUNTUACION = "puntuacion";

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PUNTUACION + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Nombre TEXT NOT NULL," +
                "Fecha TEXT NOT NULL," +
                "Puntuacion INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PUNTUACION);
        onCreate(db);
    }
}
