package com.jose.diceroller;

import android.app.Application;

import java.util.Date;

public class GlobalVariables extends Application{
    private String nombre;

    private int puntuacion;

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    private Date fecha;

    public  String getNombre(){
        return nombre;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public int getPuntuacion(){
        return puntuacion;
    }
    public void setPuntuacion(int puntuacion){
        this.puntuacion = puntuacion;
    }
}
