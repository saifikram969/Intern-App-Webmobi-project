package com.example.internapp_webmobi_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView=findViewById(R.id.btmnav);


        //for bottom navigation view

        // fro selecting navigation as home
        bottomNavigationView.setSelectedItemId(R.id.navhome);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemid= item.getItemId();
                if(itemid==R.id.navprofile)
                {
                    startActivity(new Intent(getApplicationContext(), Edit_Profile.class));
                }
                return true;
            }
        });
    }
}