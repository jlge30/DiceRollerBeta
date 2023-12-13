package com.jose.diceroller.db;

public class Bote {

    public static final String TABLE_BOTE = "boteMonedas";

    public static final String PUNTOS= "puntosBote";

    public static final String ID_DOCUMENTO = "HGwkEGg6rHPfrD2D7QPI"; //asignado de manera automática por firestore



    private String puntosBote;

    public Bote() {
    }

    public String getPuntosBote() {
        return puntosBote;
    }

    public void setPuntosBote(String puntosBote) {
        this.puntosBote = puntosBote;
    }

    public Bote(String puntosBote) {
        this.puntosBote = puntosBote;
    }
}
