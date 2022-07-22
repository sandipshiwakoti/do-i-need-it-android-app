package com.nepapp.doineedit.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.nepapp.doineedit.DashboardActivity;
import com.nepapp.doineedit.ProductActivity;
import com.nepapp.doineedit.R;
import com.nepapp.doineedit.models.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Product> productList;

    public RecyclerViewAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_card_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.textProductTitle.setText(product.getTitle());
        holder.textProductPrice.setText("NPR " + product.getPrice().toString());

        String image = product.getImage();
        if(image == null){
            image = "https://res.cloudinary.com/nepal-cloud/image/upload/v1644651363/doineedit/noimage_mpo6ko.jpg";
        }
        Picasso.get().load(image).into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener{
        public TextView textProductTitle;
        public TextView textProductPrice;
        public ImageView imgProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            textProductTitle = itemView.findViewById(R.id.textProductTitle);
            textProductPrice = itemView.findViewById(R.id.textProductPrice);
            imgProduct = itemView.findViewById(R.id.imgProduct);

        }

        @Override
        public void onClick(View view) {
            Log.d("ViewDetailCLick: ", "Clicked");
            int position = this.getAdapterPosition();
            Product product = productList.get(position);
            String id = product.getId();
            String title = product.getTitle();
            String price = product.getPrice().toString();
            String description = product.getDescription();
            String url = product.getUrl();
            boolean purchased = product.getPurchased();
            String location = product.getLocation();

            String image = product.getImage();
            if(image == null){
                image = "https://res.cloudinary.com/nepal-cloud/image/upload/v1644651363/doineedit/noimage_mpo6ko.jpg";
            }


            Intent intent = new Intent(view.getContext(), ProductActivity.class);
            intent.setAction("");
            intent.putExtra("id", id);
            intent.putExtra("title", title);
            intent.putExtra("price", price);
            intent.putExtra("description", description);
            intent.putExtra("url", url);
            intent.putExtra("purchased", purchased);
            intent.putExtra("image", image);
            intent.putExtra("location", location);
            context.startActivity(intent);
        }
    }
}
