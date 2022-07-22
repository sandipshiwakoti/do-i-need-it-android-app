package com.nepapp.doineedit.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static Retrofit retrofit;
    private static final String baseURL = "https://doineedit-app.herokuapp.com/api/v1/";
    private static final String localBaseURL ="http://192.168.1.3:5000/ap1/v1/";

    public static Retrofit getRetrofitInstance(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
