package com.jose.diceroller.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.jose.diceroller.PantallaFinal;

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
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void subirPuntuacion(DataItem item, Context context){
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Fecha", item.getDateString());
        values.put("Nombre", item.getName());
        values.put("Puntuacion", (Integer)item.getScore());
        Toast.makeText(context, "fecha "+item.getDateString(), Toast.LENGTH_LONG).show();
        Toast.makeText(context, "Puntos "+item.getScore(), Toast.LENGTH_LONG).show();
        Toast.makeText(context, "Nombre "+item.getName(), Toast.LENGTH_LONG).show();
        Toast.makeText(context, "Tabla "+TABLE_PUNTUACION, Toast.LENGTH_LONG).show();
// Insertar los datos en la base de datos
        long newRowId = database.insert(TABLE_PUNTUACION, null, values);
        try {
// Comprobar si la inserción fue exitosa
            if (newRowId != -1) {
                Log.d("MiApp", "Puntuación guardada correctamente. newRowId = " + newRowId);
                Toast.makeText(context, "Puntuación guardada", Toast.LENGTH_LONG).show();
            } else {
                Log.e("MiApp", "Error al guardar la puntuación.");
                Toast.makeText(context, "Error al guardar la puntuación", Toast.LENGTH_LONG).show();
            }
            // Cerrar la base de datos cuando hayas terminado
            database.close();
        }catch(Exception e) {
            Log.e("MiApp", "Error al guardar la puntuación: " + e.getMessage());
        }
    }
}
