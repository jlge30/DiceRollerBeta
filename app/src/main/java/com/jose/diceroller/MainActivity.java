package com.jose.diceroller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
//    private TextView resultado1;
//    private TextView resultado2;
    /*
    Atributos para generar un numero aleatorio
     */
    private MediaPlayer mediaPlayer;
    private Random random1 = new Random();
    private Random random2 = new Random();
    private TextView txtMensaje;

    private TextView txtPuntuacion, txtFinalJuego;
    private TextView txtTiradas;
    private int puntuacion = 10;
    private GlobalVariables datos;

    private Button btnLanzar;

    /*
    Clase SoundPool para asociar al giro de los dados
     */
    private SoundPool soundPool;

    int sonido;


    private Handler handler;//atributo para retrasar la aparción de los mensajes


    private int tiradas = 10;



    private ImageButton musicaMenuPrincipal;
    private Button btnClosePopup;
    private PopupWindow popupWindow;




    @SuppressLint("ObsoleteSdkInt")
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

        datos = (GlobalVariables) getApplicationContext();//instanciamos la variable global
        txtTiradas = findViewById(R.id.txtTiradas);
        String mensaje = "Tiradas Pendientes: " + String.valueOf(tiradas);
        txtTiradas.setText(mensaje);
        txtPuntuacion = findViewById(R.id.txt_puntuacion);
        String puntos = "X " + String.valueOf(puntuacion);
        txtPuntuacion.setText(puntos);//mensaje de número de tiradas
        btnLanzar = findViewById(R.id.btnLanzar);
        txtFinalJuego = findViewById(R.id.txt_final_juego);
        musicaMenuPrincipal=findViewById(R.id.musicaMenuPrincipal);
        sonido = soundPool.load(this, R.raw.dados,1);

        //MUSICA:
        mediaPlayer = MediaPlayer.create(this, R.raw.musica02);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        musicaMenuPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });
    }

    private void showPopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_menu_musica, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // Permite el clic fuera del Popup para cerrarlo

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // Configurar un fondo translúcido
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(android.R.color.transparent));
        popupWindow.setBackgroundDrawable(colorDrawable);

        // Animación para entrar (puedes ajustarla según tus preferencias)
        popupWindow.setAnimationStyle(R.style.PopupAnimation);

        // Mostrar el Popup en el centro de la pantalla
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        Button btn_musica01 = popupView.findViewById(R.id.btn_musica01);
        btn_musica01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar la canción cuando se hace clic en el botón dentro del popup
                playSong(R.raw.musica01);
                Toast.makeText(MainActivity.this, "Canción 01", Toast.LENGTH_SHORT).show();
            }
        });
        Button btn_musica02 = popupView.findViewById(R.id.btn_musica02);
        btn_musica02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar la canción cuando se hace clic en el botón dentro del popup
                playSong(R.raw.musica02);
                Toast.makeText(MainActivity.this, "Canción 02", Toast.LENGTH_SHORT).show();
            }
        });
        Button btn_musica03 = popupView.findViewById(R.id.btn_musica03);
        btn_musica03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar la canción cuando se hace clic en el botón dentro del popup
                playSong(R.raw.musica03);
                Toast.makeText(MainActivity.this, "Canción 03", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar el botón de cerrar en el Popup
        btnClosePopup = popupView.findViewById(R.id.btn_menuMusicaCancelar);
        btnClosePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }


    /**
     * función para lanzar el dado
     * @param view
     */
    public void lanzar(View view) {
        /*
        llamamos a la variables globales
         */
        //datos = (GlobalVariables) getApplicationContext();//instanciamos la variable global

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

                    Intent intent = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        intent = new Intent(MainActivity.this, PantallaFinal.class);
                    }
                    finish();//cerramos la primera vista
                    startActivity(intent);

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

    private void playSong(int songId) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, songId);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
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
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        // Restaurar la configuración de Live Caption al cerrar la aplicación
        //enableLiveCaption();
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();

            // Desactivar Live Caption mientras se reproduce música
            //disableLiveCaption();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();

            // Restaurar la configuración de Live Caption cuando la música se pausa
            //enableLiveCaption();
        }
    }/*

    // Método para desactivar Live Caption
    private void disableLiveCaption() {
        Settings.Secure.putInt(getContentResolver(), Settings.Secure.ACCESSIBILITY_DISPLAY_INVERSION_ENABLED, 0);
    }

    // Método para restaurar la configuración de Live Caption
    private void enableLiveCaption() {
        Settings.Secure.putInt(getContentResolver(), Settings.Secure.ACCESSIBILITY_DISPLAY_INVERSION_ENABLED, 1);
    }*/

}