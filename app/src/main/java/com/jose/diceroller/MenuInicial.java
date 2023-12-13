package com.jose.diceroller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jose.diceroller.db.DbManager;
import com.jose.diceroller.db.ListAdapterdb;
import com.jose.diceroller.db.PlayerHistory;
import com.jose.diceroller.db.PlayerHistorydb;

import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MenuInicial extends AppCompatActivity {

    //variables de firebase y aut. google
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

//    private GoogleSignInClient gsc;
//    private GoogleSignInOptions gso;


    //atributos
    private Button btnJugar, btnSalir;

    private DbManager dbManager;//instancia gestíon de la BBDD
    private TextView txtTopThree;

    private GlobalVariables datos;

    public static final int REQUEST_CODE = 1;

    // Declaraciones para el cambio de idioma
    private Spinner spinner;
    public static final String[] languages = {" ", "ES", "EN", "CAT"};

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


        inicicalizarFireBase();//iniciamos firebase

//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
//        gsc = GoogleSignIn.getClient(this, gso);
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if (account != null){// si no hemos iniciado por google nos vamos a obtener el nombre del usuario creado
//            String persone = account.getDisplayName();
//            String email = account.getEmail();
//            datos.setNombreJugador(persone);//obtenemos el nombre del usuario de google
//        }

        listarTopThree();

            // Para ejecutar la tarea en segundo plano

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                new LocationTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            } else {
//                new LocationTask().execute();
//            }
        if (checkLocationPermission()) { obtainLocation();
            // Para ejecutar la tarea en segundo plano, no funciona en los emuladores
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//               new LocationTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            } else {
//                new LocationTask().execute();
//           }
        } else {
            requestLocationPermission();
        }


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

                signOut();//salimos de la cuenta de google
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

            signOut();
        }
        return true;
    }


    //función listar jugadores con RxJava
    public void listarTopThree() {
        dbManager.getTopThree()
                .subscribeOn(Schedulers.io()) // Ejecuta la consulta en un hilo diferente
                .observeOn(AndroidSchedulers.mainThread()) // Recibe el resultado en el hilo principal
                .subscribe(new SingleObserver<List<PlayerHistorydb>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull List<PlayerHistorydb> playerHistories) {
                        ListAdapterdb listAdapterdb = new ListAdapterdb(playerHistories, MenuInicial.this);

                        androidx.recyclerview.widget.RecyclerView recyclerView = findViewById(R.id.listTopThree);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MenuInicial.this));
                        recyclerView.setAdapter(listAdapterdb);
                        
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
    }
    /**
     * función para obtener localización.
     */

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

    /**
     * funcion para iniciar firebase
     */
    private void inicicalizarFireBase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();


    }

    /**
     * obtenemos el nombre del usuario registrado con mail y contraseña
     */
    /**
     * función para salir de la cuenta de google
     */
    private void signOut() {

                finish();
                startActivity(new Intent(MenuInicial.this, LoginActivity.class));

    }

}