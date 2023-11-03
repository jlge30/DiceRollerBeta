package com.jose.diceroller;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jose.diceroller.db.DbManager;
import com.jose.diceroller.db.ListAdapter;
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

    private DbManager dbManager;//instancia gestíon de la BBDD
    private TextView txtTopThree;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_inicial);
        btnJugar = findViewById(R.id.btn_jugar);
        txtTopThree = findViewById(R.id.txt_top3);
        dbManager = new DbManager(this);
        btnayuda = findViewById(R.id.btn_ayuda);
        btnSalir = findViewById(R.id.btn_salir_juego);
        listarTopThree();
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
            Intent intent = new Intent(MenuInicial.this,RecyclerView.class);
            startActivity(intent);
            finish();
        }
        else if(id == R.id.menu_salir){
            finish();
        }
        return true;
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
        builder.setMessage("¿Estás seguro de que deseas eliminar todos los registros?");

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
    //función listar jugadores con RxJava
    public void listarTopThree() {
        dbManager.getTopThree()
                .subscribeOn(Schedulers.io()) // Ejecuta la consulta en un hilo diferente
                .observeOn(AndroidSchedulers.mainThread()) // Recibe el resultado en el hilo principal
                .subscribe(new SingleObserver<List<PlayerHistory>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull List<PlayerHistory> playerHistories) {
                        ListAdapter listAdapter = new ListAdapter(playerHistories, MenuInicial.this);

                        androidx.recyclerview.widget.RecyclerView recyclerView = findViewById(R.id.listTopThree);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MenuInicial.this));
                        recyclerView.setAdapter(listAdapter);

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
    }

}