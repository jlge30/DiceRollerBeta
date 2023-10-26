package com.jose.diceroller;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jose.diceroller.db.DataAdapter;
import com.jose.diceroller.db.DataItem;

import java.util.ArrayList;
import java.util.List;

public class VentanaPuntuaciones extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DataAdapter dataAdapter;

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

    // Método para obtener datos de SQLite (debes implementarlo)
    private List<DataItem> getDataFromSQLite() {
        // Aquí debes escribir la lógica para obtener datos de tu base de datos SQLite
        // y convertirlos en objetos DataItem.
        // Luego, devuelve una lista de objetos DataItem.
        // Ejemplo:
        List<DataItem> dataItems = new ArrayList<>();
        dataItems.add(new DataItem("Nombre1", 100));
        dataItems.add(new DataItem("Nombre2", 200));
        // ...
        return dataItems;
    }
}
