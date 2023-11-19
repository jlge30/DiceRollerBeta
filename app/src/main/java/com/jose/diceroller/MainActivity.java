package com.jose.diceroller;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.provider.CalendarContract;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Random;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
//    private TextView resultado1;
//    private TextView resultado2;
    /*
    Atributos para generar un numero aleatorio
     */
    private Random random1 = new Random();
    private Random random2 = new Random();
    private TextView txtMensaje;



    private TextView txtPuntuacion, txtFinalJuego;
    private TextView txtTiradas;
    private int puntuacion = 10;
    private GlobalVariables datos;

    private Button btnLanzar;

    SoundPool soundPool;

    int sonido;


    private Handler handler;//atributo para retrasar la aparción de los mensajes


    private int tiradas = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.SplashTeme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createNewSoundPool();
        } else {
            createOldSoundPool();
        }

        txtTiradas = findViewById(R.id.txtTiradas);
        String mensaje = "Tiradas Pendientes: " + String.valueOf(tiradas);
        txtTiradas.setText(mensaje);
        txtPuntuacion = findViewById(R.id.txt_puntuacion);
        String puntos = "X " + String.valueOf(puntuacion);
        txtPuntuacion.setText(puntos);//mensaje de número de tiradas
        btnLanzar = findViewById(R.id.btnLanzar);
        txtFinalJuego = findViewById(R.id.txt_final_juego);

        sonido = soundPool.load(this, R.raw.dados,1);


    }


    /**
     * función para lanzar el dado
     * @param view
     */
    public void lanzar(View view) {
        /*
        llamamos a la variables globales
         */
        datos = (GlobalVariables) getApplicationContext();//instanciamos la variable global
        //Toast.makeText(this, R.string.lanzar, Toast.LENGTH_SHORT).show();
//        resultado1 = findViewById(R.id.txt1);
//        resultado2 = findViewById(R.id.txt2);
        /*
        Imagenes iniciales que creamos para alojar los dados
         */
        ImageView diceImage = findViewById(R.id.image_dado1);
        ImageView diceImage2 = findViewById(R.id.image_dado2);
        ImageView coin = findViewById(R.id.imageView3);
        girar(diceImage);//llamamos a la función para que giren los dados
        girar(diceImage2);//llamamos a la función para que giren los dados
        girar(coin);


        tiradas --; // iniciamos el número de tiradas

        String mensaje = "Tiradas Pendientes: " + String.valueOf(tiradas);
        txtTiradas.setText(mensaje);//mensaje de número de tiradas

        int numero1 = random1.nextInt(6) + 1; //numero generado entre 6 y 1
//        resultado1.setText(Integer.toString(numero1)); //asigno el resultado al texto y lo transformo en string
        int numero2 = random2.nextInt(6) + 1; //numero generado entre 6 y 1

//        resultado2.setText(Integer.toString(numero2)); //asigno el resultado al texto y lo transformo en string
        txtMensaje = findViewById(R.id.txt_mensaje);//ventana del mensaje de ganar o perder
        //condicionales para que aparezcan los mensajes de gando o perdido monedas

        if ((numero1 + numero2) >= 6 ){//
            txtMensaje.setVisibility(View.INVISIBLE);
            txtMensaje.setTextColor(getColor(R.color.green));
            txtMensaje.setText(R.string.ganado);
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    txtMensaje.setVisibility(View.VISIBLE); // Hacer visible el TextView después de medio segundo
                }
            }, 500); // // medio segundo de retraso
            puntuacion++;

            if ((numero1 + numero2) == 12){
                txtMensaje.setVisibility(View.INVISIBLE);
                txtMensaje.setTextColor(getColor(R.color.green));
                txtMensaje.setText(R.string.ganado2);
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        txtMensaje.setVisibility(View.VISIBLE); // Hacer visible el TextView después de medio segundo
                    }
                }, 500); // medio segundo de retraso
                puntuacion++;
            }
        }else{
            txtMensaje.setVisibility(View.INVISIBLE);
            txtMensaje.setTextColor(getColor(R.color.red));
            txtMensaje.setText(R.string.perdido);
            handler = new Handler();//hacer que el mensaje salga con retraso
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    txtMensaje.setVisibility(View.VISIBLE); // Hacer visible el TextView después de medio segundo
                }
            }, 500); // medio segundo de retraso
            puntuacion--;
        }
        /*
        Actualizamo el numero de monedas de la variable global
         */
        datos.setPuntuacion(puntuacion);
        String puntos = "X " + String.valueOf(datos.getPuntuacion());

        handler = new Handler();//hacer que el marcador se sincronice con los mensajes
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                txtPuntuacion.setText(puntos);//mensaje de número de monedas
            }
        }, 500); // retraso de 0.5 segundos el marcador para que coincida con los mensajes

        if (tiradas ==0 ){
            btnLanzar.setVisibility(View.INVISIBLE);//ocultar botón lanzar
            txtFinalJuego.setVisibility(View.VISIBLE);
            handler = new Handler();//hacer que la última pantalla se siga viendo 2 segundos
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this, PantallaFinal.class);
                    startActivity(intent);
                    finish();//cerramos la primera vista

                }
            }, 2000); // retraso de 2 segundos el cierre de la ventana
        }

        /*
        condicionales para que aparezcan la imagen del dado 1 en función del resultado del numero 1

         */
        if (numero1 == 1) {
            diceImage.setImageResource(R.drawable.dice_1);
        } else if (numero1 == 2) {
            diceImage.setImageResource(R.drawable.dice_2);

        } else if (numero1 == 3) {
            diceImage.setImageResource(R.drawable.dice_3);
        }else if (numero1 == 4) {
            diceImage.setImageResource(R.drawable.dice_4);
        }else if (numero1 == 5) {
            diceImage.setImageResource(R.drawable.dice_5);
        }else if (numero1 == 6) {
            diceImage.setImageResource(R.drawable.dice_6);
        }

         /*
        condicionales para que aparezcan la imagen del dado  en función del resultado del numero 2

         */
        if (numero2 == 1) {
            diceImage2.setImageResource(R.drawable.dice_1);
        } else if (numero2 == 2) {
            diceImage2.setImageResource(R.drawable.dice_2);

        } else if (numero2 == 3) {
            diceImage2.setImageResource(R.drawable.dice_3);
        }else if (numero1 == 4) {
            diceImage2.setImageResource(R.drawable.dice_4);
        }else if (numero2 == 5) {
            diceImage2.setImageResource(R.drawable.dice_5);
        }else if (numero2 == 6) {
            diceImage2.setImageResource(R.drawable.dice_6);
        }
        //llamamos al método del sonido de los dados
        audiSoundPoll();

    }

    /**
     * función que gira las imagenes
     * @param view
     */
    private void girar(View view){
        RotateAnimation animation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(500);
        view.startAnimation(animation);
    }

    //integracion de la barra de menus
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();
        if ( id == R.id.menu_inicio){
            Intent intent = new Intent(MainActivity.this,MenuInicial.class);
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
/*
comienza la creación del SoundPool
 */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(attributes)
                .build();
    }

    @SuppressWarnings("deprecation")
    private void createOldSoundPool() {
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    }

    public void audiSoundPoll(){
        soundPool.play(sonido, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    @Override
    protected void onDestroy() {

        if (soundPool != null) {
            soundPool.release();
        }
        super.onDestroy();
    }

    public int getCalendarId(Context context) {
        Cursor cursor = context.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI,
                new String[]{CalendarContract.Calendars._ID},
                CalendarContract.Calendars.VISIBLE + " = 1",
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(CalendarContract.Calendars._ID);
            if (columnIndex != -1) {
                return cursor.getInt(columnIndex);
            } else {
                // Manejar el error: la columna no existe
                Log.e("Error", "La columna no existe");
                return -1;
            }
        } else {
            // Manejar el error: el cursor está vacío o nulo
            Log.e("Error", "El cursor está vacío o nulo");
            return -1;
        }
    }

    //=============================================================================================

    public void insertEvent() {
        int dia1, mes1, anno1, hora1, minuto1, dia2, mes2, anno2, hora2, minuto2;
        final Calendar cal = Calendar.getInstance();
        final Calendar calendario1 = Calendar.getInstance();
        final Calendar calendario2 = Calendar.getInstance();
        dia1 = cal.get(Calendar.DAY_OF_MONTH);
        mes1 = cal.get(Calendar.MONTH);
        anno1 = cal.get(Calendar.YEAR);
        hora1 = cal.get(Calendar.HOUR);
        minuto1 = cal.get(Calendar.MINUTE);
        dia2 = dia1 + 1;
        mes2 = mes1;
        anno2 = anno1;
        calendario1.set(Calendar.DAY_OF_MONTH, dia1);
        calendario1.set(Calendar.MONTH, mes1);

    }





}