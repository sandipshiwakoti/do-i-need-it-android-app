package com.nepapp.doineedit.api;

import com.nepapp.doineedit.models.LoginResponse;
import com.nepapp.doineedit.models.ProductResponse;
import com.nepapp.doineedit.models.SuccessResponse;
import com.nepapp.doineedit.models.User;
import com.nepapp.doineedit.models.UserResponse;
import com.nepapp.doineedit.models.UserSuccessResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiInterface {

    @FormUrlEncoded
   @POST("auth/register")
    Call<UserResponse> registerUser(@Field("email") String email,
                                    @Field("fullname") String fullname,
                                    @Field("mobile") String mobile,
                                    @Field("password") String password);

    @FormUrlEncoded
    @POST("auth/login")
    Call<LoginResponse> loginUser(@Field("email") String email,
                                  @Field("password") String password);

    @GET("products")
    Call<ProductResponse> getProducts(@Header("Authorization") String authHeader);

    @Multipart
    @POST("products")
    Call<SuccessResponse> addProduct(@Header("Authorization") String authHeader,
                                     @Part("title") RequestBody title,
                                     @Part("price") RequestBody price,
                                     @Part("description") RequestBody description,
                                     @Part("url") RequestBody url,
                                     @Part("purchased") RequestBody purchased,
                                     @Part MultipartBody.Part image);


    @Multipart
    @PUT("products/{id}")
    Call<SuccessResponse> editProduct(@Header("Authorization") String authHeader,
                                     @Part("title") RequestBody title,
                                     @Part("price") RequestBody price,
                                     @Part("description") RequestBody description,
                                     @Part("url") RequestBody url,
                                     @Part("purchased") RequestBody purchased,
                                     @Part MultipartBody.Part image,
                                     @Path("id") String id);

    @DELETE("products/{id}")
    Call<SuccessResponse> deleteProduct(@Header("Authorization") String authHeader,
                                        @Path("id") String id);

    @FormUrlEncoded
    @PUT("products/{id}/purchased")
    Call<SuccessResponse> markProductPurchased(@Header("Authorization") String authHeader,
                                               @Field("purchased") String purchased,
                                               @Path("id") String id);

    @FormUrlEncoded
    @PUT("products/{id}/location")
    Call<SuccessResponse> updateLocation(@Header("Authorization") String authHeader,
                                               @Field("location") String location,
                                               @Path("id") String id);

    @FormUrlEncoded
    @PUT("users/updateAccount")
    Call<LoginResponse> updateAccountInfo(@Header("Authorization") String authHeader,
                                         @Field("fullname") String fullname,
                                         @Field("mobile") String mobile,
                                         @Field("email") String email);

    @FormUrlEncoded
    @PUT("users/updatePassword")
    Call<LoginResponse> updateAccountPassword(@Header("Authorization") String authHeader,
                                         @Field("currentPassword") String currentPassword,
                                         @Field("newPassword") String newPassword);

    //Legacy requests
   @FormUrlEncoded
   @POST("products")
   Call<SuccessResponse> addProductLegacy(@Header("Authorization") String authHeader,
                                    @Field("title") String title,
                                    @Field("price") String price,
                                    @Field("description") String description,
                                    @Field("url") String url,
                                    @Field("purchased") String purchased);

   @FormUrlEncoded
   @PUT("products/{id}")
   Call<SuccessResponse> editProductLegacy(@Header("Authorization") String authHeader,
                                     @Field("title") String title,
                                     @Field("price") String price,
                                     @Field("description") String description,
                                     @Field("url") String url,
                                     @Field("purchased") String purchased,
                                     @Path("id") String id);
}
