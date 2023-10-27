package com.jose.diceroller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jose.diceroller.db.DataAdapter;
import com.jose.diceroller.db.DataItem;
import com.jose.diceroller.db.DbHelper;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentanaPuntuaciones extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DataAdapter dataAdapter;
    private DbHelper dbh = new DbHelper(VentanaPuntuaciones.this);
    private Button btnVolver, btnBorrarDatos;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_puntuaciones);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        // Configurar el administrador de diseño y el adaptador
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        dataAdapter = new DataAdapter(getDataFromSQLite()); // Obtener datos de SQLite
        recyclerView.setAdapter(dataAdapter);
        btnVolver = findViewById(R.id.btnVolverAlMenu);
        btnBorrarDatos = findViewById(R.id.btnBorrarPuntuaciones);
        btnVolver.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VentanaPuntuaciones.this, MenuInicial.class);
                startActivity(intent);
                finish();
            }
        });
        btnBorrarDatos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(VentanaPuntuaciones.this);
                builder.setMessage("¿Estás seguro que deseas eliminar los datos?");
                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbh.BorrarDatos();
                        recreate();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Cierra el cuadro de diálogo
                    }
                });
                builder.create().show();
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<DataItem> getDataFromSQLite() {
        List<DataItem> dataItems= new ArrayList<>();
        dataItems = dbh.DescargarPuntuaciones();
        return dataItems;
    }
}
