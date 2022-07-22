package com.nepapp.doineedit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.nepapp.doineedit.models.UserResponse;

import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private CircularProgressIndicator progressIndicator;
    private TextView textEmail;
    private TextView textPassword;
    private TextView textConfirmPassword;
    private TextView textFullname;
    private TextView textMobileNumber;
    private TextInputLayout textEmailLayout, textPasswordLayout, textConfirmPasswordLayout, textFullnameLayout, textMobileNumberLayout;
    public static final String LOG_TAG = "RegisterLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressIndicator = findViewById(R.id.progressIndicator);
        textEmailLayout = findViewById(R.id.textEmailLayout);
        textPasswordLayout= findViewById(R.id.textPasswordLayout);
        textConfirmPasswordLayout = findViewById(R.id.textConfirmPasswordLayout);
        textFullnameLayout = findViewById(R.id.textFullnameLayout);
        textMobileNumberLayout = findViewById(R.id.textMobileNumberLayout);

        textEmail = findViewById(R.id.textEmail);
        textPassword = findViewById(R.id.textPassword);
        textConfirmPassword = findViewById(R.id.textConfirmPassword);
        textFullname = findViewById(R.id.textFullname);
        textMobileNumber = findViewById(R.id.textMobileNumber);

        Button btnRegister = findViewById(R.id.btnRegister);
        TextView btnNavigateLogin = findViewById(R.id.btnNavigateLogin);

        btnNavigateLogin.setOnClickListener(v->{
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        });
        Log.e(LOG_TAG,"Hello");

        btnRegister.setOnClickListener(v -> {
            progressIndicator.setVisibility(View.VISIBLE);
            if(checkValidation()){
                String email = textEmail.getText().toString();
                String password = textPassword.getText().toString();
                String fullname = textFullname.getText().toString();
                String mobile = textMobileNumber.getText().toString();
                System.out.println(email + password + fullname + mobile);

                ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
                Call<UserResponse> call =  apiInterface.registerUser(email, fullname,
                        mobile, password);
                call.enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        progressIndicator.setVisibility(View.GONE);
                        if(response.isSuccessful()){
                            Log.e(LOG_TAG,"OnSuccess: " + new Gson().toJson(response.body()));
                            Toast.makeText(RegisterActivity.this, "Registered successfully!", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(RegisterActivity.this,jObjError.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }
                            catch (Exception e) {
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        progressIndicator.setVisibility(View.GONE);
                        Log.e(LOG_TAG,t.getMessage());
                        Toast.makeText(RegisterActivity.this, "Error in registration", Toast.LENGTH_LONG).show();
                    }
                });
            }
            else {
                progressIndicator.setVisibility(View.GONE);
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean checkValidation(){
        boolean isValid = true;

        textEmailLayout = findViewById(R.id.textEmailLayout);
        textPasswordLayout= findViewById(R.id.textPasswordLayout);
        textConfirmPasswordLayout = findViewById(R.id.textConfirmPasswordLayout);
        textFullnameLayout = findViewById(R.id.textFullnameLayout);
        textMobileNumberLayout = findViewById(R.id.textMobileNumberLayout);

        textEmail = findViewById(R.id.textEmail);
        textPassword = findViewById(R.id.textPassword);
        textConfirmPassword = findViewById(R.id.textConfirmPassword);
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

        if(textConfirmPassword.length() == 0){
            textConfirmPasswordLayout.setError(getString(R.string.textBlankConfirmPassword));
            isValid = false;
        }
        else if(textConfirmPassword.length() < 8){
            textConfirmPasswordLayout.setError(getString(R.string.textLengthErrorPassword));
            isValid = false;
        }
        else if(!textConfirmPassword.getText().toString().trim().equals(textPassword.getText().toString().trim())){
            textConfirmPasswordLayout.setError(getString(R.string.textUnmatchPassword));
            isValid = false;
        }
        else {
            textConfirmPasswordLayout.setError(null);
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