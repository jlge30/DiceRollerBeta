package com.jose.diceroller.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DbHelper1 extends SQLiteOpenHelper {
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

    public void insertarJugador(PlayerHistory playerHistory){
        try {
            DbHelper dbHelper = new DbHelper(context);//creacion de la base de datos
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DbHelper1.COLUMN_NOMBRE, playerHistory.getNombre());
            values.put(DbHelper1.COLUMN_PUNTUACION, playerHistory.getPuntuacion());
            values.put(DbHelper1.COLUMN_FECHA, playerHistory.getFecha());//fecha en el constructor automática
            db.insert(DbHelper1.TABLE_JUGADORES, null, values);// ver en ejemplo el long para qué sirve
            db.close();
        }catch (Exception ex){
            ex.toString();
        }

    }
    public List<PlayerHistory> getAllJugadores(){
        List<PlayerHistory> playerHistoryModelsList = new ArrayList<>();
        String query = "SELECT * FROM "+TABLE_JUGADORES+ " ORDER BY "+ COLUMN_PUNTUACION+ " DESC ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String nombre = cursor.getString(1);
                int puntuacion = cursor.getInt(2);
                String fechaStr = cursor.getString(3);

                Date fecha = null;
                PlayerHistory playerHistory = new PlayerHistory(id, nombre, puntuacion, fechaStr);
                playerHistoryModelsList.add(playerHistory);
            } while (cursor.moveToNext());
        }
        db.close();
        return playerHistoryModelsList;
    }
    public List<PlayerHistory> getTop3(){
        List<PlayerHistory> playerHistoryModelsList = new ArrayList<>();
        String query = "SELECT * FROM "+TABLE_JUGADORES+ " ORDER BY "+ COLUMN_PUNTUACION+ " DESC LIMIT 3 ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String nombre = cursor.getString(1);
                int puntuacion = cursor.getInt(2);
                String fechaStr = cursor.getString(3);
                PlayerHistory playerHistory = new PlayerHistory(id, nombre, puntuacion, fechaStr);
                playerHistoryModelsList.add(playerHistory);
            } while (cursor.moveToNext());
        }
        db.close();
        return playerHistoryModelsList;
    }
}
