package com.nepapp.doineedit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class WrapperActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrapper);
    }

    public void btnNavigateAddProduct(View view) {
        startActivity(new Intent(WrapperActivity.this, AddProductActivity.class));
    }

    public void btnNavigateDashboard(View view) {
        startActivity(new Intent(WrapperActivity.this, DashboardActivity.class));
    }

    public void btnExitApp(View view) {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}