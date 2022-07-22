package com.nepapp.doineedit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.nepapp.doineedit.api.ApiInterface;
import com.nepapp.doineedit.api.RetrofitClient;
import com.nepapp.doineedit.models.SuccessResponse;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProductActivity extends AppCompatActivity {
    private MaterialToolbar topAppBar;
    private TextView textProductTitle;
    private TextInputLayout textProductTitleLayout;
    private TextView textProductPrice;
    private TextView textProductDescription;
    private TextInputLayout textProductDescriptionLayout;
    private TextInputLayout textProductPriceLayout;
    private TextView textProductUrl;
    private TextInputLayout textProductUrlLayout;
    private CheckBox cbProductPurchased;
    private Button btnEditProduct;
    private Button btnChooseProductImage;
    private ImageView imgProductPreview;
    private CircularProgressIndicator progressIndicator;
    private SharedPreferences sharedPreferences;
    private String token;
    private String imageFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        topAppBar = findViewById(R.id.topAppBar);
        btnEditProduct = findViewById(R.id.btnEditProduct);
        btnChooseProductImage = findViewById(R.id.btnChooseProductImage);
        imgProductPreview = findViewById(R.id.imgProductPreview);
        textProductTitle = findViewById(R.id.textProductTitle);
        textProductPrice = findViewById(R.id.textProductPrice);
        textProductDescription = findViewById(R.id.textProductDescription);
        textProductUrl = findViewById(R.id.textProductUrl);
        cbProductPurchased = findViewById(R.id.cbProductPurchased);
        textProductTitleLayout = findViewById(R.id.textProductTitleLayout);
        textProductPriceLayout = findViewById(R.id.textProductPriceLayout);
        textProductDescriptionLayout = findViewById(R.id.textProductDescriptionLayout);
        textProductUrlLayout = findViewById(R.id.textProductUrlLayout);
        progressIndicator = findViewById(R.id.progressIndicator);

        sharedPreferences = getSharedPreferences("LoginSharedPreference", Context.MODE_PRIVATE);

        token = sharedPreferences.getString("access-token", "");

        if (token.isEmpty()) {
            startActivity(new Intent(EditProductActivity.this, MainActivity.class));
            Toast.makeText(EditProductActivity.this, "Login authentication required", Toast.LENGTH_SHORT).show();
        }

//        topAppBar.setOverflowIcon(getDrawable(R.drawable.ic_vertical_bar));
        setSupportActionBar(topAppBar);
        getSupportActionBar().setTitle(null);

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgProductPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(EditProductActivity.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

        btnChooseProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(EditProductActivity.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String title = intent.getStringExtra("title");
        String price = intent.getStringExtra("price");
        String description = intent.getStringExtra("description");
        String url = intent.getStringExtra("url");
        boolean purchased = intent.getExtras().getBoolean("purchased");

        textProductTitle.setText(title);
        textProductPrice.setText(price);
        textProductDescription.setText(description);
        cbProductPurchased.setChecked(purchased);
        textProductUrl.setText(url);

        btnEditProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidation()) {
                    String title = textProductTitle.getText().toString();
                    String price = textProductPrice.getText().toString();
                    String description = textProductDescription.getText().toString();
                    String purchased = Boolean.toString(cbProductPurchased.isChecked());
                    progressIndicator.setVisibility(View.VISIBLE);

                    String url = textProductUrl.getText().toString();

                    RequestBody titleRequest =
                            RequestBody.create(MediaType.parse("multipart/form-data"), title);
                    RequestBody priceRequest =
                            RequestBody.create(MediaType.parse("multipart/form-data"), price);
                    RequestBody urlRequest =
                            RequestBody.create(MediaType.parse("multipart/form-data"), url);
                    RequestBody descriptionRequest =
                            RequestBody.create(MediaType.parse("multipart/form-data"), description);
                    RequestBody purchasedRequest =
                            RequestBody.create(MediaType.parse("multipart/form-data"), purchased);


                    MultipartBody.Part imageRequest = null;

                    if(imageFilePath!=null){
                        File file = new File(imageFilePath);
                        RequestBody requestFile =
                                RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        imageRequest = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                    }

                    ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
                    Call<SuccessResponse> call = apiInterface.editProduct("Bearer " + token, titleRequest, priceRequest, descriptionRequest, urlRequest, purchasedRequest, imageRequest, id);
                    call.enqueue(new Callback<SuccessResponse>() {
                        @Override
                        public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
                            progressIndicator.setVisibility(View.GONE);
                            if (response.isSuccessful()) {
                                Log.e("EditProductLog", "OnSuccess: " + new Gson().toJson(response.body()));

                                Intent intent = new Intent(EditProductActivity.this, DashboardActivity.class);
                                finish();
                                startActivity(intent);
                                Toast.makeText(EditProductActivity.this, "Product edited successfully!",
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
                                    Log.e("EditErrorResponseLog", jObjError.getString("message"));
                                    Toast.makeText(EditProductActivity.this, jObjError.getString("message"),
                                            Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(EditProductActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SuccessResponse> call, Throwable t) {
                            progressIndicator.setVisibility(View.GONE);
                            Log.e("EditProductLog", t.getMessage());
                            Toast.makeText(EditProductActivity.this, "Error in edit", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(EditProductActivity.this, "Please validate your product info!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        imageFilePath = uri.getPath();
        imgProductPreview.setImageURI(uri);
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

                Intent intentLogout = new Intent(EditProductActivity.this, MainActivity.class);
                intentLogout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentLogout);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isValidURL(String url) {
        if (!url.startsWith("https://")) {
            url = "https://" + url;
            textProductUrl.setText(url);
            return true;
        }

        String regex = "((http|https)://)(www.)?"
                + "[a-zA-Z0-9@:%._\\+~#?&//=]"
                + "{2,256}\\.[a-z]"
                + "{2,6}\\b([-a-zA-Z0-9@:%"
                + "._\\+~#?&//=]*)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(url);
        return m.matches();
    }

    private boolean checkValidation() {
        boolean isValid = true;

        if (textProductTitle.length() == 0) {
            textProductTitleLayout.setError(getString(R.string.textBlankProductTitle));
            isValid = false;
        } else {
            textProductTitle.setError(null);
        }

        if (textProductPrice.length() == 0) {
            textProductPriceLayout.setError(getString(R.string.textBlankProductPrice));
            isValid = false;
        } else {
            textProductPrice.setError(null);
        }

        if (textProductDescription.length() == 0) {
            textProductDescriptionLayout.setError(getString(R.string.textBlankProductDescription));
            isValid = false;
        } else {
            textProductDescription.setError(null);
        }

        if (textProductUrl.length() == 0) {
            textProductUrlLayout.setError(getString(R.string.textBlankProductUrl));
            isValid = false;
        } else if (!isValidURL(textProductUrl.getText().toString())) {
            textProductUrlLayout.setError(getString(R.string.textInvalidProductUrl));
            isValid = false;
        } else {
            textProductUrl.setError(null);
        }

        return isValid;
    }

    public void btnNavigateAddProduct(View view) {
        overridePendingTransition(0, 0);
        startActivity(new Intent(EditProductActivity.this, AddProductActivity.class));
        overridePendingTransition(0, 0);
    }

    public void btnNavigateDashboard(View view) {
        overridePendingTransition(0, 0);
        finish();
        startActivity(new Intent(EditProductActivity.this, DashboardActivity.class));
        overridePendingTransition(0, 0);
    }
    public void btnNavigateAppInfo(View view) {
        overridePendingTransition(0, 0);
        startActivity(new Intent(EditProductActivity.this, AppInfoActivity.class));
        overridePendingTransition(0, 0);
    }

    public void btnNavigateAccount(View view) {
        overridePendingTransition(0, 0);
        startActivity(new Intent(EditProductActivity.this, AccountActivity.class));
        overridePendingTransition(0, 0);
    }

    public void btnExitApp(View view) {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

}