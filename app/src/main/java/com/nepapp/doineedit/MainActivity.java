package com.nepapp.doineedit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedpreferences;
    private CircularProgressIndicator progressIndicator;
    private TextView textEmail;
    private TextView textPassword;
    private TextInputLayout textEmailLayout, textPasswordLayout;
    public static final String LOG_TAG = "LoginLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences("LoginSharedPreference", Context.MODE_PRIVATE);
        progressIndicator = findViewById(R.id.progressIndicator);
        textEmailLayout = findViewById(R.id.textEmailLayout);
        textPasswordLayout= findViewById(R.id.textPasswordLayout);
        textEmail = findViewById(R.id.textEmail);
        textPassword = findViewById(R.id.textPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView btnNavigateRegister = findViewById(R.id.btnNavigateRegister);

        String token = sharedpreferences.getString("access-token", "");


        if (!token.isEmpty()) {
            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
            Toast.makeText(MainActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
        }

        btnNavigateRegister.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            if(checkValidation()){
                String email = textEmail.getText().toString();
                String password = textPassword.getText().toString();
                progressIndicator.setVisibility(View.VISIBLE);

                ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
                Call<LoginResponse> call =  apiInterface.loginUser(email, password);
                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        progressIndicator.setVisibility(View.GONE);
                        if(response.isSuccessful()){
                            Log.e(LOG_TAG,"OnSuccess: " + new Gson().toJson(response.body()));
                            String token = response.body().getData();
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("access-token", token);
                            editor.commit();
                            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                            finish();
                            startActivity(intent);
                            Toast.makeText(MainActivity.this, "Login successful!",
                                    Toast.LENGTH_LONG).show();
                        }
                        else {
                            String data= null;
                            try {
                                data = response.errorBody().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                JSONObject jObjError = new JSONObject(data);
                                Toast.makeText(MainActivity.this,jObjError.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }
                            catch (Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        progressIndicator.setVisibility(View.GONE);
                        Log.e(LOG_TAG,t.getMessage());
                        Toast.makeText(MainActivity.this, "Error in login", Toast.LENGTH_LONG).show();
                    }
                });
            }
            else {
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            }
        });


    }

    private boolean checkValidation(){
        boolean isValid = true;

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

        if(textPassword.length() == 0){
            textPasswordLayout.setError(getString(R.string.textBlankPassword));
            isValid = false;
        }
        else if(textPassword.length() < 8){
            textPasswordLayout.setError(getString(R.string.textLengthErrorPassword));
            isValid = false;
        }
        else {
            textPasswordLayout.setError(null);
        }

        return isValid;
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

}