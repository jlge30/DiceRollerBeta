package com.jose.diceroller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PantallaFinal extends AppCompatActivity {

    private TextView textView;
    private EditText editText;

    private TextView txtNombre;
    private GlobalVariables datos;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        datos = (GlobalVariables) getApplicationContext();//instanciamos la variable global
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_final);
        textView = findViewById(R.id.txt_puntos);//localizamos el txt de los puntos
        String puntos = "Monedas ganadas: " + String.valueOf(datos.getPuntuacion());//mostramos el valor de la puntuación global
        textView.setText(puntos);//mensaje de número de monedas

    }

}