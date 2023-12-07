package com.jose.diceroller;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jose.diceroller.db.User;


import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    Button btnRegistro, btnLogin;
    EditText txtNombre, txtEmail, txtPassword;

    FirebaseAuth mAuth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.setTitle("Registro usuarios");
        txtNombre = findViewById(R.id.txt_nombre_registro);
        txtEmail = findViewById(R.id.txt_email_registro);
        txtPassword = findViewById(R.id.txt_pass_registro);
        btnRegistro = findViewById(R.id.btn_registrar);
        btnLogin = findViewById(R.id.btn_ir_login);

        inicicalizarFireBase();


        //botón login para pasar al login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });


        //botón registrar
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearUsuario();


            }
        });

    }

    /**
     * función para crar usuarios autorizados
     */
    private void crearUsuario() {
        String nombre = txtNombre.getText().toString();
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Faltan datos por rellenar", Toast.LENGTH_SHORT).show();

        }else {
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Registro exitoso
                        Toast.makeText(RegisterActivity.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                        // Obtén la UID del usuario registrado
                        String userId = mAuth.getCurrentUser().getUid();
                        // Guarda los datos en Realtime Database
                        saveUserDataToDatabase(userId, nombre, email);
                        txtNombre.setText("");
                        txtEmail.setText("");
                        txtPassword.setText("");
                    } else {
                        // Si falla el registro, muestra un mensaje al usuario
                        Toast.makeText(RegisterActivity.this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisterActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //añadir todos los datos de los usuarios
    private void saveUserDataToDatabase(String userId, String name, String email) {
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        User user = new User(userId, name, email );
        // Crea un nuevo nodo con la UID del usuario y guarda los datos
        databaseReference.child(userId).setValue(user);
    }
    /**
     * Iniciamos las instanciias firebases
     */
    private void inicicalizarFireBase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

    }
}