package com.jose.diceroller;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jose.diceroller.db.DbManager;
import com.jose.diceroller.db.PlayerHistory;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class VentanaTodosRegistros extends AppCompatActivity {
    private LinearLayout linearLayoutJugadores;
    private DbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_todos_registros);
        linearLayoutJugadores = findViewById(R.id.linear_layout_jugadores);
        dbManager = new DbManager(this);
        listarJugadorRx();
    }

    //función listar jugadores con RxJava
    @SuppressLint("CheckResult")
    private void listarJugadorRx(){
        dbManager.getAllJugadores()//todos los jugadores
                .subscribeOn(Schedulers.io()) // Ejecuta la consulta en un hilo diferente
                .observeOn(AndroidSchedulers.mainThread()) // Recibe el resultado en el hilo principal
                .subscribe(new SingleObserver<List<PlayerHistory>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d(TAG, "onSubscribe: called:");

                    }

                    @Override
                    public void onSuccess(@NonNull List<PlayerHistory> playerHistories) {
                        for (PlayerHistory playerHistory : playerHistories) {
                            TextView textView = new TextView(VentanaTodosRegistros.this);
                            textView.setTextSize(15);
                            //textView.setTextColor(color);
                            Typeface typeface = textView.getTypeface();
                            textView.setTypeface(Typeface.create(typeface, Typeface.BOLD));
                            textView.setText(playerHistory.getNombre() +" "+ playerHistory.getPuntuacion()+ " monedas fecha: "+ playerHistory.getFecha());
                            linearLayoutJugadores.addView(textView);
                        }
                        Log.d(TAG, "onSuccess: " + Thread.currentThread().getName());


                    }
                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(VentanaTodosRegistros.this, "Error al listar", Toast.LENGTH_SHORT).show();

                        Log.e(TAG, "onError: ",e );

                    }
                });


    }
    //añadimos la barra de menu
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();
        if ( id == R.id.menu_inicio){
            Intent intent = new Intent(VentanaTodosRegistros.this,MenuInicial.class);
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