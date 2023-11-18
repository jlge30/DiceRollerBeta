package com.jose.diceroller;
import androidx.core.content.*;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.provider.MediaStore;
import android.Manifest;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.jose.diceroller.db.DbManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;


public class PantallaFinal extends AppCompatActivity {

    private TextView textView;
    private EditText gamerN;
    private EditText editText;

    private View vista;
    private TextView txtPuntuacion;
    private GlobalVariables datos;
    private Button saveName, btnSalir, btnInicio;

    private DbManager dbManager;

    private static final int REQ_PERMISSION = 1;
    private static final int REQUEST_CODE_PERMISO_UBICACION_MEDIOS = 100;

    private static final int REQUEST_CODE_PERMISO_ESCRIBIR_EXTERNO = 100;
    private Button boton;
    private String[] permission = new String[]{
            Manifest.permission.ACCESS_MEDIA_LOCATION
    };

    private static final int REQUEST_MEDIA_PROJECTION = 1;

    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;

    private static final int PERMISSION_REQUEST_CODE = 1;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_pantalla_final);
        datos = (GlobalVariables) getApplicationContext();//instanciamos la variable global
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_final);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        vista = findViewById(R.id.SaveButton);
        if (!vista.isDrawingCacheEnabled()) {
            vista.setDrawingCacheEnabled(true);
        }
        vista.setDrawingCacheEnabled(true);
        dbManager = new DbManager(this);
        textView = findViewById(R.id.txt_puntos);//localizamos el txt de los puntos
        gamerN = findViewById(R.id.txtGamerN);
        String puntos = "Monedas ganadas: " + String.valueOf(datos.getPuntuacion());//mostramos el valor de la puntuación global
        textView.setText(puntos);//mensaje de número de monedas
        saveName = findViewById(R.id.SaveButton);
        txtPuntuacion = findViewById(R.id.txtScoreTitle);
        btnInicio = findViewById(R.id.btn_volver_jugar);
        btnSalir = findViewById(R.id.btn_salir);
        saveName.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {


                if (gamerN.getText().toString().isEmpty()) {//comprobamos que la caja de texto nombre tiene datos
                    Toast.makeText(PantallaFinal.this, "Introduce el nombre", Toast.LENGTH_SHORT).show();

                } else {

                    insertJugadorRx();
                    gamerN.setText("");//vaciamos la caja de texto
                    datos.setPuntuacion(0);//dejamos la puntuación a 0
                    textView.setVisibility(View.INVISIBLE);//dejamos invisibles la puntuacion y el texto de título
                    txtPuntuacion.setVisibility(View.INVISIBLE);
                    saveName.setEnabled(false);//deshabilitamos el botón de guardar
                    tomarCapturaDePantalla();

                }


            }
        });
        //botón salir
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PantallaFinal.this, MenuInicial.class);
                startActivity(intent);
                finish();
            }
        });

    }


    //Insertar jugador con RxJava
    public void insertJugadorRx() {
        String nombre = gamerN.getText().toString();
        int puntuacion = datos.getPuntuacion();
        Date fecha = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Ajusta el formato
        String fechaStr = dateFormat.format(fecha);
        double latitud = datos.getLatitud();
        double longitud = datos.getLongitud();
        dbManager.insertJugador(nombre, puntuacion, fechaStr, latitud, longitud)
                .subscribeOn(Schedulers.io()) // Ejecuta la inserción en un hilo diferente
                .observeOn(AndroidSchedulers.mainThread()) // Recibe el resultado en el hilo principal
                .subscribe(new SingleObserver<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Long id) {
                        // La inserción fue exitosa, puedes manejar el resultado aquí (id es la clave primaria generada)
                        Log.d("InsertarRegistros", "Jugada registrada con éxito, ID: " + id);
                        Toast.makeText(PantallaFinal.this, "Registro insertado con exito", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        // Hubo un error al insertar, puedes manejar el error aquí
                        Log.e("InsertarRegistros", "Error al insertar persona", e);
                        Toast.makeText(PantallaFinal.this, "Registro NOO insertado", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //añadimos el menún
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();
        if (id == R.id.menu_inicio) {
            Intent intent = new Intent(PantallaFinal.this, MenuInicial.class);
            startActivity(intent);
            finish();
            //Toast.makeText(this, "Has clicado la primera opcion", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_salir) {
            finish();
        } else if (id == R.id.menu_salir) {
            finish();
        }
        return true;
    }

    private void reqPermission() {
        if (ActivityCompat.checkSelfPermission(this, permission[0]) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, permission, REQ_PERMISSION);
        }
    }


    //======================================================

    public void tomarCapturaDePantalla() {

        // Solicita permiso para acceder a la ubicación de los medios
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Solicita el permiso
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_MEDIA_LOCATION}, REQUEST_CODE_PERMISO_UBICACION_MEDIOS);
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Solicita el permiso
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISO_ESCRIBIR_EXTERNO);
            return;
        }

        // Crea la carpeta dashrolls
        File directorioDashrolls = new File(Environment.getExternalStorageDirectory(), "dashrolls");
        if (!directorioDashrolls.exists()) {
            directorioDashrolls.mkdirs();
        }


// Toma la captura de pantalla
        Bitmap capturaDePantalla = vista.getDrawingCache();

// Genera un nombre para la captura de pantalla
        int numero = (int) (Math.random() * 1000000);
        String nombreCaptura = "dash_roll_" + numero + ".png";

// Crea el archivo para la captura de pantalla
        File archivoCaptura = new File(directorioDashrolls, nombreCaptura);
        try {
            archivoCaptura.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

// Imprime el valor de archivoCaptura
        Log.d("MY APP", archivoCaptura.toString());

// Escribe la captura de pantalla en el archivo
        try (FileOutputStream fos = new FileOutputStream(archivoCaptura)) {
            capturaDePantalla.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }

//Otorga permisos de escritura al archivo
        archivoCaptura.setWritable(true);

    }
}






