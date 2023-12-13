package com.jose.diceroller;


import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import static com.jose.diceroller.MenuInicial.REQUEST_CODE;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
//-------------------
import android.provider.CalendarContract;
//-------------------
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jose.diceroller.db.ApiClient;
import com.jose.diceroller.db.ApiService;
import com.jose.diceroller.db.Bote;
import com.jose.diceroller.db.DbManager;
import com.jose.diceroller.db.PlayerHistory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class PantallaFinal extends AppCompatActivity {
    //atributos de firebase y google

    private FirebaseFirestore mfirestore;
    private DatabaseReference databaseReference;

    //resto de atributos
    private TextView textView;
    private EditText gamerN;

    private TextView txtPuntuacion;
    private GlobalVariables datos;
    private Button saveName, btnSalir, btnInicio;


    private int datosGrabar;
    private int puntosParaBote = 15;//puntos mínimos para llevarte el bote
    private int puntosActualesBote; //vemos los puntos que tenemos en el bote
    public Activity activity = this;
    View vista;
    private DbManager dbManager;

    public static final int REQUEST_CODE_PERMISSIONS = 1;

    private static final int REQUEST_CODE_PERMISO_ESCRIBIR_EXTERNO = 2;
    private static final int REQUEST_CODE_PERMISO_UBICACION_MEDIOS = 1;
    private static final int REQUEST_CODE_PERMISO_ESCRIBIR_EXT = 2;


    private static final String[] PERMISSION = {
            Manifest.permission.ACCESS_MEDIA_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MEDIA_CONTENT_CONTROL
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;


    // Declaraciones para las notificaciones
    private Button btnNotificacion;
    private static final String CHANNEL_ID = "canal"; // string para el canal (doc android)
    private PendingIntent pendingIntent; // lanzar la actividad al hacer click

    private ApiService apiService; //variable para usarla en la inserción del jugador con retrofit

    private Gson gson;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        datos = (GlobalVariables) getApplicationContext();//instanciamos la variable global
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_final);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        dbManager = new DbManager(this);
        textView = findViewById(R.id.txt_puntos);//localizamos el txt de los puntos
        gamerN = findViewById(R.id.txtGamerN);
        String puntos = "Monedas ganadas: " + String.valueOf(datos.getPuntuacion());//mostramos el valor de la puntuación global
        textView.setText(puntos);//mensaje de número de monedas
        saveName = findViewById(R.id.SaveButton);
        vista = findViewById(R.id.SaveButton);
        txtPuntuacion = findViewById(R.id.txtScoreTitle);
        btnInicio = findViewById(R.id.btn_volver_jugar);
        btnSalir = findViewById(R.id.btn_salir);
        gson = new Gson();
        mfirestore = FirebaseFirestore.getInstance(); //instanciamos la bbdd
        apiService = ApiClient.getClient().create(ApiService.class);//instanciamos la api para cargar los jugadores con Retrofit

//        puntosActualesBote = datos.getPuntosBote();
//
//        if (datos.getPuntuacion() >= puntosParaBote) {
//            datosGrabar = puntosActualesBote + datos.getPuntuacion();
//            Toast.makeText(PantallaFinal.this, "Ganaste el Bote te llevas:  " + datosGrabar, Toast.LENGTH_SHORT).show();
//        } else {
//            datosGrabar = datos.getPuntuacion();
//
//        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_MEDIA_LOCATION}, REQUEST_CODE_PERMISO_ESCRIBIR_EXTERNO);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISO_ESCRIBIR_EXTERNO);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MEDIA_CONTENT_CONTROL}, REQUEST_CODE_PERMISO_ESCRIBIR_EXTERNO);

        verifyPermission(this);
        String nombre;
        nombre = datos.getNombreJugador().toString();
        gamerN.setText(nombre);

        //Vamos a crear un bote cada vez que el jugador no llegue a 15 puntos este bote se acumulará en firestore de firebase


        if (datos.getPuntuacion() > 10) {
            // NOTIFICACION victoria (más de 10 monedas)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                showNotification();
            } else {
                showNewNotification();
            }
        } // Podriamos añadir un else por si existe mensaje de error

        if (datos.getPuntuacion() > 10) {
            // NOTIFICACION victoria (más de 10 monedas)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                showNotification();
            } else {
                showNewNotification();
            }
        } // Podriamos añadir un else por si existe mensaje de error

        saveName.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {


                if (gamerN.getText().toString().isEmpty()) {//comprobamos que la caja de texto nombre tiene datos
                    Toast.makeText(PantallaFinal.this, "Introduce el nombre", Toast.LENGTH_SHORT).show();
                    Bitmap capture = createScreenshot();
                    takeScreenCapture(activity, capture);
                    createFile(getWindow().getDecorView().getRootView(), "result");
                } else {
                    Bitmap capture = createScreenshot();
                    int datosGrabar1 = datos.getPuntuacion();
                    crearJugada(datosGrabar1);
                    insertJugadorRx();
//                    if (datos.getPuntuacion() >= puntosParaBote) {
//                        ajustarSaldoBote(0);
//                        datos.setPuntosBote(0);
//                    } else {
//                        ajustarSaldoBote(puntosParaBote + datos.getPuntosBote());
//                    }
                    gamerN.setText("");//vaciamos la caja de texto
                    datos.setPuntuacion(0);//dejamos la puntuación a 0
                    textView.setVisibility(View.INVISIBLE);//dejamos invisibles la puntuacion y el texto de título
                    txtPuntuacion.setVisibility(View.INVISIBLE);
                    saveName.setEnabled(false);//deshabilitamos el botón de guardar
                    takeScreenCapture(activity, capture);
                    insertEvent();

                }


            }
        });
        //botón salir
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mAuth.signOut();//salimos de la autenticacion de mail y contraseña
                signOut();//salimos de la cuenta de google
            }
        });
        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(PantallaFinal.this, PlayersOnlineActivity.class);
                startActivity(intent);
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
            //mAuth.signOut();
            signOut();
        }
        return true;
    }

    protected static File createFile(View view, String filename) {
        Date date = new Date();
        CharSequence format = DateFormat.getDateInstance().format("yyyy-MM-dd_hh:mm:ss");
        try {
            String directoryPath = Environment.getExternalStorageDirectory().toString() + "/dashrolls";
            File directorioDashrolls = new File(Environment.getExternalStorageDirectory(), "/dashrolls");
            if (!directorioDashrolls.exists()) {
                directorioDashrolls.mkdirs();
            }
            String path = directoryPath + "/" + filename + ".jpg";
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            File image = new File(path);
            FileOutputStream fileOutputStream = new FileOutputStream(image);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            return image;

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public static void verifyPermission(Activity activity) {

        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSION, REQUEST_EXTERNAL_STORAGE);
        }
    }

    public Bitmap createScreenshot() {
        // Obtener la ventana raíz
        Window window = getWindow();
        // Obtener el tamaño de la pantalla
        int width = window.getDecorView().getWidth();
        int height = window.getDecorView().getHeight();
        // Crear una nueva imagen
        Bitmap screenshot = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // Crear un nuevo lienzo
        Canvas canvas = new Canvas(screenshot);
        // Capturar la pantalla
        window.getDecorView().draw(canvas);
        return screenshot;
    }

    public void takeScreenCapture(Activity activity, Bitmap screenshot) {
        //Solicitar permisos
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (activity.checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(permissions, REQUEST_CODE_PERMISSIONS);

        }
        //Crear carpeta

        File diceRollerFolder = new File(
                activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "DiceRoller"
        );
        if (!diceRollerFolder.exists()) {
            diceRollerFolder.mkdirs();
        }
        if (screenshot == null) {
            // Maneja el error
            Log.e("PantallaFinal", "Falló al tomar la captura de pantalla");
            return;
        }
        // Guarda la captura de pantalla en un archivo
        try {
            FileOutputStream outputStream = new FileOutputStream(new File(
                    activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    String.format("dr_%s.jpg", new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()))
            ));
            screenshot.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
        } catch (IOException e) {
            Log.e("PantallaFinal", "Falló al guardar la captura de pantalla", e);
        }
    }

    public void insertEvent() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, REQUEST_CODE);
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, "Nueva victoria DiceRoller")
                .putExtra(CalendarContract.Events.DESCRIPTION, "Nueva victoria alcanzada")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "En tu dispositivo Android");
        startActivity(intent);
    }

    // Métodos para mostrar notificaciones
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotification() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "NEW",
                NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        showNewNotification();
    }

    private void showNewNotification() {
        setPendingIntent(MenuInicial.class); // redirigir a la pantalla NotificacionActivity al hacer click
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),
                CHANNEL_ID)
                .setSmallIcon(R.drawable.dice_6)
                .setContentTitle("Victoria!! Enhorabuena!")
                .setContentText("Conseguiste más de 10 puntos")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        managerCompat.notify(1, builder.build());
    }

    private void setPendingIntent(Class<?> clsActivity) {
        Intent intent = new Intent(this, clsActivity);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(clsActivity);
        stackBuilder.addNextIntent(intent);
        pendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    /**
     * función para salir de pantalla
     */
    private void signOut() {
        finish();
        startActivity(new Intent(PantallaFinal.this, LoginActivity.class));
    }


    /**
     * comprobamos si el jugador ha ganado el bote
     *
     * @return
     */
    private boolean ganarBote() {
        if (datos.getPuntuacion() >= puntosParaBote) {
            return true;
        }

        return false;
    }


    /**
     * preparamos los datos del jugador para cargar en la bbdd
     */
    private void crearJugada(int puntosRecibidos) {
        //cogemos la fecha actual
        Date currentDate = new Date();
        // Crea un objeto SimpleDateFormat con el formato deseado
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // convertimos la fecha a una cadena en el formato especificado
        String dateString = dateFormat.format(currentDate);
        String nombre = datos.getNombreJugador();
        int puntos = puntosRecibidos;
        double latitud = datos.getLatitud();
        double longitud = datos.getLongitud();
        PlayerHistory playerHistory = new PlayerHistory(nombre, puntos, dateString, latitud, longitud);
        insertarJugadorRetrofit(playerHistory);
    }


    /**
     * metodo para insertar el jugado usando Retrofit en Real Time de firebase
     *
     * @param jugador
     */
    private void insertarJugadorRetrofit(PlayerHistory jugador) {

        Call<PlayerHistory> call = apiService.agregarJugador(jugador);
        call.enqueue(new Callback<PlayerHistory>() {
            @Override
            public void onResponse(Call<PlayerHistory> call, Response<PlayerHistory> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PantallaFinal.this, "Jugador agregado con Retrofit", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PantallaFinal.this, "Error al agregar jugador con Retrofit", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlayerHistory> call, Throwable t) {
                Toast.makeText(PantallaFinal.this, "Error de red con Retrofit", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Función para cargar el bote o vaciarle a cero
     *
     * @param cantidad
     */
    private void ajustarSaldoBote(int cantidad) {
        DocumentReference boteMonedasRef = mfirestore.collection(Bote.TABLE_BOTE).document(Bote.ID_DOCUMENTO);
        boteMonedasRef.update(Bote.PUNTOS, cantidad)
                .addOnSuccessListener(aVoid -> Log.d("TAG", "Saldo ajustado con éxito"))
                .addOnFailureListener(e -> Log.d("TAG", "Error al ajustar el saldo: " + e.getMessage()));
    }


}



    ///--------------------no usado--------------------------------
//    /**
//     * metodo para guardar la jugada en firebase
//     */
//    private void crearJugadaDatos() {
//        UUID uuid = UUID.randomUUID();
//        String playerId = uuid.toString();
//        String nombre = datos.getNombreJugador();
//        int nuevaPuntuacion;
//        if (ganarBote()){
//            nuevaPuntuacion = datos.getPuntuacion();
//        }else{
//            nuevaPuntuacion = datos.getPuntuacion();
//        }
//        double nuevaLatitud = datos.getLatitud();
//        double nuevaLongitud = datos.getLongitud();
//        crearJugada(playerId,nombre,nuevaPuntuacion, nuevaLatitud, nuevaLongitud );
//    }
//
//    /**
//     * función para crear las jugadas en firebase RealTime no usado
//     * @param playerId
//     * @param nombre
//     * @param nuevaPuntuacion
//     * @param nuevaLatitud
//     * @param nuevaLongitud
//     */
//    private void crearJugada(String playerId, String nombre, int nuevaPuntuacion, double nuevaLatitud, double nuevaLongitud) {
//        //cogemos la fecha actual
//        Date currentDate = new Date();
//        // Crea un objeto SimpleDateFormat con el formato deseado
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        // convertimos la fecha a una cadena en el formato especificado
//        String dateString = dateFormat.format(currentDate);
//        databaseReference = FirebaseDatabase.getInstance().getReference(PlayerHistory.TABLE_JUGADORES);
//        PlayerHistory playerHistory = new PlayerHistory(playerId, nombre,nuevaPuntuacion, dateString, nuevaLatitud, nuevaLongitud );
//        // Crea un nuevo nodo con la UID del usuario y guarda los datos
//        databaseReference.child(playerId).setValue(playerHistory);
//    }
//
//
//    /**
//     * insertamos el jugador con Gson en firestore de firebase Utilizamos Gson
//     * @param jugador
//     */
//    private void insertarJugadorFirestore(PlayerHistory jugador){
//        Gson gson = new Gson();
//        String json = gson.toJson(jugador);
//        java.lang.reflect.Type type =new TypeToken<Map<String, Object>>() {}.getType();
//        Map<String, Object> map = gson.fromJson(json, type);
//        mfirestore.collection(PlayerHistory.TABLE_JUGADORES)
//                .add(map)
//                .addOnSuccessListener(aVoid ->{
//                    //operación con éxito
//                    Toast.makeText(PantallaFinal.this, "Insertado correctamente",Toast.LENGTH_SHORT).show();
//                }).addOnFailureListener(e ->{
//
//                    Toast.makeText(PantallaFinal.this, "Error al insertar",Toast.LENGTH_SHORT).show();
//
//                });
//
//    }
//
//
//    private void insertarBoteFirestore(Bote bote){
//        Gson gson = new Gson();
//        String json = gson.toJson(bote);
//        java.lang.reflect.Type type =new TypeToken<Map<String, Object>>() {}.getType();
//        Map<String, Object> map = gson.fromJson(json, type);
//        mfirestore.collection(Bote.TABLE_BOTE)
//                .add(map)
//                .addOnSuccessListener(aVoid ->{
//                    //operación con éxito
//                    Toast.makeText(PantallaFinal.this, "Bote Insertado correctamente",Toast.LENGTH_SHORT).show();
//                }).addOnFailureListener(e ->{
//
//                    Toast.makeText(PantallaFinal.this, "Error al insertar",Toast.LENGTH_SHORT).show();
//
//                });
//
//    }
//
//    /**
//     * función para chequear el bote
//     */
//    private void checBote(){
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//
//        DatabaseReference registro = database.getReference("boteMonedas");
//        registro.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@io.reactivex.rxjava3.annotations.NonNull DataSnapshot dataSnapshot) {
//
//                int dato = dataSnapshot.getValue(Integer.class);
//                String datoString = String.valueOf(dato);
//                Toast.makeText(PantallaFinal.this, "El bote tienes: "+String.valueOf(datos.getPuntosBote()), Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void onCancelled(@io.reactivex.rxjava3.annotations.NonNull DatabaseError databaseError) {
//                // Se produce un error
//            }
//        });
//    }
//
//    /**
//     * Agrega una moneda al bote
//     *
//     * @param cantidad Cantidad de monedas a agregar
//     */
//    private void agregarMonedaAlBote(int cantidad) {
//        DatabaseReference boteReference = FirebaseDatabase.getInstance().getReference("boteMonedas");
//        boteReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    int cantidadActual = dataSnapshot.getValue(Integer.class);
//                    boteReference.setValue(cantidadActual + cantidad)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Log.d("PantallaFinal", "Moneda agregada al bote con éxito");
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.e("PantallaFinal", "Error al agregar moneda al bote", e);
//                                }
//                            });
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e("PantallaFinal", "Error al obtener la cantidad actual del bote", databaseError.toException());
//            }
//        });
//    }
//
//    /**
//     * Función para crear el bote si no está creado
//     */
//    private void crearBote(){
//        // Asegúrate de tener la referencia al nodo del bote
//        DatabaseReference boteReference = FirebaseDatabase.getInstance().getReference("boteMonedas");
//
//        // Verifica si el nodo del bote ya existe
//        boteReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (!dataSnapshot.exists()) {
//                    // Si no existe, crea el nodo del bote con un valor inicial
//                    boteReference.setValue(0)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    // El nodo del bote se ha creado con éxito
//                                    Log.d("Firebase", "Nodo del bote creado con éxito");
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    // Error al intentar crear el nodo del bote
//                                    Log.e("Firebase", "Error al crear el nodo del bote", e);
//                                }
//                            });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Manejar errores si es necesario
//                Log.e("Firebase", "Error al verificar la existencia del nodo del bote", databaseError.toException());
//            }
//        });
//
//    }
//
//    /**
//     * Metodo para vaciar bote, pendiente de ver funcionamiento retrofit
//     */
//    private void sacarBote(){
//        // Crea una referencia a la base de datos de Firebase
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//
//        // Agrega un ValueEventListener al registro
//        DatabaseReference registro = database.getReference("boteMonedas");
//        registro.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // Recupera el dato del registro
//                int dato = dataSnapshot.getValue(Integer.class);
//                String datoString = String.valueOf(dato);
//                // Ajusta el dato a cero
//                dato = 0;
//                // Actualiza el registro con el nuevo dato
//                registro.setValue(dato);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Se produce un error
//            }
//        });
//    }
//
//
//
//
//    /**
//     * funcion que retorna la cantidad bote que hay
//     * @return Saldo del bote como un entero.
//     */
//    private int checkBoteGson2() {
//        final int[] saldoActualBote = {0};
//
//        mfirestore.collection(Bote.TABLE_BOTE)
//                .document(Bote.ID_DOCUMENTO)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        if (document.exists()) {
//                            String json = gson.toJson(document.getData());
//                            Bote bote = gson.fromJson(json, Bote.class);
//                            if (bote != null) {
//                                Log.d("TAG", "El saldo del bote esSSSS: " + bote.getPuntosBote());
//                                String saldoBote = bote.getPuntosBote();
//
//                                saldoActualBote[0] = Integer.parseInt(saldoBote);
//                                Toast.makeText(PantallaFinal.this, "Bote actual: "+saldoBote, Toast.LENGTH_SHORT).show();
//                                saveName.setVisibility(View.VISIBLE);//hasta que no vemos el saldo del bote no lo hacemos visible
//                            }
//                        } else {
//                            Log.d("TAG", "El documento no existe");
//                        }
//                    } else {
//                        Log.d("TAG", "Error al obtener el documento: " + task.getException());
//                    }
//                });
//
//        return saldoActualBote[0];
//    }
//
//    /**
//     * función para saber el saldo del bote
//     */
//    private void checkBoteGson1() {
//        mfirestore.collection(Bote.TABLE_BOTE)
//                .document(Bote.ID_DOCUMENTO) //id del docuemnto de la consola de firebase
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        if (document.exists()) {
//                            String json = gson.toJson(document.getData());//convertimos en String los datos del objeto
//                            Bote bote = gson.fromJson(json, Bote.class);// lo transformamos en la clase
//                            if (bote != null) {
//                                Log.d("TAG", "El saldo del bote es: " + bote.getPuntosBote());
//                                String saldoBote = bote.getPuntosBote();
//                                datos.setPuntosBote(Integer.parseInt(saldoBote));
//                            }
//                        } else {
//                            Log.d("TAG", "El documento no existe");
//                        }
//                    } else {
//                        Log.d("TAG", "Error al obtener el documento: " + task.getException());
//                    }
//                });
//    }
//
//    private void checkBoteGson() {
//        mfirestore.collection(Bote.TABLE_BOTE)
//                .document(Bote.ID_DOCUMENTO)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        if (document.exists()) {
//                            String json = gson.toJson(document.getData());
//                            Bote bote = gson.fromJson(json, Bote.class);
//                            if (bote != null) {
//                                Log.d("TAG", "El saldo del bote esSSSS: " + bote.getPuntosBote());
//                                String saldoBote = bote.getPuntosBote();
//                                int saldoActualBote = Integer.parseInt(saldoBote);
//
//                                // Realiza acciones con el saldo, por ejemplo, guardar la partida
//                                //guardarPartida(saldoActualBote);
//                            }
//                        } else {
//                            Log.d("TAG", "El documento no existe");
//                        }
//                    } else {
//                        Log.d("TAG", "Error al obtener el documento: " + task.getException());
//                    }
//                });
//    }
//
//}

