package com.example.elec_trade;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.elec_trade.Adapter.Producto;
import com.example.elec_trade.Adapter.ProductoAdapter;
import com.example.elec_trade.Fragments.SectionsPagerAdapter;
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
    private SectionsPagerAdapter sectionsPagerAdapter;
    private MenuItem prevMenuItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView mybottomNavView = findViewById(R.id.bottom_navigation);
        //aniadirProd = findViewById(R.id.addProduct);
        //page adapter
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        mybottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    item.setChecked(true);
                    Toast.makeText(Main.this, "Home", Toast.LENGTH_SHORT).show();
                    removeBadge(mybottomNavView,item.getItemId());
                    viewPager.setCurrentItem(0);
                } else if (item.getItemId() == R.id.chat) {
                    item.setChecked(true);
                    Toast.makeText(Main.this, "Chats", Toast.LENGTH_SHORT).show();
                    removeBadge(mybottomNavView,item.getItemId());
                    viewPager.setCurrentItem(1);
                } else if (item.getItemId() == R.id.cart) {
                    item.setChecked(true);
                    Toast.makeText(Main.this, "Cart", Toast.LENGTH_SHORT).show();
                    removeBadge(mybottomNavView,item.getItemId());
                    viewPager.setCurrentItem(2);
                } else if (item.getItemId() == R.id.profile) {
                    item.setChecked(true);
                    Toast.makeText(Main.this, "Profile", Toast.LENGTH_SHORT).show();
                    removeBadge(mybottomNavView,item.getItemId());
                    viewPager.setCurrentItem(3);
                }
                return false;
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    mybottomNavView.getMenu().getItem(0).setChecked(false);
                    mybottomNavView.getMenu().getItem(position).setChecked(true);
                    removeBadge(mybottomNavView, mybottomNavView.getMenu().getItem(position).getItemId());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        /*//Color buscador
        SearchView searchView = findViewById(R.id.searchView);
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        // Cambiar el color del texto
        int textColor = ContextCompat.getColor(this, R.color.black);
        searchEditText.setTextColor(textColor);
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
        });*/
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

    public static void removeBadge(BottomNavigationView bottomNavigationView, @IdRes int itemId) {
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
        if (itemView.getChildCount() == 3) {
            itemView.removeViewAt(2);
        }
    }

}