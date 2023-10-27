package com.jose.diceroller.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.jose.diceroller.PantallaFinal;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
// Insertar los datos en la base de datos
        long newRowId = database.insert(TABLE_PUNTUACION, null, values);
        try {
// Comprobar si la inserción fue exitosa
            if (newRowId != -1) {
                Log.d("MiApp", "Puntuación guardada correctamente. newRowId = " + newRowId);
                Toast.makeText(context, "Puntuación de "+item.getName()+" guardada", Toast.LENGTH_LONG).show();
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<DataItem> DescargarPuntuaciones() {
        List<DataItem> dataItems = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try {
            String[] projection = {
                    "Nombre",
                    "Fecha",
                    "Puntuacion"
            };

            Cursor cursor = db.query(TABLE_PUNTUACION, projection, null, null, null, null, null);
            int nameColumnIndex = cursor.getColumnIndex("Nombre");
            int fechaColumnIndex = cursor.getColumnIndex("Fecha");
            int scoreColumnIndex = cursor.getColumnIndex("Puntuacion");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    if (nameColumnIndex != -1 && fechaColumnIndex != -1 && scoreColumnIndex != -1) {
                        String name = cursor.getString(nameColumnIndex);

                        String fechaString = cursor.getString(fechaColumnIndex);
                        String[] partes = fechaString.split("/");
                        // Reorganizar la fecha en formato ISO 8601
                        String fechaISO = partes[2] + "-" + partes[1] + "-" + partes[0];
                        // Analizar la fecha en formato ISO 8601
                        LocalDate fecha = LocalDate.parse(fechaISO);
                        int score = cursor.getInt(scoreColumnIndex);
                        DataItem dataItem = new DataItem(name, fecha, score);
                        dataItems.add(dataItem);
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return dataItems;
    }
}

