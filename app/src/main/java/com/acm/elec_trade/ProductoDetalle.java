package com.acm.elec_trade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ProductoDetalle extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_detalle);
        // Inicializa FirebaseFirestore
        firebaseFirestore = FirebaseFirestore.getInstance();
        // Recupera la información del Intent
        Intent intent = getIntent();
        String nombreProducto = intent.getStringExtra("idProducto");
        obtenerDatosDelProducto(nombreProducto);
        obtenerDatosUsuario(nombreProducto);
        TextView nProd = findViewById(R.id.nameProduct);
        nProd.setText(nombreProducto);
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

    private void obtenerDatosUsuario(String nombreProducto) {
        // Realiza una consulta para obtener el documento del producto
        firebaseFirestore.collection("products")
                .whereEqualTo("name", nombreProducto)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // El documento existe, ahora puedes obtener más datos
                            String userId = document.getString("userP");
                            firebaseFirestore.collection("user")
                                    .whereEqualTo("id", userId)
                                    .get().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) { // Corregir aquí
                                            for (QueryDocumentSnapshot d : task1.getResult()) {
                                                // El documento existe, ahora puedes obtener más datos
                                                String userName = d.getString("name");
                                                String userMail = d.getString("email");
                                                // Ahora puedes usar la información como desees
                                                mostrarDatosEnLaInterfazUsuario(userName, userMail);
                                            }
                                        } else {
                                            // Error al realizar la segunda consulta
                                            // Maneja el error de alguna manera
                                        }
                                    });
                        }
                    } else {
                        // Error al realizar la primera consulta
                        // Maneja el error de alguna manera
                    }
                });
    }

    private void mostrarDatosEnLaInterfazUsuario(String userName, String userMail) {
        TextView name = findViewById(R.id.userName);
        TextView email = findViewById(R.id.userMail);

        name.setText(userName);
        email.setText(String.valueOf(userMail));
    }

    private void mostrarDatosEnLaInterfaz(String descripcion, String precio, String photo) {
        TextView descripcionTextView = findViewById(R.id.descriptionProduct);
        TextView precioTextView = findViewById(R.id.priceProduct);
        ImageView imagenImageView = findViewById(R.id.photoProduct);

        descripcionTextView.setText(descripcion);
        precioTextView.setText(precio+"€");
        Glide.with(this).load(photo).centerCrop().into(imagenImageView);
    }

    @Override
    public void onBackPressed() {
        // Agrega cualquier lógica adicional que desees al presionar el botón de retroceso.
        // Por ejemplo, puedes realizar alguna acción específica antes de cerrar la actividad.

        // Elimina la llamada a super.onBackPressed() para evitar cerrar la actividad.
        super.onBackPressed();
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
    }
}