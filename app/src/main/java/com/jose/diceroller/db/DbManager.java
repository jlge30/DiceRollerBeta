package com.jose.diceroller.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class DbManager {

    private DbHelper dbHelper;

    public DbManager(Context context) {
        dbHelper= new DbHelper(context);//instanciamos la clase DbHelper para manipular la BBDD con Sqlite
    }
    //insercion del jugador con RXJava
    public Single<Long> insertJugador(String nombre, int puntuacion, String fecha) {
        return Single.fromCallable(() -> {
            ContentValues values = new ContentValues();
            values.put(PlayerHistory.COLUMN_NOMBRE, nombre);
            values.put(PlayerHistory.COLUMN_PUNTUACION, puntuacion);
            values.put(PlayerHistory.COLUMN_FECHA, fecha);
            long id = dbHelper.getWritableDatabase().insert(PlayerHistory.TABLE_JUGADORES, null, values);
            dbHelper.close();
            return id;
        });
    }
    //listado todos jugadores RXJava
    public Single<List<PlayerHistory>> getAllJugadores() {
        return Single.fromCallable(() -> {
            List<PlayerHistory> jugadores = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getReadableDatabase();//ponemos la BBDD en lectura
            Cursor cursor = db.query(PlayerHistory.TABLE_JUGADORES, null, null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(PlayerHistory.COLUMN_ID);
                int nameIndex = cursor.getColumnIndex(PlayerHistory.COLUMN_NOMBRE);
                int scoreIndex = cursor.getColumnIndex(PlayerHistory.COLUMN_PUNTUACION);
                int fechaIndex = cursor.getColumnIndex(PlayerHistory.COLUMN_FECHA);
                do {
                    if(idIndex != -1 && nameIndex != -1 && scoreIndex != -1 && fechaIndex != -1 ){
                        int id = cursor.getInt(idIndex);
                        String nombre = cursor.getString(nameIndex);
                        int score = cursor.getInt(scoreIndex);
                        String fecha = cursor.getString(fechaIndex);
                        PlayerHistory playerHistory = new PlayerHistory(id, nombre, score, fecha);
                        jugadores.add(playerHistory);
                    }
                } while (cursor.moveToNext());

                cursor.close();
            }

            dbHelper.close();//cerramos la BBDD
            return jugadores;
        });
    }
    //listado los tres mejores con RXJava
    public Single<List<PlayerHistory>> getTopThree() {
        return Single.fromCallable(() -> {
            List<PlayerHistory> jugadores = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getReadableDatabase();//ponemos la BBDD en lectura
            String[] projection = null; //seleccionar todas las columnas
            String selection = null; //no aplicamos ninguna condición WHERE
            String[] selectionArgs = null; //no utilizar argumentos de selección
            String sortOrder = PlayerHistory.COLUMN_PUNTUACION+" DESC"; // ordenado por puntuación de forma descendente
            String limit = "3"; // Limita los resultados a 3
            Cursor cursor = db.query(PlayerHistory.TABLE_JUGADORES, projection, selection, selectionArgs, null,null,sortOrder, limit);

            if (cursor != null && cursor.moveToFirst()) {//obtenemos los datos
                int idIndex = cursor.getColumnIndex(PlayerHistory.COLUMN_ID);
                int nameIndex = cursor.getColumnIndex(PlayerHistory.COLUMN_NOMBRE);
                int scoreIndex = cursor.getColumnIndex(PlayerHistory.COLUMN_PUNTUACION);
                int fechaIndex = cursor.getColumnIndex(PlayerHistory.COLUMN_FECHA);
                do {
                    if(idIndex != -1 && nameIndex != -1 && scoreIndex != -1 && fechaIndex != -1 ){
                        int id = cursor.getInt(idIndex);
                        String nombre = cursor.getString(nameIndex);
                        int score = cursor.getInt(scoreIndex);
                        String fecha = cursor.getString(fechaIndex);
                        PlayerHistory playerHistory = new PlayerHistory(id, nombre, score, fecha);
                        jugadores.add(playerHistory);//añadimos los jugadores
                    }
                } while (cursor.moveToNext());

                cursor.close();
            }

            dbHelper.close();
            return jugadores;
        });
    }

    //eliminacion todos los jugadores con RXJava
    public Single<Integer> deleteAllJugadores() {
        return Single.fromCallable(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();//ponemos la bbdd escribible
            int rowsDeleted = db.delete(PlayerHistory.TABLE_JUGADORES, null, null);
            dbHelper.close();
            return rowsDeleted;
        });
    }

}
