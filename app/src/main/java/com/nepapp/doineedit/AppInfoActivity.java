package com.nepapp.doineedit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;

public class AppInfoActivity extends AppCompatActivity {
    private ListView listViewFeatures;
    private TextView textAppInfo;
    private MaterialToolbar topAppBar;
    private SharedPreferences sharedPreferences;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        listViewFeatures = findViewById(R.id.listViewFeatures);
        textAppInfo = findViewById(R.id.textAppInfo);
        topAppBar = findViewById(R.id.topAppBar);
//        topAppBar.setOverflowIcon(getDrawable(R.drawable.ic_vertical_bar));
        setSupportActionBar(topAppBar);
        getSupportActionBar().setTitle(null);



        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sharedPreferences = getSharedPreferences("LoginSharedPreference", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("access-token", "");
        if (token.isEmpty()) {
            startActivity(new Intent(AppInfoActivity.this, MainActivity.class));
            Toast.makeText(AppInfoActivity.this, "Login authentication required", Toast.LENGTH_SHORT).show();
        }

        String info = "This app is designed to assist students and others in saving money by preventing them from purchasing items they do not require. " +
                "It has become far too easy to waste money and buy items unnecessarily in this age of next-day deliveries and one-click purchases." +
                "\nAs a result, this software encourages users to save product items and purchase them later.";


        ArrayList<String> features = new ArrayList<>();
        features.add("Login and Signup");
        features.add("Save product into the list");
        features.add("Display product list");
        features.add("Edit saved product");
        features.add("Mark product purchased");
        features.add("Visit webpage through the app");
        features.add("Send product item to others via SMS, Messenger, Signal etc.");


        ArrayAdapter<String> featuresAdapter = new ArrayAdapter<String>(AppInfoActivity.this, android.R.layout.simple_list_item_1, features);
        listViewFeatures.setAdapter(featuresAdapter);

        textAppInfo.setText(info);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_menu, menu);

        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mi_logout:
                sharedPreferences.edit().remove("access-token").commit();

                Intent intentLogout = new Intent(AppInfoActivity.this, MainActivity.class);
                intentLogout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentLogout);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void btnNavigateAddProduct(View view) {
        overridePendingTransition(0, 0);
        startActivity(new Intent(AppInfoActivity.this, AddProductActivity.class));
        overridePendingTransition(0, 0);
    }

    public void btnNavigateDashboard(View view) {
        overridePendingTransition(0, 0);
        finish();
        startActivity(new Intent(AppInfoActivity.this, DashboardActivity.class));
        overridePendingTransition(0, 0);
    }

    public void btnNavigateAccount(View view) {
        overridePendingTransition(0, 0);
        startActivity(new Intent(AppInfoActivity.this, AccountActivity.class));
        overridePendingTransition(0, 0);
    }

    public void btnExitApp(View view) {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}