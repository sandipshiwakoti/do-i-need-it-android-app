package com.nepapp.doineedit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.nepapp.doineedit.api.ApiInterface;
import com.nepapp.doineedit.api.RetrofitClient;
import com.nepapp.doineedit.models.SuccessResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductActivity extends AppCompatActivity {
    private TextView textProductTitle;
    private TextView textProductDescription;
    private TextView textProductPrice;
    private FloatingActionButton btnMarkPurchased;
    private Button btnVisitProductUrl;
    private Button btnSendProduct;
    private Button btnNavigateProductLocation;
    private ImageView imgProduct;
    private SharedPreferences sharedPreferences;
    private String token;
    private CircularProgressIndicator progressIndicator;
    private String userFullname;
    private MaterialToolbar topAppBar;
    private String id;
    private String title;
    private String price;
    private String description;
    private String url;
    private String image;
    private boolean purchased;
    private String location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        textProductTitle = findViewById(R.id.textProductTitle);
        textProductDescription = findViewById(R.id.textProductDescription);
        textProductPrice = findViewById(R.id.textProductPrice);
        btnMarkPurchased = findViewById(R.id.btnMarkPurchased);
        btnVisitProductUrl = findViewById(R.id.btnVisitProductUrl);
        btnSendProduct = findViewById(R.id.btnSendProduct);
        btnNavigateProductLocation = findViewById(R.id.btnNavigateProductLocation);
        imgProduct = findViewById(R.id.imgProduct);

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


        progressIndicator = findViewById(R.id.progressIndicator);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");
        price = intent.getStringExtra("price");
        url = intent.getStringExtra("url");
        image = intent.getStringExtra("image");
        description = intent.getStringExtra("description");
        purchased = intent.getExtras().getBoolean("purchased");
        location = intent.getStringExtra("location");

        textProductTitle.setText(title);
        textProductDescription.setText(description);
        textProductPrice.setText("NPR " + price);
        Picasso.get().load(image).into(imgProduct);

        sharedPreferences = getSharedPreferences("LoginSharedPreference", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("access-token", "");
        if (token.isEmpty()) {
            startActivity(new Intent(ProductActivity.this, MainActivity.class));
            Toast.makeText(ProductActivity.this, "Login authentication required", Toast.LENGTH_SHORT).show();
        }

        try {
            JSONObject payload = JWTUtils.decoded(token);
            userFullname = payload.getString("fullname");
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (purchased) {
            btnMarkPurchased.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            btnMarkPurchased.setColorFilter(Color.WHITE);
        } else {
            btnMarkPurchased.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            btnMarkPurchased.setColorFilter(Color.BLACK);
        }

        btnNavigateProductLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductActivity.this, ProductMapActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("location", location);
                finish();
                startActivity(intent);
            }
        });

        btnMarkPurchased.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressIndicator.setVisibility(View.VISIBLE);
                String newPurchased = Boolean.toString(!purchased);
                ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
                Call<SuccessResponse> call = apiInterface.markProductPurchased("Bearer " + token, newPurchased, id);
                call.enqueue(new Callback<SuccessResponse>() {
                    @Override
                    public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
                        progressIndicator.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            Log.e("MarkPurchasedLog: ", "OnSuccess: " + new Gson().toJson(response.body()));

                            overridePendingTransition(0, 0);
                            Intent intent = new Intent(ProductActivity.this, ProductActivity.class);
                            intent.putExtra("id", id);
                            intent.putExtra("title", title);
                            intent.putExtra("price", price);
                            intent.putExtra("url", url);
                            intent.putExtra("description", description);
                            intent.putExtra("image", image);
                            intent.putExtra("purchased", Boolean.valueOf(newPurchased));
                            startActivity(intent);
                            overridePendingTransition(0, 0);

                            if (Boolean.valueOf(newPurchased)) {
                                Toast.makeText(ProductActivity.this, "Product marked as purchased!",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ProductActivity.this, "Product marked as not purchased!",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            String data = null;
                            try {
                                data = response.errorBody().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                JSONObject jObjError = new JSONObject(data);
                                Toast.makeText(ProductActivity.this, jObjError.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(ProductActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SuccessResponse> call, Throwable t) {
                        progressIndicator.setVisibility(View.GONE);
                        Log.e("OnErrorMark", t.getMessage());
                        Toast.makeText(ProductActivity.this, "Error in marking", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        btnVisitProductUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("URLCheck", url);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

        btnSendProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Hey, " + userFullname + " wants you to buy this awesome product: " + url;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("sms:"));
                intent.putExtra("sms_body", message);
                startActivity(intent);
            }
        });

    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_top_app_menu, menu);

        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
//            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mi_edit:
                Intent intentEdit = new Intent(ProductActivity.this, EditProductActivity.class);
                intentEdit.putExtra("id", id);
                intentEdit.putExtra("title", title);
                intentEdit.putExtra("price", price);
                intentEdit.putExtra("description", description);
                intentEdit.putExtra("url", url);
                intentEdit.putExtra("image", image);
                intentEdit.putExtra("purchased", purchased);
                startActivity(intentEdit);
                return true;
            case R.id.mi_delete:
                progressIndicator.setVisibility(View.VISIBLE);
                ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
                Call<SuccessResponse> call = apiInterface.deleteProduct("Bearer " + token, id);
                call.enqueue(new Callback<SuccessResponse>() {
                    @Override
                    public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
                        progressIndicator.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            Log.e("OnSuccessDelete: ", "OnSuccess: " + new Gson().toJson(response.body()));
                            Intent intent = new Intent(ProductActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            Toast.makeText(ProductActivity.this, "Product deleted successfully!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            String data = null;
                            try {
                                data = response.errorBody().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                JSONObject jObjError = new JSONObject(data);
                                Toast.makeText(ProductActivity.this, jObjError.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(ProductActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SuccessResponse> call, Throwable t) {
                        progressIndicator.setVisibility(View.GONE);
                        Log.e("OnErrorDelete", t.getMessage());
                        Toast.makeText(ProductActivity.this, "Error in deletion", Toast.LENGTH_LONG).show();
                    }
                });
                return true;
            case R.id.mi_logout:
                sharedPreferences.edit().remove("access-token").commit();
                Intent intentLogout = new Intent(ProductActivity.this, MainActivity.class);
                intentLogout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentLogout);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void btnNavigateAddProduct(View view) {
        overridePendingTransition(0, 0);
        startActivity(new Intent(ProductActivity.this, AddProductActivity.class));
        overridePendingTransition(0, 0);
    }

    public void btnNavigateDashboard(View view) {
        overridePendingTransition(0, 0);
        startActivity(new Intent(ProductActivity.this, DashboardActivity.class));
        overridePendingTransition(0, 0);
    }

    public void btnNavigateAppInfo(View view) {
        overridePendingTransition(0, 0);
        startActivity(new Intent(ProductActivity.this, AppInfoActivity.class));
        overridePendingTransition(0, 0);
    }

    public void btnNavigateAccount(View view) {
        overridePendingTransition(0, 0);
        startActivity(new Intent(ProductActivity.this, AccountActivity.class));
        overridePendingTransition(0, 0);
    }

    public void btnExitApp(View view) {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}