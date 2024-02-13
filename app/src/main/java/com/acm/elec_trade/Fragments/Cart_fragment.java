package com.acm.elec_trade.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.acm.elec_trade.Adapter.ProductAdapterFB;
import com.acm.elec_trade.Adapter.ProductFB;
import com.acm.elec_trade.AniadirProducto;
import com.acm.elec_trade.ProductoDetalle;
import com.acm.elec_trade.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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
        View rootView = inflater.inflate(R.layout.fragment_home_fragment, container, false);
        // Instanciamos el Firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        // Inicializa el RecyclerView
        recyclerViewCart = rootView.findViewById(R.id.recyclerView);
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
                // Implementa la lógica para abrir el nuevo Activity aquí
                Intent intent = new Intent(getContext(), ProductoDetalle.class);
                intent.putExtra("idProducto", clickedProduct.getName());
                // Puedes usar Intent para iniciar un nuevo Activity, pasando la información necesaria
                startActivity(intent);
            }
        });
        recyclerViewCart.setAdapter(mProductAdapterFB);
        return rootView;
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