package com.acm.elec_trade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acm.elec_trade.Main;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class ProductoDetalle extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_detalle);
        // Inicializa FirebaseFirestore
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        // Recupera la información del Intent
        Intent intent = getIntent();
        String nombreProducto = intent.getStringExtra("idProducto");
        obtenerDatosDelProducto(nombreProducto);
        obtenerDatosUsuario(nombreProducto);
        TextView nProd = findViewById(R.id.nameProduct);
        nProd.setText(nombreProducto);
        // Boton para añadir al carrito
        Button cart = findViewById(R.id.addCart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                aniadirAlCarrito(uid, nombreProducto);
            }
        });

        // Botón de contacto con el usuario
        Button contactUser = findViewById(R.id.contactUser);
        contactUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el nombre y el correo del vendedor
                TextView userNameTextView = findViewById(R.id.userName);
                String nombreVendedor = userNameTextView.getText().toString();
                TextView userEmailTextView = findViewById(R.id.userMail);
                String correoVendedor = userEmailTextView.getText().toString();

                // Abrir el correo con el nombre del vendedor ya escrito
                abrirCorreoConNombreVendedor(nombreVendedor, correoVendedor);
            }
        });
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
                            mostrarDatosEnLaInterfaz(descripcion, precio, photo);
                        }
                    } else {
                        // Error al realizar la consulta
                        showToast("Error al obtener datos del producto: " + task.getException().getMessage());
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
                                        if (task1.isSuccessful()) {
                                            for (QueryDocumentSnapshot d : task1.getResult()) {
                                                // El documento existe, ahora puedes obtener más datos
                                                String userName = d.getString("name");
                                                String userMail = d.getString("email");
                                                mostrarDatosEnLaInterfazUsuario(userName, userMail);
                                            }
                                        } else {
                                            // Error al realizar la segunda consulta
                                            showToast("Error al obtener datos del usuario: " + task1.getException().getMessage());
                                        }
                                    });
                        }
                    } else {
                        // Error al realizar la primera consulta
                        showToast("Error al obtener datos del producto: " + task.getException().getMessage());
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

    private void aniadirAlCarrito(String uid, String nombreProducto) {
        // Obtener el producto completo de la colección "products"
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
                            String userId = document.getString("userP");

                            // Crear un mapa con los datos del producto
                            Map<String, Object> productoCarrito = new HashMap<>();
                            productoCarrito.put("nombre", nombreProducto);
                            productoCarrito.put("descripcion", descripcion);
                            productoCarrito.put("precio", precio);
                            productoCarrito.put("imagen", photo);
                            productoCarrito.put("usuario", userId);

                            // Agregar el producto a la colección "cart" del usuario
                            firebaseFirestore.collection("user").document(uid).collection("cart")
                                    .add(productoCarrito)
                                    .addOnSuccessListener(documentReference -> showToast("Producto agregado al carrito"))
                                    .addOnFailureListener(e -> showToast("Error al agregar producto al carrito: " + e.getMessage()));
                        }
                    } else {
                        // Error al realizar la consulta
                        showToast("Error al obtener datos del producto: " + task.getException().getMessage());
                    }
                });
    }


    private void abrirCorreoConNombreVendedor(String nombreVendedor, String correoVendedor) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{correoVendedor});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Consulta sobre producto");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hola " + nombreVendedor + ", ");
        emailIntent.setType("message/rfc822");

        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar correo"));
        } catch (android.content.ActivityNotFoundException ex) {
            showToast("No hay aplicaciones de correo instaladas.");
        }
    }





    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
