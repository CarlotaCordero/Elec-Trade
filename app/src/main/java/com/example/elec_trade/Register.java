package com.example.elec_trade;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;

public class Register extends AppCompatActivity {

    private Button su;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        su = findViewById(R.id.register);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Configura el color del título, por ejemplo
            actionBar.setTitle(Html.fromHtml("<font color=\"#F2A71B\">Add Product</font>"));
        }

        su.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMain = new Intent(Register.this, Main.class);
                toMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                toMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toMain);
            }
        });

    }
}