package com.jose.diceroller;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    Button btnSalir, btnJugar;
    TextView txtNombre;

   // GlobalVariables datos;

    SignInButton btnGoogle;

    FirebaseAuth mAuth;

    private GoogleSignInClient gsc;
    private GoogleSignInOptions gso;

    private FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //datos = (GlobalVariables) getApplicationContext();//instanciamos la variable global
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setTitle("Login");
        btnSalir = findViewById(R.id.btn_salir);
        btnJugar = findViewById(R.id.btn_jugar);
        btnGoogle = findViewById(R.id.btn_google);
        txtNombre = findViewById(R.id.txt_nombre);

        //instanciamos firebase Auth y la BBDD para inclir los usuarios
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();


        //iniciamos los servicios de autenticacion de google
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
//        //iniciamos los servicios de autenticacion de google
//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//
//        gsc = GoogleSignIn.getClient(this, gso);

        //botón login de cuenta de google
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                singIn();
                btnGoogle.setVisibility(View.INVISIBLE);

            }
        });

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goOut();

            }
        });

        btnJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCreatePlayer();
            }
        });

    }

    /***
     * Metodo para iniciar con la cuenta de Google
     */
    private void singIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    /**
     * Metodo para salir
     */
    private void goOut() {
        FirebaseAuth.getInstance().signOut();
        gsc.signOut().addOnCompleteListener(this, task -> {
            finish();
        });

    }

    /**
     *  obtener el usuario actualmente autenticado mediante Firebase Authentication y luego actualiza la interfaz de usuario
     */
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                GoogleSignInAccount account = Auth.GoogleSignInApi.getSignInResultFromIntent(data).getSignInAccount();
                if (account != null) {
                    ///firebaseAuthWithGoogle(account.getIdToken());
                    //datos.setNombreJugador(account.getDisplayName());
                    txtNombre.setText(account.getDisplayName());
                    btnJugar.setVisibility(View.VISIBLE);
                    //navigateToCreatePlayer();
                    Toast.makeText(LoginActivity.this, "REGISTRO CORRECTO: "+ account.getDisplayName() , Toast.LENGTH_SHORT).show();
                }

            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, "Algo fué mal", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * función para ir a las jugadas
     */
    private void navigateToCreatePlayer() {
        finish();
        startActivity(new Intent(LoginActivity.this, PlayersOnlineActivity.class));
    }

    /**
     * metodo para grabar en la tabla users los datos de la cuenta de google que se ha logueado
     * @param idToken
     */
    private void firebaseAuthWithGoogle(String idToken){
        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        FirebaseUser user = mAuth.getCurrentUser();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("id", user.getUid());
                        map.put("name", user.getDisplayName());
                        map.put("email",user.getEmail());
                        firebaseDatabase.getReference().child("users").child(user.getUid()).setValue(map);
                    }else {
                        Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Actualizamos los datos el usuario que hay logueado
     * @param user
     */
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String displayName = user.getDisplayName();
            String email = user.getEmail();
            btnJugar.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Bienvenido, " + displayName, Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Por favor pulsa inicio o salir del juego, ", Toast.LENGTH_SHORT).show();
        }
    }

}