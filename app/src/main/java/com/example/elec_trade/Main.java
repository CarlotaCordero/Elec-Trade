package com.example.elec_trade;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.elec_trade.Adapter.Producto;
import com.example.elec_trade.Adapter.ProductoAdapter;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class Main extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductoAdapter productoAdapter;
    private FloatingActionButton aniadirProd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView mybottomNavView = findViewById(R.id.bottom_navigation);
        aniadirProd = findViewById(R.id.addProduct);

        //Set BottomNavigationView
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) mybottomNavView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(2);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;
        LayoutInflater.from(this)
                .inflate(R.layout.layout_badge, itemView, true);
        //Inicializa el RecyclerView
        inicializarRecyclerView();
        //Accion para añadir producto
        aniadirProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addProducto = new Intent(Main.this, AniadirProducto.class);
                startActivity(addProducto);
            }
        });
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