package com.jose.diceroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.Timer;
import java.util.TimerTask;

public class Bienvenida extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida);

        TimerTask tarea =new TimerTask() {//creamos la tarea de la ventana bienvenida
            @Override
            public void run() {//tarea para que la pantalla esté visible 3 segundos
                Intent intent = new Intent(Bienvenida.this, MenuInicial.class);
                startActivity(intent);
                finish();
            }
        };
        Timer tiempo = new Timer();//retardamos la otra ventana
        tiempo.schedule(tarea, 3000);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();
        if ( id == R.id.menu_inicio){
            Intent intent = new Intent(Bienvenida.this,MenuInicial.class);
            startActivity(intent);
            finish();
            //Toast.makeText(this, "Has clicado la primera opcion", Toast.LENGTH_SHORT).show();
        }else if ( id == R.id.menu_salir){
            finish();
        }
        else if(id == R.id.menu_salir){
            finish();
        }
        return true;
    }
}