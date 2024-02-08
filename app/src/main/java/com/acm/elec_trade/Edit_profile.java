package com.acm.elec_trade;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Edit_profile extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private TextInputEditText nombre;
    private ImageView fotoPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        fotoPerfil=findViewById(R.id.profilePick);
        nombre = findViewById(R.id.name);

        // Verifica si el usuario actual existe
        if (user != null) {
            // Obtiene el ID del usuario actual
            String userId = user.getUid();

            // Accede al documento del usuario en Firestore
            firebaseFirestore.collection("user")
                    .document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Obtiene el nombre del documento y establece el texto en el TextInputEditText
                                String nombreUsuario = document.getString("name");
                                nombre.setText(nombreUsuario);
                                nombre.setEnabled(false);
                                nombre.setAlpha(0.5f);
                            } else {
                                // El documento no existe
                            }
                        } else {
                            // Error al obtener el documento
                        }
                    });
        }


        Glide.with(this)
                .load(R.drawable.user_icon)
                .circleCrop()
                .into(fotoPerfil);
    }
}