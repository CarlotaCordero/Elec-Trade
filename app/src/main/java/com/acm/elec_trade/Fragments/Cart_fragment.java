package com.acm.elec_trade.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.acm.elec_trade.Adapter.ProductAdapterFB;
import com.acm.elec_trade.Adapter.ProductFB;
import com.acm.elec_trade.AniadirProducto;
import com.acm.elec_trade.Main;
import com.acm.elec_trade.ProductoDetalle;
import com.acm.elec_trade.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Cart_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Cart_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //
    private RecyclerView recyclerViewCart;
    private ProductAdapterFB mProductAdapterFB;
    FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public Cart_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Cart_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Cart_fragment newInstance(String param1, String param2) {
        Cart_fragment fragment = new Cart_fragment();
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
        // Creamos el rootView
        View rootView = inflater.inflate(R.layout.fragment_cart_fragment, container, false);
        // Instanciamos el Firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        // Inicializa el RecyclerView
        recyclerViewCart = rootView.findViewById(R.id.recyclerViewCart);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Obtén el UID del usuario actual
        String uid = firebaseAuth.getCurrentUser().getUid();
        // Consulta los productos en el carrito del usuario actual
        Query query = firebaseFirestore.collection("user").document(uid).collection("cart");
        FirestoreRecyclerOptions<ProductFB> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<ProductFB>().setQuery(query, ProductFB.class).build();
        mProductAdapterFB = new ProductAdapterFB(firestoreRecyclerOptions);
        mProductAdapterFB.notifyDataSetChanged();
        mProductAdapterFB.startListening();
        mProductAdapterFB.setOnItemClickListener(new ProductAdapterFB.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                // Obtén el modelo de producto correspondiente al documento
                ProductFB clickedProduct = documentSnapshot.toObject(ProductFB.class);
                //Verificar existencia del producto
                verificarExistenciaProducto(clickedProduct);
                // Implementa la lógica para abrir el nuevo Activity aquí
                /*Intent intent = new Intent(getContext(), ProductoDetalle.class);
                intent.putExtra("idProducto", clickedProduct.getName());
                // Puedes usar Intent para iniciar un nuevo Activity, pasando la información necesaria
                startActivity(intent);*/
            }
        });
        recyclerViewCart.setAdapter(mProductAdapterFB);
        checkIfCartIsEmpty(rootView);
        return rootView;
    }

    private void verificarExistenciaProducto(ProductFB product) {
        FirebaseFirestore.getInstance().collection("products")
                .whereEqualTo("name", product.getName())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // El documento existe, abrir la actividad ProductoDetalle
                            abrirProductoDetalle(product.getName()); // Utilizo document.getId() como identificador único del documento
                        }
                    } else {
                        // Error al realizar la consulta
                        Log.e("ExistenciaProducto", "Error al obtener datos del producto: " + task.getException());
                    }
                });
    }

    private void abrirProductoDetalle(String productId) {
        Intent intent = new Intent(getContext(), ProductoDetalle.class);
        intent.putExtra("idProducto", productId);
        startActivity(intent);
    }

    private void eliminarProductoDelCarrito(String productId) {
        String uid = firebaseAuth.getCurrentUser().getUid();

        // Referencia al documento del producto en el carrito del usuario
        firebaseFirestore.collection("user").document(uid).collection("cart").document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    eliminarDelCarrito(uid, productId);
                })
                .addOnFailureListener(e -> {
                    Log.e("EliminarProducto", "Error al eliminar el producto del carrito", e);
                    showToast("Error al eliminar el producto del carrito");
                });
    }

    private void eliminarDelCarrito(String uid, String productId) {
        // Realiza una consulta para obtener el documento del producto en el carrito
        firebaseFirestore.collection("user").document(uid).collection("cart")
                .whereEqualTo("name", productId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // El documento existe, ahora puedes eliminarlo
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> showConfirmationDialog(productId))
                                    .addOnFailureListener(e -> showToast("Error al eliminar producto del carrito"));
                        }
                    } else {
                        // Error al realizar la consulta
                        showToast("Error al obtener datos del producto en el carrito");
                    }
                });
    }

    private void showConfirmationDialog(String productId) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle(R.string.prodDelete);
        builder.setMessage(productId);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Puedes realizar alguna acción después de hacer clic en OK, si es necesario
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkIfCartIsEmpty(View rootView) {
        String uid = firebaseAuth.getCurrentUser().getUid();

        // Consulta los productos en el carrito del usuario actual
        firebaseFirestore.collection("user").document(uid).collection("cart")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // El carrito está vacío, muestra el ImageView
                            rootView.findViewById(R.id.imageEmptyCart).setVisibility(View.VISIBLE);
                        } else {
                            // El carrito no está vacío, oculta el ImageView y muestra el RecyclerView
                            rootView.findViewById(R.id.imageEmptyCart).setVisibility(View.INVISIBLE);
                        }
                    } else {
                        // Manejar el error al verificar la existencia de productos en el carrito
                        //Toast.makeText(getContext(), "Error al obtener datos del carrito", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    //Revisar
    /*@Override
    public void onStart() {
        super.onStart();
        mProductAdapterFB.startListening();
    }*/

    @Override
    public void onPause() {
        super.onPause();
        mProductAdapterFB.stopListening();
    }
    //Revisar
    /*@Override
    public void onResume() {
        super.onResume();
        mProductAdapterFB.startListening();
    }*/

    @Override
    public void onStop() {
        super.onStop();
        mProductAdapterFB.stopListening();
    }
}