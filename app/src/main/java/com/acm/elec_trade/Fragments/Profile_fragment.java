package com.acm.elec_trade.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acm.elec_trade.Adapter.ProductAdapterFB;
import com.acm.elec_trade.Adapter.ProductFB;
import com.acm.elec_trade.Adapter.Producto;
import com.acm.elec_trade.Adapter.ProductoAdapter;
import com.acm.elec_trade.Login;
import com.bumptech.glide.Glide;
import com.acm.elec_trade.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Profile_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //

    private Button lOut;
    private ImageView profilePic;
    private TextView uName, uEmail;
    private RecyclerView mRecyclerView;
    private ProductAdapterFB mProductAdapterFB;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    public Profile_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile_fragment newInstance(String param1, String param2) {
        Profile_fragment fragment = new Profile_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();  // Inicializa firebaseAuth
        // Instanciamos el Firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
        // Instanciamos el Firebase
        // Creamos el rootView
        View rootView = inflater.inflate(R.layout.fragment_profile_fragment, container, false);
        lOut = rootView.findViewById(R.id.logout);
        lOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog();
            }
        });
        //Sacar datos de usuario
        FirebaseUser user = firebaseAuth.getCurrentUser();
        uName = rootView.findViewById(R.id.profileName);
        String uid = user.getUid();
        DocumentReference userReference = firebaseFirestore.collection("user").document(uid);
        bucarUsuario(userReference);
        uEmail = rootView.findViewById(R.id.profileEmail);
        uEmail.setText(user.getEmail().toString());
        // Inicializa el RecyclerView
        inicializarRecyclerView(rootView, uid);
        //Subir foto con glide
        profilePic = rootView.findViewById(R.id.profilePic);
        Glide.with(requireContext())
                .load(R.drawable.user_icon)
                .circleCrop()
                .into(profilePic);
        return rootView;
    }

    private void showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
            builder.setTitle("LOGOUT");
            builder.setCancelable(false);
            //Set buttons
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    firebaseAuth.signOut();
                    goToLogin();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        //Create dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void goToLogin() {
        Intent toLogin = new Intent(requireActivity(), Login.class);
        toLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        toLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toLogin);
        requireActivity().finish();
    }

    private void inicializarRecyclerView(View rootView, String uid) {
        mRecyclerView = rootView.findViewById(R.id.reciclerProfile);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        Query query = firebaseFirestore.collection("products").whereEqualTo("userP", uid);
        FirestoreRecyclerOptions<ProductFB> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<ProductFB>().setQuery(query, ProductFB.class).build();
        mProductAdapterFB = new ProductAdapterFB(firestoreRecyclerOptions);
        mProductAdapterFB.notifyDataSetChanged();
        mProductAdapterFB.setOnItemClickListener(new ProductAdapterFB.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                // Obtén el modelo de producto correspondiente al documento
                ProductFB clickedProduct = documentSnapshot.toObject(ProductFB.class);

                // Implementa la lógica para abrir el nuevo Activity aquí
                // Puedes usar Intent para iniciar un nuevo Activity, pasando la información necesaria
                Toast.makeText(getContext(), "Pulsaste la tarjeta " + position, Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(mProductAdapterFB);
    }

    private void bucarUsuario(DocumentReference userReference) {
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Obtener los datos del usuario
                        String username = document.getString("name");
                        //Se cambia el texto por el nombre de usuario
                        uName.setText(username);
                    } else {
                        // El documento del usuario no existe en Firestore
                        // Puedes manejar esto según tus necesidades
                    }
                } else {
                    // Manejar errores de lectura de Firestore si es necesario
                    Exception exception = task.getException();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mProductAdapterFB.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mProductAdapterFB.stopListening();
    }
}