package com.acm.elec_trade;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class Edit_profile extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private TextInputEditText nombre, newNombre;
    private ImageView fotoPerfil;
    private TextView nameUser;
    private Button subirFoto, guardarCambios;
    private static final int PICK_IMAGE = 100;
    private Uri selectedImageUri;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        topBar();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        fotoPerfil = findViewById(R.id.profilePick);
        nombre = findViewById(R.id.name);
        newNombre = findViewById(R.id.newName);
        nameUser = findViewById(R.id.nameUser);
        subirFoto = findViewById(R.id.buttonEditPick);
        guardarCambios = findViewById(R.id.buttonGuardarCambios);

        storageReference = FirebaseStorage.getInstance().getReference();

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
                                nameUser.setText(nombreUsuario);

                                // Obtiene la URL de la foto de perfil y la carga usando Glide
                                String photoUrl = document.getString("fotoperfil");
                                if (photoUrl != null && !photoUrl.isEmpty()) {
                                    Glide.with(this)
                                            .load(photoUrl)
                                            .circleCrop()
                                            .into(fotoPerfil);
                                } else {
                                    // Si no hay foto de perfil, muestra la imagen predeterminada
                                    Glide.with(this)
                                            .load(R.drawable.user_icon)
                                            .circleCrop()
                                            .into(fotoPerfil);
                                }
                            } else {
                                // El documento no existe
                            }
                        } else {
                            // Error al obtener el documento
                        }
                    });
        }

        subirFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        guardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nuevoNombre = newNombre.getText().toString().trim();

                if (selectedImageUri != null) {
                    if(nuevoNombre.isEmpty()){
                        uploadImage(selectedImageUri,nombre.getText().toString());
                    }else{
                        // Subir la imagen al Storage
                        uploadImage(selectedImageUri, nuevoNombre);
                    }
                } else {
                    // No se seleccionó una nueva imagen, solo actualizar el nombre
                    if (!nuevoNombre.isEmpty()) {
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                        if (currentUser != null) {
                            updateUserNameAndPhoto(currentUser.getUid(), nuevoNombre, null);
                        }
                    } else {
                        Toast.makeText(Edit_profile.this, "Ingrese un nuevo nombre o seleccione una imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            selectedImageUri = data.getData();
            fotoPerfil.setImageURI(selectedImageUri);
        }
    }

    private void uploadImage(Uri imageUri, final String nuevoNombre) {
        // Define la ruta en el Storage donde se almacenará la imagen
        final StorageReference imageRef = storageReference.child("images/" + firebaseAuth.getCurrentUser().getUid() + ".jpg");

        // Sube la imagen al Storage
        UploadTask uploadTask = imageRef.putFile(imageUri);

        // Maneja los eventos de éxito y fracaso de la carga
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Obtiene la URL de la imagen subida
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUri) {
                        // Actualiza el nombre y la URL de la imagen en Firestore
                        updateUserNameAndPhoto(firebaseAuth.getCurrentUser().getUid(), nuevoNombre, downloadUri.toString());
                        finish();
                        openMainActivity();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Edit_profile.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserNameAndPhoto(String userId, String nuevoNombre, String photoUrl) {
        DocumentReference userRef = firebaseFirestore.collection("user").document(userId);
// Crea un mapa con los datos a actualizar
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", nuevoNombre);

        // Agrega la URL de la imagen solo si se proporciona
        if (photoUrl != null) {
            updates.put("fotoperfil", photoUrl);
        }

        // Actualiza los datos en Firestore
        userRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Edit_profile.this, "Cambios guardados con éxito", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Edit_profile.this, "Error al guardar los cambios", Toast.LENGTH_SHORT).show();
                });
    }

    private void topBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(Html.fromHtml("<font color=\"#F2A71B\">Edit Profile</font>"));
        }
    }

    private void openMainActivity() {
        Intent mainIntent = new Intent(Edit_profile.this, Main.class);
        startActivity(mainIntent);
        finishAffinity();
    }
}