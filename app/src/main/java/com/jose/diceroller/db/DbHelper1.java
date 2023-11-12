package com.jose.diceroller.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper1 extends SQLiteOpenHelper {
    //atributos
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NOMBRE = "jugador.db";
    private static final String TABLE_JUGADORES = "jugadores";

    //variables globales
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOMBRE = "nombre";
    private static final String COLUMN_PUNTUACION = "puntuacion";
    private static final String COLUMN_FECHA = "fecha";

    public PlayerHistory playerHistory;
    public Context context;


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {//creacion de la tabla de la base de datos
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_JUGADORES + "(" +
                COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NOMBRE + " TEXT NOT NULL,"+
                COLUMN_PUNTUACION + " INTEGER NOT NULL, " +
                COLUMN_FECHA + " TEXT NOT NULL)");

    }



    public DbHelper1(@Nullable Context context) {//creacion de la base de datos
        super(context, DATABASE_NOMBRE, null, DATABASE_VERSION);
        this.context = context;

    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {//versión de la base de datos
        sqLiteDatabase.execSQL("DROP TABLE " + TABLE_JUGADORES);
        onCreate(sqLiteDatabase);
    }

}
