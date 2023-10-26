package com.jose.diceroller;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jose.diceroller.db.DataAdapter;
import com.jose.diceroller.db.DataItem;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentanaPuntuaciones extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DataAdapter dataAdapter;

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
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<DataItem> getDataFromSQLite() {

        List<DataItem> dataItems = new ArrayList<>();
        dataItems.add(new DataItem("Nombre1", LocalDate.now(), 100));
        dataItems.add(new DataItem("Nombre2", LocalDate.now(), 200));
        // ...
        return dataItems;
    }
}
