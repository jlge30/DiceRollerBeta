package com.jose.diceroller;

import android.app.Application;
/*
Clase creada para acumular la puntuación de una pantalla a otra
 */
public class GlobalVariables extends Application{


    private int puntuacion;

    public int getPuntuacion(){
        return puntuacion;
    }
    public void setPuntuacion(int puntuacion){
        this.puntuacion = puntuacion;
    }
}
