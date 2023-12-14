package com.jose.diceroller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.jose.diceroller.db.Bote;
import com.jose.diceroller.db.PlayerHistory;
import com.jose.diceroller.db.ListAdapter;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class PlayersOnlineActivity extends AppCompatActivity {

    Button btnJugar, btnSalir;
    DatabaseReference databaseReference;
    TextView txtBote;
    GlobalVariables datos;

    private Gson gson;

    FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        datos = (GlobalVariables) getApplicationContext();//instanciamos la variable global
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_online);
        btnJugar =findViewById(R.id.btn_jugar);
        btnSalir = findViewById(R.id.btn_salir);
        txtBote = findViewById(R.id.txt_bote);
        firestore = FirebaseFirestore.getInstance();
        gson = new Gson();
        //checkBoteGson();

        //checBote();
        addTopTen();

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        btnJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToPlay();
            }
        });



    }

    /**
     * función para saber el saldo del bote
     */
    private void checkBoteGson() {
        firestore.collection(Bote.TABLE_BOTE)
                .document(Bote.ID_DOCUMENTO) //id del docuemnto de la consola de firebase
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String json = gson.toJson(document.getData());//convertimos en String los datos del objeto
                            Bote bote = gson.fromJson(json, Bote.class);// lo transformamos en la clase
                            if (bote != null) {
                                Log.d("TAG", "El saldo del bote es: " + bote.getPuntosBote());
                                String saldoBote = bote.getPuntosBote();
                                txtBote.setText("Saldo Bote: "+saldoBote);//ponemos el saldo del bote en la caja de texto
                                int datos1 = Integer.parseInt(saldoBote);
                                datos.setPuntosBote(datos1);

                            }
                        } else {
                            Log.d("TAG", "El documento no existe");
                        }
                    } else {
                        Log.d("TAG", "Error al obtener el documento: " + task.getException());
                    }
                });
    }


    /**
     * Función para traer la BBDD de los jugadores de Real Time
     */
    private void addTopTen() {
        databaseReference = FirebaseDatabase.getInstance().getReference(PlayerHistory.TABLE_JUGADORES);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<PlayerHistory> playerList = new ArrayList<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    PlayerHistory player = userSnapshot.getValue(PlayerHistory.class);
                    playerList.add(player);
                }

                Collections.sort(playerList, new Comparator<PlayerHistory>() {
                    @Override
                    public int compare(PlayerHistory o1, PlayerHistory o2) {
                        // ordenamos la lista
                        return Integer.compare(o2.getPuntuacion(), o1.getPuntuacion());
                    }
                });

                // mostramos los 10 mejores
                int topPlayersCount = Math.min(10, playerList.size());
                List<PlayerHistory> topPlayers = playerList.subList(0, topPlayersCount);

                // enviamos la lista limpia a la función que llena el recycler view
                mostrarUsuariosEnInterfaz(topPlayers);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", "Error al obtener datos de la base de datos", databaseError.toException());
            }
        });
    }


    /**
     * función que carga la lista de jugadas en el recycler view
     * @param userList
     */
    private void mostrarUsuariosEnInterfaz(List<PlayerHistory> userList) {

        ListAdapter listAdapter = new ListAdapter(userList, PlayersOnlineActivity.this);
        androidx.recyclerview.widget.RecyclerView recyclerView = findViewById(R.id.listTopTen);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(PlayersOnlineActivity.this));
        recyclerView.setAdapter(listAdapter);
    }


    /**
     * nos vamos a la pantalla siguiente
     */
    private void navigateToPlay() {
        finish();
        startActivity(new Intent(PlayersOnlineActivity.this, MenuInicial.class));
    }

    /**
     * función para volver al menu login
     */
    private void signOut() {
        startActivity(new Intent(PlayersOnlineActivity.this, LoginActivity.class));
        finish();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuonline, menu);
        return true;
    }
    @SuppressLint("CheckResult")
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();
        if ( id == R.id.menu_borrar_registros){
            mostrarDialogoDeConfirmacion();
        }
        else if(id == R.id.menu_salir){
            signOut();
        }
        return true;
    }


    /**
     * funicón borrado de la bbdd de los jugadores
     */
    private void borrarDB() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(PlayerHistory.TABLE_JUGADORES);

        Task<DataSnapshot> tareaLeerJugadores = databaseReference.get();
        tareaLeerJugadores.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();

                    if (dataSnapshot.exists()) {
                        for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                            Task<Void> tareaEliminarJugador = playerSnapshot.getRef().removeValue();
                            tareaEliminarJugador.addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(PlayersOnlineActivity.this, "Registros de jugadores eliminados", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Exception exception = task.getException();
                                    }
                                }
                            });
                        }
                    } else {
                        Toast.makeText(PlayersOnlineActivity.this, "No hay registros de jugadores para eliminar", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Exception exception = task.getException();
                }
            }
        });
    }





    /**
     * Aviso antes de borrar la BBDD
     */
    private void mostrarDialogoDeConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirmación");
        builder.setMessage("¿Estás seguro de que deseas eliminar todos los registros?");

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Código para la acción afirmativa
                borrarDB();
                //sacarBote();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }



    //////////////no usado//////////////

    private void sacarBote() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference registro = database.getReference("boteMonedas");

        Task<DataSnapshot> tareaLeerBote = registro.get();

        tareaLeerBote.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();

                    Integer dato = dataSnapshot.getValue(Integer.class);

                    if (dato != null) {

                        dato = 0;

                    }
                } else {
                    Exception exception = task.getException();
                }
            }
        });

    }

    private void checBote(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference registro = database.getReference("boteMonedas");
        registro.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@io.reactivex.rxjava3.annotations.NonNull DataSnapshot dataSnapshot) {
                int dato = dataSnapshot.getValue(Integer.class);
                String datoString = String.valueOf(dato);
                txtBote.setText("Bote acumulado: "+ datoString);
                Toast.makeText(PlayersOnlineActivity.this, "El bote tienes: "+String.valueOf(datos.getPuntosBote()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@io.reactivex.rxjava3.annotations.NonNull DatabaseError databaseError) {
                // Se produce un error
            }
        });
    }

    private void actualizar(Bote bote) {

        txtBote.setText("Bote acumulado: "+String.valueOf(bote.getPuntosBote()));
    }

}