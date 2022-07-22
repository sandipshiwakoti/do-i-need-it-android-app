package com.nepapp.doineedit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.nepapp.doineedit.api.ApiInterface;
import com.nepapp.doineedit.api.RetrofitClient;
import com.nepapp.doineedit.models.SuccessResponse;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextInputEditText textSearchLocation;
    private Button btnSearchLocation;
    private FloatingActionButton btnSaveLocation;
    private LatLng latLng;
    private String address;
    private String token;
    private SupportMapFragment mapFragment;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_map);

        textSearchLocation = findViewById(R.id.textSearchLocation);
        btnSearchLocation = findViewById(R.id.btnSearchLocation);
        btnSaveLocation = findViewById(R.id.btnSaveLocation);
//        String address = textSearchLocation.getText().toString();

        sharedPreferences = getSharedPreferences("LoginSharedPreference", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("access-token", "");
        if (token.isEmpty()) {
            startActivity(new Intent(ProductMapActivity.this, MainActivity.class));
            Toast.makeText(ProductMapActivity.this, "Login authentication required", Toast.LENGTH_SHORT).show();
        }


        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String location = intent.getStringExtra("location");

        latLng = new LatLng(27.7172453, 85.3239605);
        address = "Store";

        Log.e("loc", id + "---" + location);
        if (location != null) {
//            String location = "27.7172453, 85.3239605";
            double latitude = Double.parseDouble(location.split(",")[0].trim());
            double longitude = Double.parseDouble(location.split(",")[1].trim());
            latLng = new LatLng(latitude, longitude);
            address = "Store";
        }

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.mapContainer, mapFragment)
                .commit();


        mapFragment.getMapAsync(this);

        btnSaveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String latitude = String.valueOf(latLng.latitude);
                String longitude = String.valueOf(latLng.longitude);
                String newLocation = latitude + ", " + longitude;
                Log.e("Check", latitude + "----" + longitude);

                ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
                Call<SuccessResponse> call = apiInterface.updateLocation("Bearer " + token, newLocation, id);
                call.enqueue(new Callback<SuccessResponse>() {
                    @Override
                    public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
                        if (response.isSuccessful()) {
                            Log.e("LocationLog: ", "OnSuccess: " + new Gson().toJson(response.body()));
                            Intent intent = new Intent(ProductMapActivity.this, DashboardActivity.class);
                            finish();
                            startActivity(intent);
                            Toast.makeText(ProductMapActivity.this, "Store location saved!",
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
                                Toast.makeText(ProductMapActivity.this, jObjError.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(ProductMapActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SuccessResponse> call, Throwable t) {
                        Log.e("OnErrorMark", t.getMessage());
                        Toast.makeText(ProductMapActivity.this, "Error in saving store location", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        btnSearchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                address = textSearchLocation.getText().toString();
                List<Address> addressList = null;

                Geocoder geocoder = new Geocoder(ProductMapActivity.this);

                try {
                    addressList = geocoder.getFromLocationName(address, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addressList.size() != 0) {
                    Address searchAddress = addressList.get(0);
                    latLng = new LatLng(searchAddress.getLatitude(), searchAddress.getLongitude());
                    mapFragment.getMapAsync(ProductMapActivity.this);
                } else {
                    Toast.makeText(ProductMapActivity.this, "Address not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            Log.e("Log", address + "-" + latLng.toString());

            googleMap.clear();
            googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(address)
                    .snippet("Product is here!!!!")
            );
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(16f));

            googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    latLng = googleMap.getCameraPosition().target;
                    Geocoder geocoder = new Geocoder(ProductMapActivity.this);
                    List<Address> addressList = null;
                    try {
                        addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        address = addressList.get(0).toString();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (googleMap != null) {
                        googleMap.clear();
                    }
                    googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(address)
                            .snippet("Product is here!!!!"));
                }
            });
        }
    }

}