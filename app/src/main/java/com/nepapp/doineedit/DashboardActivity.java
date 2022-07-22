package com.nepapp.doineedit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.nepapp.doineedit.adapter.RecyclerViewAdapter;
import com.nepapp.doineedit.api.ApiInterface;
import com.nepapp.doineedit.api.RetrofitClient;
import com.nepapp.doineedit.models.Product;
import com.nepapp.doineedit.models.ProductResponse;
import com.nepapp.doineedit.models.SuccessResponse;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DashboardActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private String token;
    private ConstraintLayout dashboardContainerLayout;
    private TextView textUserFullname;
    private ImageView imgProduct;
    private RecyclerView recyclerViewProducts;
    private RecyclerViewAdapter recyclerViewAdapter;
    private CircularProgressIndicator progressIndicator;
    private MaterialToolbar topAppBar;
    private List<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sharedPreferences = getSharedPreferences("LoginSharedPreference", Context.MODE_PRIVATE);
        textUserFullname = findViewById(R.id.textUserFullname);
        dashboardContainerLayout = findViewById(R.id.dashboardContainerLayout);

        topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);
        getSupportActionBar().setTitle(null);

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        progressIndicator = findViewById(R.id.progressIndicator);
        progressIndicator.setVisibility(View.VISIBLE);

        recyclerViewProducts = findViewById(R.id.recycleViewProducts);
        recyclerViewProducts.setHasFixedSize(true);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));

        imgProduct = findViewById(R.id.imgProduct);

        token = sharedPreferences.getString("access-token", "");

        if (token.isEmpty()) {
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            Toast.makeText(DashboardActivity.this, "Login authentication required", Toast.LENGTH_SHORT).show();
        }

        try {
            JSONObject payload = JWTUtils.decoded(token);
            String fullname = payload.getString("fullname");
            String email = payload.getString("email");
            textUserFullname.setText(fullname + " 👋");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        String intentType = intent.getType();
        String intentAction = intent.getAction();

        if (intentType != null) {
            if (Intent.ACTION_SEND.equals(intentAction)) {
                if ("text/plain".equals(intentType)) {
                    String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                    if (sharedText != null) {
                        String[] data = sharedText.split("https");
                        String title = data[0];
                        String url = "https" + data[1];
                        Log.e("Log", title + "--------" + url);
                        Intent intentProduct = new Intent(DashboardActivity.this, AddProductActivity.class);
                        intentProduct.putExtra("title", title);
                        intentProduct.putExtra("url", url);
                        startActivity(intentProduct);
                    }
                }
            }
        }
        loadProductList();

    }


    private void loadProductList() {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<ProductResponse> call = apiInterface.getProducts("Bearer " + token);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                progressIndicator.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Log.e("ProductLog", "OnSuccess: " + new Gson().toJson(response.body()));
                    products = new ArrayList<>();
                    products = response.body().getData();
                    recyclerViewAdapter = new RecyclerViewAdapter(DashboardActivity.this, products);
                    recyclerViewProducts.setAdapter(recyclerViewAdapter);

                    new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                        @Override
                        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                            return false;
                        }

                        @Override
                        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                            int position = viewHolder.getAdapterPosition();
                            Product product = products.get(position);
                            String id = product.getId();
                            String title = product.getTitle();
                            String price = product.getPrice().toString();
                            String description = product.getDescription();
                            String url = product.getUrl();
                            boolean purchased = product.getPurchased();

                            switch (direction) {
                                case ItemTouchHelper.LEFT:
                                    Intent intentEdit = new Intent(DashboardActivity.this, EditProductActivity.class);
                                    intentEdit.putExtra("id", id);
                                    intentEdit.putExtra("title", title);
                                    intentEdit.putExtra("price", price);
                                    intentEdit.putExtra("description", description);
                                    intentEdit.putExtra("url", url);
                                    intentEdit.putExtra("purchased", purchased);
                                    startActivity(intentEdit);
                                    recyclerViewAdapter.notifyItemChanged(position);
                                    break;
                                case ItemTouchHelper.RIGHT:
                                    removeItem(id, position);
                                    products.remove(viewHolder.getAdapterPosition());
                                    recyclerViewAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                                    break;
                            }

                        }

                        @Override
                        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                                    .addSwipeRightBackgroundColor(ContextCompat.getColor(DashboardActivity.this, R.color.red))
                                    .addSwipeRightActionIcon(R.drawable.ic_delete)
                                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(DashboardActivity.this, android.R.color.holo_blue_dark))
                                    .addSwipeLeftActionIcon(R.drawable.ic_edit)
                                    .setActionIconTint(ContextCompat.getColor(DashboardActivity.this, R.color.white))
                                    .create()
                                    .decorate();

                            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        }


                    }).attachToRecyclerView(recyclerViewProducts);


                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                progressIndicator.setVisibility(View.GONE);
                Toast.makeText(DashboardActivity.this, "Something went wrong. Try again!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void removeItem(String id, int position) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<SuccessResponse> call = apiInterface.deleteProduct("Bearer " + token, id);
        call.enqueue(new Callback<SuccessResponse>() {
            @Override
            public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
                if (response.isSuccessful()) {
                    Log.e("OnSuccessDelete: ", "OnSuccess: " + new Gson().toJson(response.body()));
                    Toast.makeText(DashboardActivity.this, "Product deleted successfully!",
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
                        Toast.makeText(DashboardActivity.this, jObjError.getString("message"),
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(DashboardActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SuccessResponse> call, Throwable t) {
                Log.e("OnErrorDelete", t.getMessage());
                Toast.makeText(DashboardActivity.this, "Error in deletion", Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_menu, menu);

        if (menu instanceof MenuBuilder) {
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

                Intent intentLogout = new Intent(DashboardActivity.this, MainActivity.class);
                intentLogout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentLogout);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void btnNavigateAddProduct(View view) {
        overridePendingTransition(0, 0);
        startActivity(new Intent(DashboardActivity.this, AddProductActivity.class));
        overridePendingTransition(0, 0);
    }

    public void btnNavigateDashboard(View view) {
//        startActivity(new Intent(DashboardActivity.this, DashboardActivity.class));
    }

    public void btnNavigateAppInfo(View view) {
        overridePendingTransition(0, 0);
        startActivity(new Intent(DashboardActivity.this, AppInfoActivity.class));
        overridePendingTransition(0, 0);
    }

    public void btnNavigateAccount(View view) {
        overridePendingTransition(0, 0);
        startActivity(new Intent(DashboardActivity.this, AccountActivity.class));
        overridePendingTransition(0, 0);
    }

    public void btnExitApp(View view) {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

}