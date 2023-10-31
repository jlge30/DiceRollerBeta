package com.jose.diceroller;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.jose.diceroller.db.DbManager;
import com.jose.diceroller.db.PlayerHistory;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MenuInicial extends AppCompatActivity {

    //atributos
    private Button btnJugar, btnayuda, btnSalir;
    private LinearLayout linearLayoutJugadores;
    private DbManager dbManager;//instancia gestíon de la BBDD
    private TextView txtTopThree;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_inicial);
        btnJugar = findViewById(R.id.btn_jugar);
        linearLayoutJugadores = findViewById(R.id.linear_layout_jugadores);
        txtTopThree = findViewById(R.id.txt_top3);
        dbManager = new DbManager(this);
        btnayuda = findViewById(R.id.btn_ayuda);
        btnSalir = findViewById(R.id.btn_salir_juego);

        listarJugadorRx();//funcion para listar con RxJava
        btnJugar.setOnClickListener(new View.OnClickListener() {//pasar a la siguiente ventana
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(MenuInicial.this, MainActivity.class);
                startActivity(intent);
            }
        });
        //boton salir app
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //boton menú ayuda
        btnayuda.setOnClickListener(new View.OnClickListener() {//pasar a la ventana ayuda
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(MenuInicial.this, VentanaAyuda.class);
                startActivity(intent);
            }
        });

    }
    //añadimos el menu a la barra de herramientas
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_inicio, menu);
        return true;
    }
    @SuppressLint("CheckResult")
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();
        if ( id == R.id.menu_borrarBD){
            mostrarDialogoDeConfirmacion();
//            eliminarRegistros();
//            finish();
            //Toast.makeText(this, "Has clicado la primera opcion", Toast.LENGTH_SHORT).show();
        }else if ( id == R.id.menu_ayuda){
            Intent intent = new Intent(MenuInicial.this,VentanaAyuda.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.menu_ver_all){
            Intent intent = new Intent(MenuInicial.this,VentanaTodosRegistros.class);
            startActivity(intent);
            finish();
        }
        else if(id == R.id.menu_salir){
            finish();
        }
        return true;
    }

    //función listar jugadores con RxJava
    @SuppressLint("CheckResult")
    private void listarJugadorRx(){
        dbManager.getTopThree()//lista de los tres primeros
                .subscribeOn(Schedulers.io()) // Ejecuta la consulta en un hilo diferente
                .observeOn(AndroidSchedulers.mainThread()) // Recibe el resultado en el hilo principal
                .subscribe(new SingleObserver<List<PlayerHistory>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull List<PlayerHistory> playerHistories) {//nos devuelve listado de jugadores
                        int posicion = 1;
                        for (PlayerHistory playerHistory : playerHistories) {
                            int color = getResources().getColor(R.color.blue);
                            TextView textView = new TextView(MenuInicial.this);//creamos un textview
                            textView.setTextSize(20);
                            textView.setTextColor(color);
                            Typeface typeface = textView.getTypeface();
                            textView.setTypeface(Typeface.create(typeface, Typeface.BOLD));
                            textView.setText(posicion+"- "+ playerHistory.getNombre() +" "+ playerHistory.getPuntuacion()+ " monedas\n"+ playerHistory.getFecha());
                            linearLayoutJugadores.addView(textView);//añadimos los jugadores al Layout
                            posicion ++;
                        }
                    }
                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(MenuInicial.this, "Error al listar", Toast.LENGTH_SHORT).show();

                        Log.e("Visualizacion","Error al listar", e);

                    }
                });

    }

    //funcion eliminación BBDD con RxJava
    @SuppressLint("CheckResult")
    public void eliminarRegistros(){
        dbManager.deleteAllJugadores()
                .subscribe(deletedRows -> {
                    if (deletedRows > 0) {
                        Toast.makeText(MenuInicial.this, "Registros eliminados correctamente",
                                Toast.LENGTH_SHORT).show();
                        // Todos los registros se eliminaron con éxito
                    } else {
                        Toast.makeText(MenuInicial.this, "Registros no eliminados",
                                Toast.LENGTH_SHORT).show();
                        // No se encontraron registros para eliminar
                    }
                }, throwable -> {
                    Toast.makeText(MenuInicial.this, "Error en la eliminacion",
                            Toast.LENGTH_SHORT).show();
                    // Ocurrió un error durante la eliminación
                });

        Intent intent = new Intent(MenuInicial.this,MenuInicial.class);
        startActivity(intent);

    }
    //función para confirmar la eliminación de todos los registros de la BBDD
    private void mostrarDialogoDeConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirmación");
        builder.setMessage("¿Estás seguro de que deseas continuar?");

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Código para la acción afirmativa
                eliminarRegistros();
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

}