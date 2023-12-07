package com.jose.diceroller.db;

public class PlayerHistorydb {
    //atributos del jugador
    public static final String TABLE_JUGADORES = "jugadores";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_PUNTUACION = "puntuacion";
    public static final String COLUMN_FECHA = "fecha";

    public static final String COLUMN_LATITUD = "latitud";

    public static final String COLUMN_LONGITUD = "longitud";




    private int id;
    private String nombre;
    private int puntuacion;
    private String fecha;

    private double latitud;

    private double longitud;

    //constructores
    public PlayerHistorydb(){

    }

    public PlayerHistorydb(String nombre, int puntuacion, String fecha) {
        this.nombre = nombre;
        this.puntuacion = puntuacion;
        this.fecha = fecha;
    }
    public PlayerHistorydb(int id, String nombre, int puntuacion, String fecha) {
        this.id = id;
        this.nombre = nombre;
        this.puntuacion = puntuacion;
        this.fecha = fecha;
    }

    public PlayerHistorydb(int id, String nombre, int puntuacion, String fecha, double latitud, double longitud) {
        this.id = id;
        this.nombre = nombre;
        this.puntuacion = puntuacion;
        this.fecha = fecha;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(float latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
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
