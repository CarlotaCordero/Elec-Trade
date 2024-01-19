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

        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            Drawable icon = menuItem.getIcon();

            //if (icon != null) {
                icon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
                menuItem.setIcon(icon);
            //}
        }
        bottomAppBar.setOnNavigationItemSelectedListener(item -> {
            // Maneja la selección del elemento del menú aquí
            return true;
        });

        //Inicializa el RecyclerView
        inicializarRecyclerView();
    }
        /*
        //setupToolbar();


        // Obtén una referencia al icono de navegación del BottomAppBar
        Drawable navigationIcon = bottomAppBar.getNavigationIcon();

        // Aplica un filtro de color blanco al icono de navegación
        navigationIcon.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);

        // Configura el icono de navegación actualizado en el BottomAppBar
        bottomAppBar.setNavigationIcon(navigationIcon);
        // Obtén una referencia al menú


        // Obtén una referencia a cada elemento del menú y aplica el filtro de color blanco

*/
    /*
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.bottomAppBar);
        setSupportActionBar(toolbar);
    }
    */

    private void inicializarRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Producto> productoList = new ArrayList<>();

        for(int i = 0; i < 30; i++) {
            productoList.add(new Producto("R.color.black","Producto"+i,"Precio"+i));
        }

        productoAdapter = new ProductoAdapter(productoList, this);

        recyclerView.setAdapter(productoAdapter);
    }

}