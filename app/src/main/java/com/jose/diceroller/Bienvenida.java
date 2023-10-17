package com.jose.diceroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class Bienvenida extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida);



        TimerTask tarea =new TimerTask() {//creamos la tarea de la ventana bienvenida
            @Override
            public void run() {//tarea para que la pantalla esté visible 5 segundos
                Intent intent = new Intent(Bienvenida.this, MenuInicial.class);
                startActivity(intent);
                finish();
            }
        };
        Timer tiempo = new Timer();//retardamos la otra ventana
        tiempo.schedule(tarea, 5000);
    }
}