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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.jose.diceroller.db.DbManager;

public class Ajustes extends AppCompatActivity {
    private Button borrarBBDD;
    private DbManager dbManager;//instancia gestíon de la BBDD


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
        borrarBBDD= findViewById(R.id.btn_borrarBBDD);
        dbManager = new DbManager(this);

        borrarBBDD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoDeConfirmacion();
            }
        });
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
            Intent intent = new Intent(Ajustes.this,MenuInicial.class);
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
    //funcion eliminación BBDD con RxJava
    @SuppressLint("CheckResult")
    public void eliminarRegistros(){
        dbManager.deleteAllJugadores()
                .subscribe(deletedRows -> {
                    if (deletedRows > 0) {
                        Toast.makeText(Ajustes.this, "Registros eliminados correctamente",
                                Toast.LENGTH_SHORT).show();
                        // Todos los registros se eliminaron con éxito
                    } else {
                        Toast.makeText(Ajustes.this, "Registros no eliminados",
                                Toast.LENGTH_SHORT).show();
                        // No se encontraron registros para eliminar
                    }
                }, throwable -> {
                    Toast.makeText(Ajustes.this, "Error en la eliminacion",
                            Toast.LENGTH_SHORT).show();
                    // Ocurrió un error durante la eliminación
                });

        Intent intent = new Intent(Ajustes.this,MenuInicial.class);
        startActivity(intent);

    }
}