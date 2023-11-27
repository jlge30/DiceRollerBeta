package com.jose.diceroller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jose.diceroller.db.DbManager;
import com.jose.diceroller.db.ListAdapter;
import com.jose.diceroller.db.PlayerHistory;

import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MenuInicial extends AppCompatActivity {

    //atributos
    private Button btnJugar, btnSalir;

    private DbManager dbManager;//instancia gestíon de la BBDD
    private TextView txtTopThree;

    // Declaraciones para el cambio de idioma
    private Spinner spinner;
    public static final String[] languages = {" ", "ES", "EN", "CAT"};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_inicial);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btnJugar = findViewById(R.id.btn_jugar);
        txtTopThree = findViewById(R.id.txt_top3);
        dbManager = new DbManager(this);
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

        // IDIOMAS
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLang = parent.getItemAtPosition(position).toString();
                if (selectedLang.equals("ES")){
                    setLocal(MenuInicial.this, "es");
                    finish();
                    startActivity(getIntent());
                } else if (selectedLang.equals("EN")) {
                    setLocal(MenuInicial.this, "en");
                    finish();
                    startActivity(getIntent());
                } else if (selectedLang.equals("CAT")) {
                    setLocal(MenuInicial.this, "cat");
                    finish();
                    startActivity(getIntent());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    // Método para cambiar el idioma
    public void setLocal(Activity activity, String langCode){
        Locale locale = new Locale(langCode);
        locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config,resources.getDisplayMetrics());
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
        if ( id == R.id.menu_ajustes){

            Intent intent = new Intent(MenuInicial.this,Ajustes.class);
            startActivity(intent);
            finish();

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