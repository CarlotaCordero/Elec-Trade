package com.example.elec_trade;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.elec_trade.Adapter.Producto;
import com.example.elec_trade.Adapter.ProductoAdapter;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;


public class Main extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductoAdapter productoAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomAppBar = findViewById(R.id.bottom_navigation);
        Menu menu = bottomAppBar.getMenu();

        //Inicializa el RecyclerView
        inicializarRecyclerView();
    }

    private void inicializarRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Producto> productoList = new ArrayList<>();

        for(int i = 0; i < 30; i++) {
            productoList.add(new Producto("https://s3-symbol-logo.tradingview.com/intel--600.png","Producto"+i,"Precio"+i));
        }

        productoAdapter = new ProductoAdapter(productoList, this);

        recyclerView.setAdapter(productoAdapter);
    }

}