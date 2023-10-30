package com.jose.diceroller.db;

public class PlayerHistory {
    public static final String TABLE_JUGADORES = "jugadores";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_PUNTUACION = "puntuacion";
    public static final String COLUMN_FECHA = "fecha";
    private int id;
    private String nombre;
    private int puntuacion;
    private String fecha;


    public PlayerHistory(){

    }

    public PlayerHistory(String nombre, int puntuacion, String fecha) {
        this.nombre = nombre;
        this.puntuacion = puntuacion;
        this.fecha = fecha;
    }
    public PlayerHistory(int id, String nombre, int puntuacion, String fecha) {
        this.nombre = nombre;
        this.puntuacion = puntuacion;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "DbJugadores{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", puntuacion=" + puntuacion +
                ", fecha=" + fecha +
                '}';
    }

}
