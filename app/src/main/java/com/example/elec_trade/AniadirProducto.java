package com.example.elec_trade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AniadirProducto extends AppCompatActivity {

    private Button aniadir;
    private EditText nom, desc, prec;
    private FirebaseFirestore mFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aniadir_producto);
        //Configuracion Barra superior
        topBar();

        mFirestore = FirebaseFirestore.getInstance();

        nom = findViewById(R.id.nomProducto);
        desc = findViewById(R.id.descProducto);
        prec = findViewById(R.id.precProducto);
        aniadir = findViewById(R.id.aniadir);

        aniadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nProd = nom.getText().toString().trim();
                String dProd = desc.getText().toString().trim();
                String pProd = prec.getText().toString().trim();

                if (nProd.isEmpty() || dProd.isEmpty() || pProd.isEmpty()) {
                    Toast.makeText(AniadirProducto.this, "Ingrese los datos", Toast.LENGTH_SHORT).show();
                } else {
                    // Verificar la longitud del campo de descripción
                    if (dProd.length() > 400) {
                        Toast.makeText(AniadirProducto.this, "La descripción no puede superar los 400 caracteres", Toast.LENGTH_SHORT).show();
                    } else {
                        postProd(nProd, dProd, pProd);
                    }
                }
            }
        });


    }

    private void postProd(String nProd, String dProd, String pProd) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", nProd);
        map.put("desc", dProd);
        map.put("price", pProd);
        mFirestore.collection("products").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            //Never enters on on Success or onFailure function
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(AniadirProducto.this, "Producto subido con exito", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AniadirProducto.this, "Error al ingresar producto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void topBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Configura el color del título, por ejemplo
            actionBar.setTitle(Html.fromHtml("<font color=\"#F2A71B\">Añadir Producto</font>"));
        }
    }

}