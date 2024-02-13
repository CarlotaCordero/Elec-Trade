package com.acm.elec_trade;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private String userIdOfProduct;

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
        verificarSiEnCarrito(nombreProducto);
        TextView nProd = findViewById(R.id.nameProduct);
        nProd.setText(nombreProducto);
        //Boton para añadir al carrito
        Button cart = findViewById(R.id.addCart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                aniadirAlCarrito(uid, nombreProducto);
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
                            verificarPropietarioDelProducto(userId);
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

    private void verificarPropietarioDelProducto(String userId) {
        // Obtén el UID del usuario actual
        String currentUserUid = firebaseAuth.getCurrentUser().getUid();

        // Tarjetas de Usuario y Producto
        CardView cardUsuario = findViewById(R.id.cardUsuario);
        CardView cardProducto = findViewById(R.id.cardProducto);

        // Verificar si el usuario actual es el propietario del producto
        if (userId.equals(currentUserUid)) {
            // El usuario actual es el propietario del producto, ocultar tarjeta de Usuario
            cardUsuario.setVisibility(View.GONE);
            // Mostrar tarjeta de Producto
            cardProducto.setVisibility(View.VISIBLE);
        } else {
            // El usuario actual no es el propietario del producto, ocultar tarjeta de Producto
            cardProducto.setVisibility(View.GONE);
            // Mostrar tarjeta de Usuario
            cardUsuario.setVisibility(View.VISIBLE);
        }

    }

    private void verificarSiEnCarrito(String nombreProducto) {
        // Obtén el UID del usuario actual
        String currentUserUid = firebaseAuth.getCurrentUser().getUid();

        // Realiza una consulta para verificar si el producto ya está en el carrito del usuario
        firebaseFirestore.collection("user").document(currentUserUid).collection("cart")
                .whereEqualTo("name", nombreProducto)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Button cartButton = findViewById(R.id.addCart);
                        // Verificar si hay algún documento en la consulta (producto en el carrito)
                        if (!task.getResult().isEmpty()) {
                            // El producto ya está en el carrito, cambiar el texto del botón y establecer un listener para eliminarlo
                            cartButton.setText("Remove from Cart");
                            cartButton.setOnClickListener(v -> eliminarDelCarrito(currentUserUid, nombreProducto));
                        } else {
                            // El producto no está en el carrito, mantener el comportamiento original del botón
                            cartButton.setOnClickListener(v -> aniadirAlCarrito(currentUserUid, nombreProducto));
                        }
                    } else {
                        // Error al realizar la consulta
                        showToast("Error al verificar el carrito: " + task.getException().getMessage());
                    }
                });
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
                            productoCarrito.put("name", nombreProducto);
                            productoCarrito.put("desc", descripcion);
                            productoCarrito.put("price", precio);
                            productoCarrito.put("imgurl", photo);
                            productoCarrito.put("userP", userId); // Agregar el UID del usuario que subió el producto

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

    private void eliminarDelCarrito(String uid, String nombreProducto) {
        // Realiza una consulta para obtener el documento del producto en el carrito
        firebaseFirestore.collection("user").document(uid).collection("cart")
                .whereEqualTo("name", nombreProducto)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // El documento existe, ahora puedes eliminarlo
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> showToast("Producto eliminado del carrito"))
                                    .addOnFailureListener(e -> showToast("Error al eliminar producto del carrito: " + e.getMessage()));
                        }
                    } else {
                        // Error al realizar la consulta
                        showToast("Error al obtener datos del producto en el carrito: " + task.getException().getMessage());
                    }
                });
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

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}