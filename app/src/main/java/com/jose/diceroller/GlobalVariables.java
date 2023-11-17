package com.jose.diceroller;

import android.app.Application;
/*
Clase creada para acumular la puntuación de una pantalla a otra así como la localización
 */
public class GlobalVariables extends Application{
    private double latitud = 0;
    private double longitud = 0;
    private int puntuacion;
    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }
    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
    public int getPuntuacion(){
        return puntuacion;
    }
    public void setPuntuacion(int puntuacion){
        this.puntuacion = puntuacion;
    }
}
