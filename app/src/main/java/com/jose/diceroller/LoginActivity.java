package com.jose.diceroller;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin, btnRegistrar, btnSalir;
    EditText txtEmail;
    EditText txtPassword;

    SignInButton btnGoogle;

    FirebaseAuth mAuth;

    private GoogleSignInClient gsc;
    private GoogleSignInOptions gso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setTitle("Login");
        txtPassword = findViewById(R.id.txt_pass_login);
        txtEmail = findViewById(R.id.txt_email_login);
        btnLogin = findViewById(R.id.btn_login);
        btnRegistrar = findViewById(R.id.btn_registrar);
        btnSalir = findViewById(R.id.btn_salir);
        //iniciamos la autenticación de firebase con usuario y contraseña
        mAuth = FirebaseAuth.getInstance();

        //iniciamos los servicios de autenticacion de google
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        btnGoogle = findViewById(R.id.btn_google);
        //nos volvemes a la pantalla del registro
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //botor registro con usuario y contraseña
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        //revisamos si tenemos la sesión abierta para seguir en ella.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if ( account !=null){
            navigateToCreatePlayer();
        }
        //botón login de cuenta de google
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singIn();
            }
        });

    }


    /**
     * función para logearse con contraseña y email.
     */
    private void loginUser() {
        String emailUser = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        if ( emailUser.isEmpty() || password.isEmpty()){
            Toast.makeText(LoginActivity.this, "Faltan Datos", Toast.LENGTH_SHORT).show();
        }else {
            mAuth.signInWithEmailAndPassword(emailUser, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        finish();
                        navigateToCreatePlayer();
                        Toast.makeText(LoginActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(LoginActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            startActivity(new Intent(LoginActivity.this, MenuInicial.class));
            finish();
        }
    }

    private void singIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                navigateToCreatePlayer();
                Toast.makeText(LoginActivity.this, "REGISTRO CORRECTO", Toast.LENGTH_SHORT).show();
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, "Algo fué mal", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * función para ir a las jugadas
     */
    private void navigateToCreatePlayer() {
        startActivity(new Intent(LoginActivity.this, MenuInicial.class));
    }
}