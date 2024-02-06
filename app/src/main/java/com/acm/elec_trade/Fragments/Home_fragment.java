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

import com.acm.elec_trade.Adapter.Producto;
import com.acm.elec_trade.Adapter.ProductoAdapter;
import com.acm.elec_trade.AniadirProducto;
import com.acm.elec_trade.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //
    private RecyclerView recyclerView;
    private ProductoAdapter productoAdapter;
    private FloatingActionButton aniadirProd;

    public Home_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Home_fragment newInstance(String param1, String param2) {
        Home_fragment fragment = new Home_fragment();
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
        //Instanciamos nuestros elementos
        aniadirProd = rootView.findViewById(R.id.addProduct);
        SearchView searchView = rootView.findViewById(R.id.searchView);
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        int textColor = ContextCompat.getColor(requireContext(), R.color.black);
        searchEditText.setTextColor(textColor);
        // Inicializa el RecyclerView
        inicializarRecyclerView(rootView);
        //Accion para añadir producto
        aniadirProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addProducto = new Intent(requireActivity(), AniadirProducto.class);
                startActivity(addProducto);
            }
        });
        return rootView;
    }

    private void inicializarRecyclerView(View rootView) {
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<Producto> productoList = new ArrayList<>();

        for(int i = 0; i < 30; i++) {
            productoList.add(new Producto("https://s3-symbol-logo.tradingview.com/intel--600.png","Producto"+i,"Precio"+i));
        }

        productoAdapter = new ProductoAdapter(productoList, requireContext());

        recyclerView.setAdapter(productoAdapter);

    }
}