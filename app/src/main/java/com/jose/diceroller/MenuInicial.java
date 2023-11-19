package com.jose.diceroller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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

    private MediaPlayer mediaPlayer;

    //atributos
    private Button btnJugar, btnSalir;

    private DbManager dbManager;//instancia gestíon de la BBDD
    private TextView txtTopThree;

    private GlobalVariables datos;

    public static final int REQUEST_CODE = 1;

    @SuppressLint({"MissingInflatedId", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        datos = (GlobalVariables) getApplicationContext();//instanciamos la variable global
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_inicial);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        btnJugar = findViewById(R.id.btn_jugar);
        txtTopThree = findViewById(R.id.txt_top3);
        dbManager = new DbManager(this);
        btnSalir = findViewById(R.id.btn_salir_juego);
        listarTopThree();

        //MUSICA:
        mediaPlayer = MediaPlayer.create(this, R.raw.musica01);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

/*
        if (checkLocationPermission()) { obtainLocation();
            // Para ejecutar la tarea en segundo plano, no funciona en los emuladores
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
               new LocationTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new LocationTask().execute();
           }
        } else {
            requestLocationPermission();
        }
        */
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

    /**
     * función para obtener localización.
     */
    /*
    @SuppressLint("SetTextI18n")
    private void obtainLocation(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return;
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null){
                datos.setLatitud(location.getLatitude());
                datos.setLongitud(location.getLongitude());
            }else{
                Toast.makeText(MenuInicial.this, "No se ha podido obtener la ubicacion", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean checkLocationPermission(){
        int permissionState = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }
    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE );
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @androidx.annotation.NonNull String[] permissions, @androidx.annotation.NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ( requestCode == REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                obtainLocation();
            }else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //función asincrona para llamar a la funcion de localización.
    private class LocationTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            // Llamada a la función de localización.
            obtainLocation();
            return null;
        }
    }
    */
/*
    @Override
    protected void onResume() {
        super.onResume();
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();

        }
    }*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer=null;
    }


}