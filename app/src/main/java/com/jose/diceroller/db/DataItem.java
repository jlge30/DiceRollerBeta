package com.jose.diceroller.db;

import android.annotation.SuppressLint;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.jose.diceroller.PantallaFinal;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DataItem {
    private String name;
    private LocalDate fecha;
    private int score;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public DataItem(String name, LocalDate fecha, int score) {
        this.name = name;
        this.fecha = LocalDate.now();
        this.score = score;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDateString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Puedes ajustar el patrón según tus necesidades
        String fechaComoCadena = (fecha).format(formatter);
        return fechaComoCadena;
    }
    public LocalDate getDate() {
        return fecha;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDate TansformStringToLocalDate(String fecha){
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate fechaLocal = LocalDate.parse(fecha, formato);
        return fechaLocal;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}

