package com.jose.diceroller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

import com.jose.diceroller.db.DbManager;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PantallaFinal extends AppCompatActivity {

    private TextView textView;
    private EditText gamerN;
    private EditText editText;

    private TextView txtPuntuacion;
    private GlobalVariables datos;
    private Button saveName, btnSalir, btnInicio;

    private DbManager dbManager;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        datos = (GlobalVariables) getApplicationContext();//instanciamos la variable global
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_final);
        dbManager = new DbManager(this);
        textView = findViewById(R.id.txt_puntos);//localizamos el txt de los puntos
        gamerN = findViewById(R.id.txtGamerN);
        String puntos = "Monedas ganadas: " + String.valueOf(datos.getPuntuacion());//mostramos el valor de la puntuación global
        textView.setText(puntos);//mensaje de número de monedas
        saveName = findViewById(R.id.SaveButton);
        txtPuntuacion = findViewById(R.id.txtScoreTitle);
        btnInicio =findViewById(R.id.btn_volver_jugar);
        btnSalir = findViewById(R.id.btn_salir);

        saveName.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (gamerN.getText().toString().isEmpty()){//comprobamos que la caja de texto nombre tiene datos
                    Toast.makeText(PantallaFinal.this, "Introduce el nombre", Toast.LENGTH_SHORT).show();
                }else insertJugadorRx();
                gamerN.setText("");//vaciamos la caja de texto
                datos.setPuntuacion(0);//dejamos la puntuación a 0
                textView.setVisibility(View.INVISIBLE);//dejamos invisibles la puntuacion y el texto de título
                txtPuntuacion.setVisibility(View.INVISIBLE);
                saveName.setEnabled(false);//deshabilitamos el botón de guardar



//                DbHelper dbHelper = new DbHelper(PantallaFinal.this);
//                SQLiteDatabase db =dbHelper.getWritableDatabase();
//                if(db != null){
//
//                    LocalDate horaActual = LocalDate.now();
//                    String name = gamerN.getText().toString();
//                    int puntos = datos.getPuntuacion();
//                    DataItem jugador = new DataItem(name, horaActual, puntos);
//                    dbHelper.subirPuntuacion(jugador, PantallaFinal.this);
//                    Intent intent = new Intent(PantallaFinal.this, VentanaPuntuaciones.class);
//                    startActivity(intent);
//                    finish();
//                }else{
//                    Toast.makeText(PantallaFinal.this, "Error al acceder a la bd", Toast.LENGTH_LONG).show();
//                }
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
    public void insertJugadorRx(){
        String nombre = gamerN.getText().toString();
        int puntuacion = datos.getPuntuacion();
        Date fecha = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Ajusta el formato
        String fechaStr = dateFormat.format(fecha);

        dbManager.insertJugador(nombre, puntuacion, fechaStr)
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
        if ( id == R.id.menu_inicio){
            Intent intent = new Intent(PantallaFinal.this,MenuInicial.class);
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

}