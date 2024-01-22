package com.example.elec_trade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Register extends AppCompatActivity {

    private Button su;
    private TextInputLayout userName, userEmail, userPass, userPassRep;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firestore = FirebaseFirestore.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        userName = findViewById(R.id.username);
        userEmail = findViewById(R.id.email);
        userPass = findViewById(R.id.password);
        userPassRep = findViewById(R.id.passwordconfirm);
        su = findViewById(R.id.register);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Configura el color del título, por ejemplo
            actionBar.setTitle(Html.fromHtml("<font color=\"#F2A71B\">Register</font>"));
        }

        su.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uName = userName.getEditText().getText().toString().trim();
                String uEmail = userEmail.getEditText().getText().toString().trim();
                String uPass = userPass.getEditText().getText().toString().trim();
                String uPassRep = userPassRep.getEditText().getText().toString().trim();

                if (uName.isEmpty() || uEmail.isEmpty() || uPass.isEmpty() || uPassRep.isEmpty()) {
                    Toast.makeText(Register.this, "Complete los datos", Toast.LENGTH_SHORT).show();
                } else if (uPass.equals(uPassRep)) {
                    registerUser(uName, uEmail, uPass);
                } else {
                    Toast.makeText(Register.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    //registerUser(uName, uEmail, uPass);
                }
            }
        });

    }

    private void registerUser(String uName, String uEmail, String uPass) {
        firebaseAuth.createUserWithEmailAndPassword(uEmail, uPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String id = firebaseAuth.getCurrentUser().getUid();
                Map<String, Object> map = new HashMap<>();
                map.put("id", id);
                map.put("name", uName);
                map.put("email", uEmail);
                map.put("password", uPass);

                firestore.collection("user").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //toMainPage();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                        toMainPage();
                    }
                });
                toMainPage();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Register.this, "Error al registrar "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void toMainPage() {
        try {
            Intent toMain = new Intent(Register.this, Main.class);
            toMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            toMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(toMain);
        } catch (Exception e) {
            Log.e("Registro", "Error al iniciar AniadirProducto: " + e.getMessage());
            e.printStackTrace();
        }
    }
}