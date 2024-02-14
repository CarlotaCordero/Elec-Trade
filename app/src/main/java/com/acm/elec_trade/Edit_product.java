package com.acm.elec_trade;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Edit_product extends AppCompatActivity {
    private Button editar, editarImagen;
    private TextInputEditText nom, desc, prec;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private static final int PICK_IMAGE = 100;
    private ImageView imageView;
    private Uri selectedImageUri;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String nombreProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aniadir_producto);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        Intent intent = getIntent();
        nombreProducto = intent.getStringExtra("nomProduc");

        nom = findViewById(R.id.nomProducto);
        desc = findViewById(R.id.descProducto);
        prec = findViewById(R.id.precProducto);
        editar = findViewById(R.id.aniadir);
        editarImagen = findViewById(R.id.aniadirimagen);
        imageView = findViewById(R.id.imageViewPreview);

        // Inicializa FirebaseFirestore
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        // Recupera la información del Intent
        Intent editProduct = getIntent();
        nombreProducto = editProduct.getStringExtra("idProducto");
        obtenerDatosDelProducto(nombreProducto);
        nom.setHint(nombreProducto);

        editar.setText("Save Changes");

        topBar();
    }

    private void obtenerDatosDelProducto(String nombreProducto) {
        // Realiza una consulta para obtener el documento del producto
        firebaseFirestore.collection("products")
                .whereEqualTo("name", nombreProducto)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // El documento existe, ahora puedes obtener más datos
                            String descripcion = document.getString("desc");
                            String precio = document.getString("price");
                            String photo = document.getString("imgurl");
                            //String userProd = document.getString("userP");
                            // Ahora puedes usar la información como desees
                            mostrarDatosEnLaInterfaz(descripcion, precio, photo);
                        }
                    } else {
                        // Error al realizar la consulta
                    }
                });
    }

    private void mostrarDatosEnLaInterfaz(String descripcion, String precio, String photo) {
        desc.setText(descripcion);
        prec.setText(precio);

        // También puedes cargar la imagen en el ImageView si es necesario
        Glide.with(this).load(photo).centerCrop().into(imageView);
    }

    private void topBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(Html.fromHtml("<font color=\"#F2A71B\">Edit Product</font>"));
        }
    }
}
