package com.nepapp.doineedit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.nepapp.doineedit.api.ApiInterface;
import com.nepapp.doineedit.api.RetrofitClient;
import com.nepapp.doineedit.models.LoginResponse;

import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends AppCompatActivity {
    private TextView textFullname;
    private TextView textEmail;
    private TextView textMobileNumber;
    private TextView textCurrentPassword;
    private TextView textNewPassword;
    private TextInputLayout textFullnameLayout;
    private TextInputLayout textEmailLayout;
    private TextInputLayout textMobileNumberLayout;
    private TextInputLayout textCurrentPasswordLayout;
    private TextInputLayout textNewPasswordLayout;
    private Button btnUpdateAccountInfo;
    private Button btnUpdateAccountPassword;
    private CircularProgressIndicator progressIndicatorAccountInfo;
    private CircularProgressIndicator progressIndicatorAccountPassword;
    private MaterialToolbar topAppBar;
    private SharedPreferences sharedPreferences;
    private String token;
    private String userFullname;
    private String userEmail;
    private String userMobile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        textFullname = findViewById(R.id.textFullname);
        textEmail = findViewById(R.id.textEmail);
        textMobileNumber = findViewById(R.id.textMobileNumber);
        textCurrentPassword = findViewById(R.id.textCurrentPassword);
        textNewPassword = findViewById(R.id.textNewPassword);

        textFullnameLayout = findViewById(R.id.textFullnameLayout);
        textEmailLayout = findViewById(R.id.textEmailLayout);
        textMobileNumberLayout = findViewById(R.id.textMobileNumberLayout);
        textCurrentPasswordLayout = findViewById(R.id.textCurrentPasswordLayout);
        textNewPasswordLayout = findViewById(R.id.textNewPasswordLayout);

        btnUpdateAccountInfo = findViewById(R.id.btnUpdateAccountInfo);
        btnUpdateAccountPassword = findViewById(R.id.btnUpdateAccountPassword);
        progressIndicatorAccountInfo = findViewById(R.id.progressIndicatorAccountInfo);
        progressIndicatorAccountPassword = findViewById(R.id.progressIndicatorAccountPassword);

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
            startActivity(new Intent(AccountActivity.this, MainActivity.class));
            Toast.makeText(AccountActivity.this, "Login authentication required", Toast.LENGTH_SHORT).show();
        }

        try {
            JSONObject payload = JWTUtils.decoded(token);
            userFullname = payload.getString("fullname");
            userEmail = payload.getString("email");
            userMobile = payload.getString("mobile");
            textFullname.setText(userFullname);
            textMobileNumber.setText(userMobile);
            textEmail.setText(userEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnUpdateAccountInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInfoValidation()) {
                    String fullname = textFullname.getText().toString().trim();
                    String mobile = textMobileNumber.getText().toString().trim();
                    String email = textEmail.getText().toString().trim();
                    String authHeader = "Bearer " + token;

                    progressIndicatorAccountInfo.setVisibility(View.VISIBLE);

                    ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
                    Call<LoginResponse> call = apiInterface.updateAccountInfo(authHeader, fullname, mobile,
                            email);
                    call.enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            progressIndicatorAccountInfo.setVisibility(View.GONE);
                            if (response.isSuccessful()) {
                                Log.e("LOG_TAG", "OnSuccess: " + new Gson().toJson(response.body()));
                                Toast.makeText(AccountActivity.this, "Account info updated successfully!", Toast.LENGTH_LONG).show();

                                String newToken = response.body().getData();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("access-token", newToken);
                                editor.commit();

                            } else {
                                String data = null;
                                try {
                                    data = response.errorBody().string();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    JSONObject jObjError = new JSONObject(data);
                                    Toast.makeText(AccountActivity.this, jObjError.getString("message"),
                                            Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(AccountActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            progressIndicatorAccountInfo.setVisibility(View.GONE);
                            Log.e("LOG_TAG", t.getMessage());
                            Toast.makeText(AccountActivity.this, "Error in updation", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        btnUpdateAccountPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPasswordValidation()) {
                    progressIndicatorAccountPassword.setVisibility(View.VISIBLE);
                    String currentPassword = textCurrentPassword.getText().toString();
                    String newPassword = textNewPassword.getText().toString();
                    String authHeader = "Bearer " + token;

                    ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
                    Call<LoginResponse> call = apiInterface.updateAccountPassword(authHeader, currentPassword, newPassword);
                    call.enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            progressIndicatorAccountPassword.setVisibility(View.GONE);
                            if (response.isSuccessful()) {
                                Log.e("LOG_TAG", "OnSuccess: " + new Gson().toJson(response.body()));
                                Toast.makeText(AccountActivity.this, "Password changed! You need to login again.",
                                        Toast.LENGTH_LONG).show();
                                sharedPreferences.edit().remove("access-token").commit();
                                Intent intentLogout = new Intent(AccountActivity.this, MainActivity.class);
                                intentLogout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intentLogout);
                            } else {
                                String data = null;
                                try {
                                    data = response.errorBody().string();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    JSONObject jObjError = new JSONObject(data);
                                    Toast.makeText(AccountActivity.this, jObjError.getString("message"),
                                            Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(AccountActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            progressIndicatorAccountPassword.setVisibility(View.GONE);
                            Log.e("LOG_TAG", t.getMessage());
                            Toast.makeText(AccountActivity.this, "Error in updation", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

    }

    public void btnNavigateAddProduct(View view) {
        overridePendingTransition(0, 0);
        startActivity(new Intent(AccountActivity.this, AddProductActivity.class));
        overridePendingTransition(0, 0);
    }

    public void btnNavigateDashboard(View view) {
        overridePendingTransition(0, 0);
        finish();
        startActivity(new Intent(AccountActivity.this, DashboardActivity.class));
        overridePendingTransition(0, 0);
    }

    public void btnNavigateAppInfo(View view) {
        overridePendingTransition(0, 0);
        startActivity(new Intent(AccountActivity.this, AppInfoActivity.class));
        overridePendingTransition(0, 0);
    }

    public void btnNavigateAccount(View view) {
//        startActivity(new Intent(AccountActivity.this, AccountActivity.class));
    }

    public void btnExitApp(View view) {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
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

                Intent intentLogout = new Intent(AccountActivity.this, MainActivity.class);
                intentLogout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentLogout);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkPasswordValidation(){
        boolean isValid = true;

        textCurrentPassword = findViewById(R.id.textCurrentPassword);
        textNewPassword = findViewById(R.id.textNewPassword);

        if(textCurrentPassword.length() == 0){
            textCurrentPasswordLayout.setError(getString(R.string.textBlankCurrentPassword));
            isValid = false;
        }
        else if(textCurrentPassword.length() < 8){
            textCurrentPasswordLayout.setError(getString(R.string.textLengthErrorPassword));
            isValid = false;
        }
        else {
            textCurrentPasswordLayout.setError(null);
        }

        if(textNewPassword.length() == 0){
            textNewPasswordLayout.setError(getString(R.string.textBlankNewPassword));
            isValid = false;
        }
        else if(textNewPassword.length() < 8){
            textNewPasswordLayout.setError(getString(R.string.textLengthErrorPassword));
            isValid = false;
        }

        else if(textNewPassword.getText().toString().trim().equals(textCurrentPassword.getText().toString().trim())){
            textNewPasswordLayout.setError(getString(R.string.textSameUpdatePassword));
            isValid = false;
        }
        else {
            textNewPasswordLayout.setError(null);
        }

        return isValid;
    }

    private boolean checkInfoValidation(){
        boolean isValid = true;

        textEmail = findViewById(R.id.textEmail);
        textFullname = findViewById(R.id.textFullname);
        textMobileNumber = findViewById(R.id.textMobileNumber);

        if(textEmail.length() == 0){
            textEmailLayout.setError(getString(R.string.textBlankEmailAddress));
            isValid = false;
        }
        else if(!isValidEmail(textEmail.getText().toString())){
            textEmailLayout.setError(getString(R.string.textInvalidEmailAddress));
            isValid = false;
        }
        else {
            textEmailLayout.setError(null);
        }


        if(textFullname.length() == 0){
            textFullnameLayout.setError(getString(R.string.textBlankFullname));
            isValid = false;
        }
        else {
            textFullnameLayout.setError(null);
        }

        if(textMobileNumber.length() == 0){
            textMobileNumberLayout.setError(getString(R.string.textBlankMobileNumber));
            isValid = false;
        }
        else if(!isValidMobileNumber(textMobileNumber.getText().toString())){
            textMobileNumberLayout.setError(getString(R.string.textInvalidMobileNumber));
            isValid = false;
        }
        else {
            textMobileNumberLayout.setError(null);
        }
        return isValid;
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private boolean isValidMobileNumber(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.PHONE.matcher(target).matches());
    }
}