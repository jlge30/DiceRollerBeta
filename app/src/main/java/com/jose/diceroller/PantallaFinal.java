package com.jose.diceroller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.jose.diceroller.db.DataItem;
import com.jose.diceroller.db.DbHelper;

import java.time.LocalDate;
import java.util.Timer;
import java.util.TimerTask;

public class PantallaFinal extends AppCompatActivity {

    private TextView textView;
    private EditText gamerN;
    private EditText editText;

    private TextView txtNombre;
    private GlobalVariables datos;
    private Button saveName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        datos = (GlobalVariables) getApplicationContext();//instanciamos la variable global
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_final);
        textView = findViewById(R.id.txt_puntos);//localizamos el txt de los puntos
        gamerN = findViewById(R.id.txtGamerN);
        String puntos = "Monedas ganadas: " + String.valueOf(datos.getPuntuacion());//mostramos el valor de la puntuación global
        textView.setText(puntos);//mensaje de número de monedas
        saveName = findViewById(R.id.SaveButton);

        saveName.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                DbHelper dbHelper = new DbHelper(PantallaFinal.this);
                SQLiteDatabase db =dbHelper.getWritableDatabase();
                if(db != null){

                    LocalDate horaActual = LocalDate.now();
                    String name = gamerN.getText().toString();
                    int puntos = datos.getPuntuacion();
                    DataItem jugador = new DataItem(name, horaActual, puntos);
                    dbHelper.subirPuntuacion(jugador, PantallaFinal.this);
                    TimerTask tarea =new TimerTask() {//creamos la tarea de la ventana bienvenida
                        @Override
                        public void run() {//tarea para que la pantalla esté visible 5 segundos
                            Intent intent = new Intent(PantallaFinal.this, VentanaPuntuaciones.class);
                            startActivity(intent);
                            finish();
                        }
                    };
                    Timer tiempo = new Timer();//retardamos la otra ventana
                    tiempo.schedule(tarea, 3000);
                }else{
                    Toast.makeText(PantallaFinal.this, "Error al acceder a la bd", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}